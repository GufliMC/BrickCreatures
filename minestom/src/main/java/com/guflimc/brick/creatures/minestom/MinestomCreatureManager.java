package com.guflimc.brick.creatures.minestom;

import com.guflimc.brick.creatures.api.CreatureManager;
import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.api.domain.PersistentSpawn;
import com.guflimc.brick.creatures.api.meta.Position;
import com.guflimc.brick.creatures.common.BrickDatabaseContext;
import com.guflimc.brick.creatures.common.domain.DCreature;
import com.guflimc.brick.creatures.common.domain.DSpawn;
import com.guflimc.brick.creatures.minestom.creature.MinestomCreature;
import com.guflimc.brick.creatures.minestom.creature.player.FakeFakePlayer;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class MinestomCreatureManager implements CreatureManager<Instance, EntityCreature, EntityType> {

    private final BrickDatabaseContext databaseContext;

    private final Set<PersistentCreature> persistentCreatures = new CopyOnWriteArraySet<>();
    private final Set<PersistentSpawn> persistentSpawns = new CopyOnWriteArraySet<>();

    private final Set<MinestomCreature> spawnedCreatures = new CopyOnWriteArraySet<>();

    public MinestomCreatureManager(BrickDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;

        databaseContext.queryBuilder((session, cb) -> {
            // creatures
            CriteriaQuery<DCreature> creatureQuery = cb.createQuery(DCreature.class);
            Root<DCreature> creatureRoot = creatureQuery.from(DCreature.class);
            creatureQuery = creatureQuery.select(creatureRoot);

            TypedQuery<DCreature> creatureAllQuery = session.createQuery(creatureQuery);
            persistentCreatures.addAll(creatureAllQuery.getResultList());

            // spawns
            CriteriaQuery<DSpawn> spawnQuery = cb.createQuery(DSpawn.class);
            Root<DSpawn> spawnRoot = spawnQuery.from(DSpawn.class);
            spawnQuery = spawnQuery.select(spawnRoot);

            TypedQuery<DSpawn> spawnAllQuery = session.createQuery(spawnQuery);
            persistentSpawns.addAll(spawnAllQuery.getResultList());
            persistentSpawns.forEach(persistentSpawn -> spawnPersistedCreature(persistentSpawn, defaultInstance()));
        });
    }

    private Instance defaultInstance() {
        return MinecraftServer.getInstanceManager().getInstances().stream().findFirst().orElseThrow();
    }

    public Pos position(Position position) {
        return new Pos(position.x(), position.y(), position.z(), position.yaw(), position.pitch());
    }

    public Position position(Pos pos) {
        return new Position(pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch());
    }

    // SPAWNS

    private MinestomCreature spawnPersistedCreature(@NotNull PersistentSpawn spawn, @NotNull Instance instance) {
        MinestomCreature creature = spawn(spawn.position(), instance, EntityType.fromNamespaceId(spawn.creature().type()),
                spawn.creature(), spawn);
        spawnedCreatures.add(creature);
        return creature;
    }

    private MinestomCreature spawn(@NotNull Position position, @NotNull Instance instance, @NotNull EntityType type,
                                   @Nullable PersistentCreature creature, @Nullable PersistentSpawn spawn) {
        EntityCreature entity;
        if (type == EntityType.PLAYER) {
            entity = new FakeFakePlayer();
        } else {
            entity = new EntityCreature(type);
        }

        entity.setInstance(instance, position(position)).join(); // TODO not this

        return new MinestomCreature(entity.getUuid(), entity, creature, spawn);
    }

    // SPAWNS

    @Override
    public MinestomCreature spawn(@NotNull Position position, @NotNull Instance instance, @NotNull EntityType type) {
        return spawn(position, instance, type, null, null);
    }

    @Override
    public MinestomCreature spawn(@NotNull Position position, @NotNull Instance instance, @NotNull PersistentCreature creature) {
        return spawn(position, instance, EntityType.fromNamespaceId(creature.type()), creature, null);
    }

    // PERSISTENT CREATURES

    @Override
    public Collection<PersistentCreature> creatures() {
        return Collections.unmodifiableCollection(persistentCreatures);
    }

    @Override
    public Optional<PersistentCreature> creature(@NotNull String id) {
        return persistentCreatures.stream().filter(c -> c.name().equals(id)).findFirst();
    }

    @Override
    public CompletableFuture<PersistentCreature> persist(@NotNull String id, @NotNull Creature<EntityCreature> creature) {
        DCreature dcreature = new DCreature(id, creature.entity().getEntityType().name());
        persistentCreatures.add(dcreature);
        return databaseContext.persistAsync(dcreature).thenApply(v -> dcreature);
    }

    @Override
    public CompletableFuture<PersistentCreature> persist(@NotNull String id, @NotNull EntityType type) {
        DCreature creature = new DCreature(id, type.name());
        persistentCreatures.add(creature);
        return databaseContext.persistAsync(creature).thenApply(v -> creature);
    }

    @Override
    public CompletableFuture<Void> merge(@NotNull PersistentCreature creature) {
        refresh(creature);
        return databaseContext.mergeAsync(creature);
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull PersistentCreature creature) {
        persistentCreatures.remove(creature);
        spawnedCreatures.stream().filter(c -> creature.equals(c.persistentCreature()))
                .toList().forEach(c -> {
                    c.despawn();
                    spawnedCreatures.remove(c);
                });
        return databaseContext.removeAsync(creature);
    }

    @Override
    public void refresh(@NotNull PersistentCreature creature) {
        spawnedCreatures.stream().filter(c -> creature.equals(c.persistentCreature()))
                .forEach(MinestomCreature::refresh);
    }

    // PERSISTENT SPAWNS

    @Override
    public Collection<PersistentSpawn> spawns() {
        return Collections.unmodifiableCollection(persistentSpawns);
    }

    @Override
    public Optional<PersistentSpawn> spawn(@NotNull String id) {
        return persistentSpawns.stream().filter(s -> s.name().equals(id)).findFirst();
    }

    @Override
    public CompletableFuture<PersistentSpawn> persist(@NotNull String id, @NotNull PersistentCreature creature, @NotNull Position position, @NotNull Instance instance) {
        DSpawn spawn = new DSpawn(id, (DCreature) creature, position);
        persistentSpawns.add(spawn);
        spawnPersistedCreature(spawn, instance);
        return databaseContext.persistAsync(spawn).thenApply(v -> spawn);
    }

    @Override
    public CompletableFuture<Void> merge(@NotNull PersistentSpawn spawn) {
        refresh(spawn);
        return databaseContext.mergeAsync(spawn);
    }

    @Override
    public void refresh(@NotNull PersistentSpawn spawn) {
        spawnedCreatures.stream().filter(c -> spawn.equals(c.persistentSpawn()))
                .forEach(MinestomCreature::refresh);
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull PersistentSpawn spawn) {
        persistentSpawns.remove(spawn);
        spawnedCreatures.stream().filter(c -> spawn.equals(c.persistentSpawn())).findFirst()
                .ifPresent(c -> {
                    c.despawn();
                    spawnedCreatures.remove(c);
                });
        return databaseContext.removeAsync(spawn);
    }

}

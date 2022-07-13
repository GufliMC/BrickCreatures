package com.guflimc.brick.creatures.minestom;

import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.api.domain.TraitKey;
import com.guflimc.brick.creatures.api.domain.TraitLifecycle;
import com.guflimc.brick.creatures.common.BrickCreaturesDatabaseContext;
import com.guflimc.brick.creatures.common.domain.DCreature;
import com.guflimc.brick.creatures.minestom.api.MinestomCreatureManager;
import com.guflimc.brick.creatures.minestom.api.domain.MinestomCreature;
import com.guflimc.brick.creatures.minestom.domain.MinestomBrickCreature;
import com.guflimc.brick.worlds.api.world.World;
import com.guflimc.brick.worlds.minestom.api.MinestomWorldAPI;
import com.guflimc.brick.worlds.minestom.api.event.WorldLoadEvent;
import com.guflimc.brick.worlds.minestom.api.world.MinestomWorld;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class MinestomBrickCreatureManager implements MinestomCreatureManager {

    private final Logger logger = LoggerFactory.getLogger(MinestomBrickCreatureManager.class);

    private final BrickCreaturesDatabaseContext databaseContext;

    private final Set<MinestomBrickCreature> creatures = new CopyOnWriteArraySet<>();
    private final Set<TraitKey<MinestomCreature>> traits = new CopyOnWriteArraySet<>();

    public MinestomBrickCreatureManager(BrickCreaturesDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;

        // spawn holograms that are assigned to a world when a world is loaded
        if (MinecraftServer.getExtensionManager().hasExtension("brickworlds")) {
            MinecraftServer.getGlobalEventHandler().addListener(WorldLoadEvent.class, e -> load(e.world()));
        }

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            creatures.forEach(c -> c.traitLifecycles.values().forEach(TraitLifecycle::onTick));
        }, TaskSchedule.tick(1), TaskSchedule.tick(1));
    }

    private void load(World world) {
        logger.info("Loading creatures for world '{}'.", world.info().name());
        creatures.stream().filter(c -> c.instance() == null)
                .filter(c -> c.domainCreature().location() != null && c.domainCreature().location().worldName() != null)
                .filter(c -> c.domainCreature().location().worldName().equals(world.info().name()))
                .forEach(h -> {
                    h.setInstance(((MinestomWorld) world).asInstance());
                });
    }

    @Override
    public void reload() {
        // load holograms from database
        databaseContext.findAllAsync(DCreature.class).join().stream()
                .map(MinestomBrickCreature::new)
                .forEach(crea -> {
                    // remove old one with same id
                    creatures.stream().filter(c -> c.id().equals(crea.id())).toList().forEach(c -> {
                        c.despawn();
                        creatures.remove(c);
                    });

                    creatures.add(crea);
                });

        // set instance of holograms that are assigned to a world
        if (MinecraftServer.getExtensionManager().hasExtension("brickworlds")) {
            MinestomWorldAPI.get().loadedWorlds().forEach(this::load);
        }
    }

    @Override
    public Collection<Creature> creatures() {
        return Collections.unmodifiableSet(creatures);
    }

    @Override
    public Optional<Creature> find(String name) {
        return creatures.stream().filter(c -> c.domainCreature().name().equals(name))
                .findFirst().map(c -> c);
    }

    @Override
    public MinestomCreature create(@NotNull EntityType type) {
        MinestomBrickCreature creature = new MinestomBrickCreature(new DCreature(type.name()));
        creatures.add(creature);
        return creature;
    }

    @Override
    public MinestomCreature create(@NotNull String name, @NotNull EntityType type) {
        MinestomBrickCreature creature = new MinestomBrickCreature(new DCreature(name, type.name()));
        creatures.add(creature);
        return creature;
    }

    @Override
    public CompletableFuture<Void> persist(@NotNull Creature creature) {
        MinestomBrickCreature crea = (MinestomBrickCreature) creature;
        crea.write();
        return databaseContext.persistAsync(crea.domainCreature());
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Creature creature) {
        MinestomBrickCreature crea = (MinestomBrickCreature) creature;
        crea.despawn();

        this.creatures.remove(crea);
        return databaseContext.removeAsync(crea.domainCreature());
    }

    @Override
    public CompletableFuture<Void> merge(@NotNull Creature creature) {
        MinestomBrickCreature crea = (MinestomBrickCreature) creature;
        crea.write();
        return databaseContext.mergeAsync(crea.domainCreature()).thenAccept(crea::setDomainCreature);
    }

    @Override
    public void registerTrait(TraitKey<MinestomCreature> key) {
        traits.add(key);
    }

    @Override
    public void unregisterTrait(TraitKey<MinestomCreature> key) {
        traits.remove(key);
    }

    @Override
    public Collection<TraitKey<MinestomCreature>> traits() {
        return Collections.unmodifiableSet(traits);
    }

    @Override
    public Optional<TraitKey<MinestomCreature>> findTrait(String name) {
        return traits.stream().filter(t -> t.name().equals(name)).findFirst();
    }
}

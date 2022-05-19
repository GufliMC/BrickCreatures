package com.guflimc.brick.creatures.api;

import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.api.domain.PersistentSpawn;
import com.guflimc.brick.creatures.api.meta.Position;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CreatureManager<WORLD, ENTITY, ENTITYTYPE> {

    // normal spawns

    Creature<ENTITY> spawn(@NotNull Position position, @NotNull WORLD world, @NotNull ENTITYTYPE type);

    Creature<ENTITY> spawn(@NotNull Position position, @NotNull WORLD world, @NotNull PersistentCreature creature);

    // persistent creature

    Collection<PersistentCreature> creatures();

    Optional<PersistentCreature> creature(@NotNull String id);

    CompletableFuture<PersistentCreature> persist(@NotNull String id, @NotNull Creature<ENTITY> creature);

    CompletableFuture<PersistentCreature> persist(@NotNull String id, @NotNull ENTITYTYPE type);

    CompletableFuture<Void> persist(@NotNull PersistentCreature creature);

    CompletableFuture<Void> remove(@NotNull PersistentCreature creature);

    // persistent spawn

    Collection<PersistentSpawn> spawns();

    Optional<PersistentSpawn> spawn(@NotNull String id);

    CompletableFuture<PersistentSpawn> persist(@NotNull String id, @NotNull PersistentCreature creature,
                                               @NotNull Position position, @NotNull WORLD world);

    CompletableFuture<Void> remove(@NotNull PersistentSpawn spawn);

    CompletableFuture<Void> persist(@NotNull PersistentSpawn spawn);



}

package com.guflimc.brick.creatures.api;

import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.api.domain.TraitKey;
import com.guflimc.brick.creatures.api.domain.TraitLifecycle;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CreatureManager<C extends Creature, T> {

    void reload();

    Collection<Creature> creatures();

    Optional<Creature> find(String name);

    C create(@NotNull T type);

    C create(@NotNull String name, @NotNull T type);

    CompletableFuture<Void> persist(@NotNull Creature creature);

    CompletableFuture<Void> remove(@NotNull Creature creature);

    CompletableFuture<Void> merge(@NotNull Creature creature);

    void registerTrait(TraitKey<C> key);

    void unregisterTrait(TraitKey<C> key);

    Collection<TraitKey<C>> traits();

    Optional<TraitKey<C>> findTrait(String name);


}

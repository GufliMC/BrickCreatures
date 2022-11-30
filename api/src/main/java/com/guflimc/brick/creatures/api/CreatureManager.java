package com.guflimc.brick.creatures.api;

import com.guflimc.brick.creatures.api.domain.Creature;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CreatureManager<C extends Creature> {

    Collection<C> creatures();

    Optional<C> findCreature(@NotNull String name);

    CompletableFuture<Void> update(@NotNull Creature creature);

    CompletableFuture<Void> remove(@NotNull Creature creature);

}

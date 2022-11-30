package com.guflimc.brick.creatures.common;

import com.guflimc.brick.creatures.api.CreatureManager;
import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.common.domain.DCreature;
import com.guflimc.brick.creatures.common.proxy.CreatureProxy;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public abstract class BrickCreatureManager<C extends Creature> implements CreatureManager<C> {

    private final BrickCreaturesDatabaseContext databaseContext;

    private final Set<Creature> creatures = new CopyOnWriteArraySet<>();

    public BrickCreatureManager(BrickCreaturesDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;

        databaseContext.findAllAsync(DCreature.class).join()
                .forEach(c -> creatures.add(proxify(c)));
    }

    protected abstract CreatureProxy proxify(DCreature creature);

    @Override
    public Collection<C> creatures() {
        return creatures.stream().map(c -> (C) c).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<C> findCreature(@NotNull String name) {
        return creatures.stream().filter(c -> c.name().equals(name))
                .findFirst().map(c -> (C) c);
    }

    @Override
    public CompletableFuture<Void> update(@NotNull Creature creature) {
        CreatureProxy proxy = (CreatureProxy) creature;
        return databaseContext.persistAsync(proxy.handle());
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Creature creature) {
        CreatureProxy proxy = (CreatureProxy) creature;
        creatures.remove(proxy);
        return databaseContext.removeAsync(proxy.handle());
    }

    protected CompletableFuture<Creature> create(@NotNull String name, @NotNull String type) {
        DCreature creature = new DCreature(name, type);
        CreatureProxy proxy = proxify(creature);
        creatures.add(proxy);
        return databaseContext.persistAsync(creature).thenApply(c -> proxy);
    }
}

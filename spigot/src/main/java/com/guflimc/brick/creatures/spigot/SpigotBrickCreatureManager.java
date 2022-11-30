package com.guflimc.brick.creatures.spigot;

import com.guflimc.brick.creatures.common.BrickCreatureManager;
import com.guflimc.brick.creatures.common.BrickCreaturesDatabaseContext;
import com.guflimc.brick.creatures.common.domain.DCreature;
import com.guflimc.brick.creatures.common.proxy.CreatureProxy;
import com.guflimc.brick.creatures.spigot.api.SpigotCreatureManager;
import com.guflimc.brick.creatures.spigot.api.domain.SpigotCreature;
import com.guflimc.brick.creatures.spigot.proxy.SpigotCreatureProxy;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SpigotBrickCreatureManager extends BrickCreatureManager<SpigotCreature> implements SpigotCreatureManager {

    public SpigotBrickCreatureManager(BrickCreaturesDatabaseContext databaseContext) {
        super(databaseContext);
    }

    @Override
    public CompletableFuture<SpigotCreature> create(@NotNull String name, @NotNull EntityType type) {
        return super.create(name, type.name()).thenApply(c -> (SpigotCreature) c);
    }

    @Override
    protected CreatureProxy proxify(DCreature creature) {
        return new SpigotCreatureProxy(creature);
    }
}

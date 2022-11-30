package com.guflimc.brick.creatures.spigot.api;

import com.guflimc.brick.creatures.api.CreatureManager;
import com.guflimc.brick.creatures.spigot.api.domain.SpigotCreature;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface SpigotCreatureManager extends CreatureManager<SpigotCreature> {

    CompletableFuture<SpigotCreature> create(@NotNull String name, @NotNull EntityType type);

}

package com.guflimc.brick.creatures.spigot.api.domain;

import com.guflimc.brick.creatures.api.domain.Creature;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface SpigotCreature extends Creature {

    EntityType type();

    <T extends Entity> void modify(@NotNull Class<T> type, @NotNull Consumer<T> consumer);

    Entity spawn(@NotNull Location location);

}

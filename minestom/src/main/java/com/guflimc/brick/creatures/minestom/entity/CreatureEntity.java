package com.guflimc.brick.creatures.minestom.entity;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CreatureEntity extends EntityCreature {

    public CreatureEntity(@NotNull EntityType entityType, @NotNull UUID uuid) {
        super(entityType, uuid);
    }

    public CreatureEntity(@NotNull EntityType entityType) {
        super(entityType);


    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void _remove() {
        super.remove();
    }
}

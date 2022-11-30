package com.guflimc.brick.creatures.common.proxy;

import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.common.domain.DCreature;

import java.util.UUID;

public class CreatureProxy implements Creature {

    protected final DCreature creature;

    public CreatureProxy(DCreature creature) {
        this.creature = creature;
    }

    public DCreature handle() {
        return creature;
    }

    @Override
    public UUID id() {
        return creature.id();
    }

    @Override
    public String name() {
        return creature.name();
    }
}

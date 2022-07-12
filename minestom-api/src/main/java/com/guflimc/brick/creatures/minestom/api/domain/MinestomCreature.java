package com.guflimc.brick.creatures.minestom.api.domain;

import com.guflimc.brick.creatures.api.domain.Creature;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;

public interface MinestomCreature extends Creature {

    void setInstance(Instance instance);

    Instance instance();

    void despawn();

    Entity entity();

}

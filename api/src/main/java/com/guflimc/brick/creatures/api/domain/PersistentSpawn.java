package com.guflimc.brick.creatures.api.domain;

import com.guflimc.brick.creatures.api.meta.Position;

public interface PersistentSpawn {

    String name();

    PersistentCreature creature();

    Position position();

    void setPosition(Position position);

}

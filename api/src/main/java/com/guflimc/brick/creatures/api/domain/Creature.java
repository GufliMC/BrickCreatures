package com.guflimc.brick.creatures.api.domain;

import com.guflimc.brick.maths.api.geo.Position;

import java.util.List;
import java.util.UUID;

public interface Creature {

    UUID id();

    String name();

    Position position();

    void setPosition(Position position);

    void setHumanSkin(String textures, String signature);

    List<TraitKey<?>> traits();

    void addTrait(TraitKey<?> key);

    void removeTrait(TraitKey<?> key);

}

package com.guflimc.brick.creatures.api.domain;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface Creature<T> {

    UUID id();

    T entity();

    void setHologram(Component hologram);

    void addTrait(Trait trait);

    void despawn();

    void refresh();

}

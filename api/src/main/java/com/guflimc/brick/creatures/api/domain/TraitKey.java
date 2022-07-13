package com.guflimc.brick.creatures.api.domain;

public record TraitKey<T extends Creature>(String name, TraitLifecycle.Creator<T> creator) {
}

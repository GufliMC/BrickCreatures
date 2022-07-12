package com.guflimc.brick.creatures.api.domain;

public abstract class TraitLifecycle<T extends Creature> {

    protected final T creature;

    protected TraitLifecycle(T creature) {
        this.creature = creature;
    }

    // lifecycle

    public void onTick() {}

    public void onEnable() {}

    public void onDisable() {}

    public void onInteract() {}

    public void onDamage() {}

}

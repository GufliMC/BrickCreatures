package com.guflimc.brick.creatures.api.domain;

public abstract class TraitHandler<T> {

    protected final Trait trait;
    protected final T entity;

    protected TraitHandler(Trait trait, T entity) {
        this.trait = trait;
        this.entity = entity;
    }

    public final Trait trait() {
        return trait;
    }

    // lifecycle

    public void onTick() {}

    public void onEnable() {}

    public void onDisable() {}

    public void onInteract() {}
    public
    void onDamage() {}

}

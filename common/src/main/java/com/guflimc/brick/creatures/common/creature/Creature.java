package com.guflimc.brick.creatures.common.creature;

import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.api.domain.Trait;
import com.guflimc.brick.creatures.api.domain.TraitHandler;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class Creature<T> implements com.guflimc.brick.creatures.api.domain.Creature<T> {

    protected final UUID id;
    protected final T entity;

    protected final List<TraitHandler<?>> traitHandlers = new ArrayList<>();

    protected final PersistentCreature persistentCreature;

    public Creature(UUID id, T entity, PersistentCreature persistentCreature) {
        this.id = id;
        this.entity = entity;
        this.persistentCreature = persistentCreature;
    }

    public Creature(UUID id, T entity) {
        this(id, entity, null);
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public T entity() {
        return entity;
    }

    public PersistentCreature persistentCreature() {
        return persistentCreature;
    }

    public List<TraitHandler<?>> traitHandlers() {
        return Collections.unmodifiableList(traitHandlers);
    }

    @Override
    public void addTrait(Trait trait) {
        TraitHandler<?> handler = trait.newHandler(entity);
        traitHandlers.add(handler);
        handler.onEnable();
    }

    @Override
    public void despawn() {
        traitHandlers.forEach(TraitHandler::onDisable);
        traitHandlers.clear();
    }
}

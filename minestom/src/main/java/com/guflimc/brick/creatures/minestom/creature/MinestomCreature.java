package com.guflimc.brick.creatures.minestom.creature;

import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.api.domain.PersistentSpawn;
import com.guflimc.brick.creatures.api.domain.Trait;
import com.guflimc.brick.creatures.common.creature.Creature;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.EntityCreature;

import java.util.UUID;

public class MinestomCreature extends Creature<EntityCreature> {

    private final PersistentSpawn spawn;

    public MinestomCreature(UUID id, EntityCreature entity, PersistentCreature persistentCreature, PersistentSpawn spawn) {
        super(id, entity, persistentCreature);
        this.spawn = spawn;
    }

    public MinestomCreature(UUID id, EntityCreature entity, PersistentCreature creature) {
        this(id, entity, creature, null);
    }

    public MinestomCreature(UUID id, EntityCreature entity) {
        this(id, entity, null, null);
    }

    public final PersistentSpawn spawn() {
        return spawn;
    }

    @Override
    public void setHologram(Component hologram) {
        entity.setCustomName(hologram);
    }

    @Override
    public void addTrait(Trait trait) {
        traitHandlers.add(trait.newHandler(entity));
    }

    @Override
    public void despawn() {
        super.despawn();
        entity.remove();

    }

    @Override
    public void refresh() {
        if ( persistentCreature != null ) {
            persistentCreature.setHologram(entity.getCustomName());
            // TODO metadata
        }
    }

}

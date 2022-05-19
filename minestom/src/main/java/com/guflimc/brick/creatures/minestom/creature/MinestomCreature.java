package com.guflimc.brick.creatures.minestom.creature;

import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.api.domain.PersistentSpawn;
import com.guflimc.brick.creatures.api.domain.Trait;
import com.guflimc.brick.creatures.common.creature.Creature;
import com.guflimc.brick.creatures.minestom.creature.player.FakeFakePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.entity.metadata.PlayerMeta;

import java.util.UUID;

public class MinestomCreature extends Creature<EntityCreature> {

    private final PersistentSpawn spawn;

    public MinestomCreature(UUID id, EntityCreature entity, PersistentCreature persistentCreature, PersistentSpawn spawn) {
        super(id, entity, persistentCreature);
        this.spawn = spawn;
        refresh();
    }

    public MinestomCreature(UUID id, EntityCreature entity, PersistentCreature creature) {
        this(id, entity, creature, null);
    }

    public MinestomCreature(UUID id, EntityCreature entity) {
        this(id, entity, null, null);
    }

    public final PersistentSpawn persistentSpawn() {
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
        if ( spawn != null ) {
            entity.teleport(new Pos(spawn.position().x(), spawn.position().y(), spawn.position().z(),
                    spawn.position().yaw(), spawn.position().pitch()));
        }

        if ( persistentCreature != null ) {
            entity.setCustomName(persistentCreature.hologram());

            if ( entity instanceof FakeFakePlayer ffp ) {
                // player specific stuff
                if ( persistentCreature.skin() != null ) {
                    ffp.setSkin(new PlayerSkin(persistentCreature.skin().texture(),
                            persistentCreature.skin().signature()));
                    PlayerMeta meta = (PlayerMeta) ffp.getEntityMeta();
                    meta.setHatEnabled(true);
                    meta.setRightLegEnabled(true);
                    meta.setLeftLegEnabled(true);
                    meta.setJacketEnabled(true);
                    meta.setRightSleeveEnabled(true);
                    meta.setLeftSleeveEnabled(true);
                    meta.setCapeEnabled(true);
                }
            }

            if ( persistentCreature.metadata() != null ) {
                // TODO
            }
        }
    }

}

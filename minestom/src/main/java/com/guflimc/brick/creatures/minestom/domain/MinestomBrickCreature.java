package com.guflimc.brick.creatures.minestom.domain;

import com.guflimc.brick.creatures.api.domain.TraitLifecycle;
import com.guflimc.brick.creatures.common.domain.DCreature;
import com.guflimc.brick.creatures.minestom.api.domain.MinestomCreature;
import com.guflimc.brick.creatures.minestom.api.domain.MinestomTraitLifecycle;
import com.guflimc.brick.creatures.minestom.entity.CreatureEntity;
import com.guflimc.brick.creatures.minestom.entity.CreatureHuman;
import com.guflimc.brick.maths.api.geo.Position;
import com.guflimc.brick.maths.minestom.api.MinestomMaths;
import com.guflimc.brick.worlds.api.world.World;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.SharedInstance;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.*;

public class MinestomBrickCreature implements MinestomCreature {

    private @NotNull DCreature domainCreature;
    private final CreatureEntity entity;

    protected final Map<String, MinestomTraitLifecycle> traitLifecycles = new HashMap<>();

    public MinestomBrickCreature(@NotNull DCreature domainCreature) {
        this.domainCreature = domainCreature;

        EntityType type = EntityType.fromNamespaceId(domainCreature.type());
        if ( type == EntityType.PLAYER ) {
            this.entity = new CreatureHuman();
        } else {
            this.entity = new CreatureEntity(type);
        }

        // TODO nbt

        // TODO traits
    }

    public void setDomainCreature(@NotNull DCreature creature) {
        this.domainCreature = creature;
    }

    public DCreature domainCreature() {
        return domainCreature;
    }

    @Override
    public void setInstance(Instance instance) {
        entity.setInstance(instance);
        entity.teleport(MinestomMaths.toPos(domainCreature.position()));

        if (MinecraftServer.getExtensionManager().hasExtension("brickworlds")) {
            if (instance instanceof World w) {
                domainCreature.location = domainCreature.location.withWorldName(w.info().name());
            } else if (instance instanceof SharedInstance si && si.getInstanceContainer() instanceof World w) {
                domainCreature.location = domainCreature.location.withWorldName(w.info().name());
            }
        }
    }

    @Override
    public Instance instance() {
        return entity.getInstance();
    }

    @Override
    public void despawn() {
        entity._remove();
    }

    @Override
    public Entity entity() {
        return entity;
    }

    //

    @Override
    public UUID id() {
        return domainCreature.id();
    }

    @Override
    public String name() {
        return domainCreature.name();
    }

    @Override
    public Position position() {
        return domainCreature.position();
    }

    @Override
    public void setPosition(Position position) {
        this.domainCreature.setPosition(position);

        if ( entity.getInstance() != null ) {
            entity.teleport(MinestomMaths.toPos(position));
        }
    }

    @Override
    public NBTCompound nbt() {
        return domainCreature.nbt();
    }

    @Override
    public void setNBT(NBTCompound nbt) {
        this.domainCreature.setNBT(nbt);
        // TODO
    }

    @Override
    public List<String> traits() {
        return domainCreature.traits();
    }

    @Override
    public void addTrait(String trait) {
        domainCreature.addTrait(trait);
        // TODO

        // create and start the trait lifecycle
    }

    @Override
    public void removeTrait(String trait) {
        domainCreature.removeTrait(trait);

        MinestomTraitLifecycle lf = traitLifecycles.remove(trait);
        if ( lf != null ) {
            lf.onDisable();
        }
    }

}

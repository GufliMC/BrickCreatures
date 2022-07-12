package com.guflimc.brick.creatures.minestom.domain;

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
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.SharedInstance;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.utils.binary.BinaryReader;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

public class MinestomBrickCreature implements MinestomCreature {

    private @NotNull DCreature domainCreature;
    private final CreatureEntity entity;

    protected final Map<String, MinestomTraitLifecycle> traitLifecycles = new HashMap<>();

    public MinestomBrickCreature(@NotNull DCreature domainCreature) {
        this.domainCreature = domainCreature;

        EntityType type = EntityType.fromNamespaceId(domainCreature.type());
        if (type == EntityType.PLAYER) {
            this.entity = new CreatureHuman();
        } else {
            this.entity = new CreatureEntity(type);
        }

        readMetadata();

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
                domainCreature.setLocation(domainCreature.location().withWorldName(w.info().name()));
            } else if (instance instanceof SharedInstance si && si.getInstanceContainer() instanceof World w) {
                domainCreature.setLocation(domainCreature.location().withWorldName(w.info().name()));
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

    /**
     * domain -> entity
     */
    public void readMetadata() {
        if ( domainCreature.metadata() == null ) {
            return;
        }

        // the metadata packet contains a native method to convert bytes -> metadata entries
        byte[] bytes = Base64.getDecoder().decode(domainCreature.metadata());
        BinaryReader reader = new BinaryReader(bytes);
        EntityMetaDataPacket packet = new EntityMetaDataPacket(reader);

        try {
            // need some reflection to put the parsed entries in the metadata object of the entity
            Field field = EntityMeta.class.getDeclaredField("metadata");
            field.trySetAccessible();

            Metadata metadata = (Metadata) field.get(entity.getEntityMeta());
            Map<Integer, Metadata.Entry<?>> entries = packet.entries();
            for ( int index : entries.keySet() ) {
                metadata.getEntries().put(index, entries.get(index));
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * entity -> domain
     */
    public void writeMetadata() {
        // the metadata packet contains a native method to convert metadata -> bytes
        BinaryWriter writer = new BinaryWriter();
        entity.getMetadataPacket().write(writer);
        domainCreature.setMetadata(Base64.getEncoder().encodeToString(writer.toByteArray()));
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

        if (entity.getInstance() != null) {
            entity.teleport(MinestomMaths.toPos(position));
        }
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
        if (lf != null) {
            lf.onDisable();
        }
    }

}

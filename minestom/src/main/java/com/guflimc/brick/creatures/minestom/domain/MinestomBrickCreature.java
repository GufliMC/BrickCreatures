package com.guflimc.brick.creatures.minestom.domain;

import com.guflimc.brick.creatures.api.domain.TraitKey;
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
import net.minestom.server.entity.EntityCreature;
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

    public final Map<TraitKey<?>, MinestomTraitLifecycle> traitLifecycles = new HashMap<>();

    public MinestomBrickCreature(@NotNull DCreature domainCreature) {
        this.domainCreature = domainCreature;

        EntityType type = EntityType.fromNamespaceId(domainCreature.type());
        if (type == EntityType.PLAYER) {
            CreatureHuman human = new CreatureHuman();
            human.setSkin(domainCreature.humanSkinTextures(), domainCreature.humanSkinSignature());

            this.entity = human;
        } else {
            this.entity = new CreatureEntity(type);
        }

        read();

        traits().forEach(this::initTrait);
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
        entity.despawn();

        traitLifecycles.values().forEach(TraitLifecycle::onDisable);
    }

    @Override
    public EntityCreature entity() {
        return entity;
    }

    /**
     * domain -> entity
     */
    public void read() {
        if (domainCreature.metadata() != null) {
            byte[] bytes = Base64.getDecoder().decode(domainCreature.metadata());
            BinaryReader reader = new BinaryReader(bytes);
            EntityMetaDataPacket packet = new EntityMetaDataPacket(reader);

            try {
                // need some reflection to put the parsed entries in the metadata object of the entity
                Field field = EntityMeta.class.getDeclaredField("metadata");
                field.trySetAccessible();

                Metadata metadata = (Metadata) field.get(entity.getEntityMeta());
                Map<Integer, Metadata.Entry<?>> entries = packet.entries();
                for (int index : entries.keySet()) {
                    metadata.getEntries().put(index, entries.get(index));
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        // TODO other stuff
    }

    /**
     * entity -> domain
     */
    public void write() {
        // the metadata packet contains a native method to convert metadata -> bytes
        BinaryWriter writer = new BinaryWriter();
        entity.getMetadataPacket().write(writer);
        domainCreature.setMetadata(Base64.getEncoder().encodeToString(writer.toByteArray()));

        // TODO other stuff
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
    public void setHumanSkin(String textures, String signature) {
        this.domainCreature.setHumanSkin(textures, signature);

        if (entity.getEntityType() == EntityType.PLAYER) {
            return;
        }

        CreatureHuman human = (CreatureHuman) entity;
        human.setSkin(textures, signature);
    }

    @Override
    public List<TraitKey<?>> traits() {
        return domainCreature.traits();
    }

    @Override
    public void addTrait(TraitKey<?> key) {
        domainCreature.addTrait(key);
        initTrait(key);
    }

    @Override
    public void removeTrait(TraitKey<?> key) {
        domainCreature.removeTrait(key);

        MinestomTraitLifecycle lf = traitLifecycles.remove(key);
        if (lf != null) {
            lf.onDisable();
        }
    }

    private void initTrait(TraitKey<?> key) {
        MinestomTraitLifecycle lf = (MinestomTraitLifecycle) ((TraitKey<MinestomCreature>) key).creator().create(this);
        traitLifecycles.put(key, lf);
        lf.onEnable();
    }

}

package com.guflimc.brick.creatures.spigot.proxy;

import com.guflimc.brick.creatures.common.domain.DCreature;
import com.guflimc.brick.creatures.common.proxy.CreatureProxy;
import com.guflimc.brick.creatures.spigot.api.domain.SpigotCreature;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.CompoundBuilder;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.nbt.NBTReader;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.function.Consumer;

public class SpigotCreatureProxy extends CreatureProxy implements SpigotCreature {

    public SpigotCreatureProxy(DCreature creature) {
        super(creature);

        if (creature.metadata() == null) {
            // load entity for the first time
            modify(type().getEntityClass(), x -> {
            });
        }
    }

    //

    private NBTTagCompound readNMS() {
        byte[] data = Base64.getDecoder().decode(creature.metadata());
        try (ByteArrayInputStream is = new ByteArrayInputStream(data);) {
            return NBTCompressedStreamTools.a(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeNMS(NBTTagCompound nbt) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            NBTCompressedStreamTools.a(nbt, os);
            creature.setMetadata(Base64.getEncoder().encodeToString(os.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //

    private NBTCompound read() {
        byte[] data = Base64.getDecoder().decode(creature.metadata());
        try (
                NBTReader reader = new NBTReader(data);
        ) {
            return (NBTCompound) reader.read();
        } catch (IOException | NBTException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(NBTCompound nbt) {
        try (
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
        ) {
            nbt.writeContents(dos);
            creature.setMetadata(Base64.getEncoder().encodeToString(os.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //

    @Override
    public EntityType type() {
        return EntityType.valueOf(creature.type());
    }

    @Override
    public <T extends Entity> void modify(@NotNull Class<T> type, @NotNull Consumer<T> consumer) {
        if (!Objects.equals(type().getEntityClass(), type)) {
            throw new IllegalArgumentException("Type mismatch.");
        }

        World world = Bukkit.getWorlds().get(0);
        Location loc = new Location(world, 0, 0, 0);
        Entity entity = spawn(loc);

        consumer.accept(type.cast(entity));

        // metadata
        NBTTagCompound nbt = new NBTTagCompound();
        CraftLivingEntity cle = (CraftLivingEntity) entity;
        cle.getHandle().b(nbt);
        writeNMS(nbt);

        entity.remove();
    }

    @Override
    public Entity spawn(@NotNull Location location) {
        if (location.getWorld() == null) {
            throw new IllegalArgumentException("Invalid location.");
        }
        Entity entity = location.getWorld().spawnEntity(location, type());

        // metadata
        if ( creature.metadata() != null ) {
            NBTTagCompound nbt = readNMS();
            CraftLivingEntity cle = (CraftLivingEntity) entity;
            cle.getHandle().a(nbt);
        }

        return entity;
    }

    public NBTCompound nbt() {
        return read();
    }

    public void modify(Consumer<MutableNBTCompound> consumer) {
        NBTCompound nbt = read().modify(consumer::accept);
        write(nbt);
        System.out.println(nbt.toSNBT());
    }

}

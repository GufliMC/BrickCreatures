package com.guflimc.brick.creatures.spigot.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import com.guflimc.brick.creatures.api.CreatureAPI;
import com.guflimc.brick.creatures.spigot.api.SpigotCreatureAPI;
import com.guflimc.brick.creatures.spigot.api.domain.SpigotCreature;
import com.guflimc.brick.creatures.spigot.proxy.SpigotCreatureProxy;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftZombie;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SpigotCreatureCommands {

    @CommandMethod("bc create <name> <type>")
    public void create(Player sender,
                               @Argument(value = "name") String name,
                               @Argument(value = "type") EntityType type
    ) {
        if (CreatureAPI.get().findCreature(name).isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.create.error.already.exists", name);
            return;
        }

        SpigotCreatureAPI.get().create(name, type);
        SpigotI18nAPI.get(this).send(sender, "cmd.create", name, type.name());
    }

    @CommandMethod("bc spawn <creature>")
    public void spawn(Player sender,
                               @Argument(value = "creature") SpigotCreature creature
    ) {
        creature.spawn(sender.getLocation());
    }

    @CommandMethod("bc getmeta <creature> <key>")
    public void getmeta(Player sender,
                        @Argument(value = "creature") SpigotCreature creature,
                        @Argument(value = "key", parserName = "metadata") String key) {

        SpigotCreatureProxy proxy = (SpigotCreatureProxy) creature;
        NBT nbt = proxy.nbt().get(key);
        Object value = null;
        if ( nbt != null ) value = nbt.getValue();

        SpigotI18nAPI.get(this).send(sender, "cmd.getmeta", key, creature.name(), value);
    }

    @CommandMethod("bc setmeta <creature> <key> <value>")
    public void setmeta(Player sender,
                             @Argument(value = "creature") SpigotCreature creature,
                             @Argument(value = "key", parserName = "metadata") String key,
                             @Argument(value = "value") @Greedy String value) {

        SpigotCreatureProxy proxy = (SpigotCreatureProxy) creature;

        Class<?> type = proxy.nbt().get(key).getValue().getClass();
        if (!parsers.containsKey(type)) {
            SpigotI18nAPI.get(this).send(sender, "cmd.setmeta.error.key.unsupported", key);
            return;
        }

        NBT val;
        try {
            val = parsers.get(type).apply(value);
        } catch (Exception ex) {
            SpigotI18nAPI.get(this).send(sender, "cmd.setmeta.error.value.invalid", value);
            return;
        }

        proxy.modify(cb -> cb.set(key, val));
        CreatureAPI.get().update(creature);

        SpigotI18nAPI.get(this).send(sender, "cmd.setmeta", key, value, creature.name());
    }

    static Map<Class<?>, Function<String, NBT>> parsers = new HashMap<>();

    static {
        parsers.put(Boolean.class, s -> NBT.Boolean(Boolean.parseBoolean(s)));
        parsers.put(Integer.class, s -> NBT.Int(Integer.parseInt(s)));
        parsers.put(Double.class, s -> NBT.Double(Double.parseDouble(s)));
        parsers.put(Long.class, s -> NBT.Long(Long.parseLong(s)));
        parsers.put(Float.class, s -> NBT.Float(Float.parseFloat(s)));
        parsers.put(Byte.class, s -> NBT.Byte(Byte.parseByte(s)));
        parsers.put(Short.class, s -> NBT.Short(Short.parseShort(s)));
        parsers.put(String.class, NBT::String);
    }
}

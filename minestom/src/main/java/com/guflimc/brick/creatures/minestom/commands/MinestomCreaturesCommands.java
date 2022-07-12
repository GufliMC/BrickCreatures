package com.guflimc.brick.creatures.minestom.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.minestom.MinestomBrickCreatureManager;
import com.guflimc.brick.creatures.minestom.api.domain.MinestomCreature;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.maths.minestom.api.MinestomMaths;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.utils.position.PositionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MinestomCreaturesCommands {

    private final MinestomBrickCreatureManager manager;

    public MinestomCreaturesCommands(MinestomBrickCreatureManager manager) {
        this.manager = manager;
    }

    @CommandMethod("bc reload")
    public void reload(Audience sender) {
        manager.reload();
        I18nAPI.get(this).send(sender, "cmd.reload");
    }

    @CommandMethod("bc create <name> <type>")
    public void creatureCreate(Player sender,
                               @Argument(value = "name") String name,
                               @Argument(value = "type") EntityType type
    ) {
        if (manager.find(name).isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.create.invalid", name);
            return;
        }

        try {
            MinestomCreature mc = manager.create(name, type);

            mc.setPosition(MinestomMaths.toPosition(sender.getPosition()));
            mc.setInstance(sender.getInstance());

            manager.persist(mc);

            I18nAPI.get(this).send(sender, "cmd.create", name, type.name());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @CommandMethod("bc delete <creature>")
    public void creatureDelete(Audience sender,
                               @Argument(value = "creature") MinestomCreature creature
    ) {
        manager.remove(creature);
        I18nAPI.get(this).send(sender, "cmd.delete", creature.name());
    }

    @CommandMethod("bc list")
    public void creatureList(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.list",
                manager.creatures().stream().map(Creature::name).collect(Collectors.toList()));
    }

    @CommandMethod("bc tphere <creature>")
    public void creatureTeleporthere(Player sender,
                                     @Argument(value = "creature") MinestomCreature creature
    ) {
        creature.setPosition(MinestomMaths.toPosition(sender.getPosition()));
        creature.setInstance(sender.getInstance());

        manager.merge(creature);
        I18nAPI.get(this).send(sender, "cmd.edit.teleporthere", creature.name());
    }

    @CommandMethod("bc lookhere <creature>")
    public void creatureLookhere(Player sender,
                                 @Argument(value = "creature") MinestomCreature creature) {

        EntityType type = creature.entity().getEntityType();

        Pos playerpos = sender.getPosition().add(0, 1.6, 0);
        Pos entitypos = MinestomMaths.toPos(creature.position()).add(0, type.height() * 0.85, 0);
        Vec delta = playerpos.sub(entitypos).asVec().normalize();

        Pos result = MinestomMaths.toPos(creature.position());
        result = result.withYaw(PositionUtils.getLookYaw(delta.x(), delta.z()));
        result = result.withPitch(PositionUtils.getLookPitch(delta.x(), delta.y(), delta.z()));

        creature.setPosition(MinestomMaths.toPosition(result));
        manager.merge(creature);

        I18nAPI.get(this).send(sender, "cmd.edit.lookhere", creature.name());
    }

    @CommandMethod("bc setskin <creature> <username>")
    public void creatureEditSkin(Player sender,
                                 @Argument(value = "creature") MinestomCreature creature,
                                 @Argument(value = "username") String username) {

        if ( creature.entity().getEntityType() != EntityType.PLAYER ) {
            I18nAPI.get(this).send(sender, "cmd.edit.skin.unsupported");
            return;
        }

        PlayerSkin skin = PlayerSkin.fromUsername(username);
        if ( skin == null ) {
            I18nAPI.get(this).send(sender, "cmd.edit.skin.invalid", username);
            return;
        }

        creature.setHumanSkin(skin.textures(), skin.signature());

        // default skin meta
        PlayerMeta meta = (PlayerMeta) creature.entity().getEntityMeta();
        meta.setCapeEnabled(true);
        meta.setLeftSleeveEnabled(true);
        meta.setRightSleeveEnabled(true);
        meta.setLeftLegEnabled(true);
        meta.setRightLegEnabled(true);
        meta.setHatEnabled(true);
        meta.setJacketEnabled(true);

        manager.merge(creature);

        I18nAPI.get(this).send(sender, "cmd.edit.skin", creature.name());
    }

    @CommandMethod("bc setmeta <creature> <key> <value>")
    public void editMetadata(Player sender,
                             @Argument(value = "creature") MinestomCreature creature,
                             @Argument(value = "key", parserName = "metadata") Method method,
                             @Argument(value = "value") @Greedy String value) {

        EntityMeta em = creature.entity().getEntityMeta();
        String key = method.getName().substring(3);

        Class<?> type = method.getParameterTypes()[0];
        if (!parsers.containsKey(type)) {
            I18nAPI.get(this).send(sender, "cmd.edit.metadata.key.unsupported", key);
            return;
        }

        Object val;
        try {
            val = parsers.get(type).apply(value);
        } catch (Exception ex) {
            I18nAPI.get(this).send(sender, "cmd.edit.metadata.value.invalid", value);
            return;
        }

        try {
            method.invoke(em, val);
        } catch (IllegalAccessException | InvocationTargetException e) {
            I18nAPI.get(this).send(sender, "cmd.edit.metadata.error");
            return;
        }

        manager.merge(creature);
        I18nAPI.get(this).send(sender, "cmd.edit.metadata", key, value, creature.name());
    }

    static Map<Class<?>, Function<String, ?>> parsers = new HashMap<>();

    static {
        parsers.put(boolean.class, Boolean::parseBoolean);
        parsers.put(int.class, Integer::parseInt);
        parsers.put(double.class, Double::parseDouble);
        parsers.put(long.class, Long::parseLong);
        parsers.put(float.class, Float::parseFloat);
        parsers.put(byte.class, Byte::parseByte);
        parsers.put(short.class, Short::parseShort);
        parsers.put(char.class, s -> s.charAt(0));
        parsers.put(String.class, Function.identity());
        parsers.put(Component.class, s -> MiniMessage.miniMessage().deserialize(s));
    }
}

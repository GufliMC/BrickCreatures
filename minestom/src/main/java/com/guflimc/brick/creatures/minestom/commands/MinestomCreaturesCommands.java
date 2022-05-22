package com.guflimc.brick.creatures.minestom.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.api.domain.PersistentSpawn;
import com.guflimc.brick.creatures.minestom.MinestomBrickCreatureManager;
import com.guflimc.brick.i18n.api.I18nAPI;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.utils.position.PositionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MinestomCreaturesCommands {

    private final MinestomBrickCreatureManager manager;

    public MinestomCreaturesCommands(MinestomBrickCreatureManager manager) {
        this.manager = manager;
    }

    @Suggestions("creature")
    public List<String> creatureSuggestion(CommandContext<Audience> sender, String input) {
        return manager.creatures().stream()
                .map(PersistentCreature::name)
//                .filter(name -> name.startsWith(input))
                .collect(Collectors.toList());
    }

    @Suggestions("spawn")
    public List<String> spawnSuggestion(CommandContext<Audience> sender, String input) {
        return manager.spawns().stream()
                .map(PersistentSpawn::name)
//                .filter(name -> name.startsWith(input))
                .collect(Collectors.toList());
    }

    @CommandMethod("bc creature create <name> <type>")
    public void creatureCreate(Audience sender, @Argument(value = "name") String name, @Argument(value = "type") EntityType type) {
        if (manager.creature(name).isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.creature.create.invalid", name);
            return;
        }

        manager.persist(name, type);
        I18nAPI.get(this).send(sender, "cmd.creature.create", name, type.name());
    }

    @CommandMethod("bc creature delete <creature>")
    public void creatureDelete(Audience sender, @Argument(value = "creature") PersistentCreature creature) {
        manager.remove(creature);
        I18nAPI.get(this).send(sender, "cmd.creature.delete", creature.name());
    }

    @CommandMethod("bc spawn create <name> <creature>")
    public void spawnCreate(Player sender, @Argument(value = "name") String name, @Argument(value = "creature") PersistentCreature creature) {
        if (manager.spawn(name).isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.spawn.create.invalid", name);
            return;
        }

        manager.persist(name, creature, manager.position(sender.getPosition()), sender.getInstance());
        I18nAPI.get(this).send(sender, "cmd.spawn.create", name, creature.name());
    }

    @CommandMethod("bc spawn delete <spawn>")
    public void spawnDelete(Audience sender, @Argument(value = "spawn", suggestions = "spawn") String name) {
        Optional<PersistentSpawn> spawn = manager.spawn(name);
        if (spawn.isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.args.spawn", name);
            return;
        }

        manager.remove(spawn.get());
        I18nAPI.get(this).send(sender, "cmd.spawn.delete", spawn.get().name());
    }

    @CommandMethod("bc spawn list")
    public void spawnList(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.spawn.list",
                manager.spawns().stream().map(PersistentSpawn::name).collect(Collectors.toList()));
    }

    @CommandMethod("bc creature list")
    public void creatureList(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.creature.list",
                manager.creatures().stream().map(PersistentCreature::name).collect(Collectors.toList()));
    }

    @CommandMethod("bc creature edit skin <creature> <player>")
    public void creatureEditSkin(Audience sender,
                                 @Argument(value = "creature") PersistentCreature creature,
                                 @Argument(value = "player") String playerName) {
        PlayerSkin skin = PlayerSkin.fromUsername(playerName);
        if (skin == null) {
            I18nAPI.get(this).send(sender, "cmd.creature.edit.skin.invalid", playerName);
            return;
        }

        creature.setSkin(new com.guflimc.brick.creatures.api.meta.PlayerSkin(skin.textures(), skin.signature()));
        manager.merge(creature);

        I18nAPI.get(this).send(sender, "cmd.creature.edit.skin", creature.name());
    }

    @CommandMethod("bc spawn edit tphere <spawn>")
    public void spawnTeleporthere(Player sender,
                                  @Argument(value = "spawn", suggestions = "spawn") String spawnName) {
        Optional<PersistentSpawn> spawn = manager.spawn(spawnName);
        if (spawn.isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.args.spawn", spawnName);
            return;
        }

        spawn.get().setPosition(manager.position(sender.getPosition()));
        manager.merge(spawn.get());

        I18nAPI.get(this).send(sender, "cmd.spawn.edit.teleporthere", spawn.get().name());
    }

    @CommandMethod("bc spawn edit lookhere <spawn>")
    public void spawnLookhere(Player sender,
                              @Argument(value = "spawn", suggestions = "spawn") String spawnName) {
        Optional<PersistentSpawn> spawn = manager.spawn(spawnName);
        if (spawn.isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.args.spawn", spawnName);
            return;
        }

        EntityType type = EntityType.fromNamespaceId(spawn.get().creature().type());

        Pos playerpos = sender.getPosition().add(0, 1.6, 0);
        Pos entitypos = manager.position(spawn.get().position()).add(0, type.height() * 0.85, 0);
        Vec delta = playerpos.sub(entitypos).asVec().normalize();

        Pos result = manager.position(spawn.get().position());
        result = result.withYaw(PositionUtils.getLookYaw(delta.x(), delta.z()));
        result = result.withPitch(PositionUtils.getLookPitch(delta.x(), delta.y(), delta.z()));

        spawn.get().setPosition(manager.position(result));
        manager.merge(spawn.get());

        I18nAPI.get(this).send(sender, "cmd.spawn.edit.lookhere", spawn.get().name());
    }
}

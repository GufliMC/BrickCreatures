package com.guflimc.brick.creatures.minestom.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.api.domain.PersistentSpawn;
import com.guflimc.brick.creatures.minestom.MinestomCreatureManager;
import com.guflimc.brick.i18n.api.I18nAPI;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.registry.ProtocolObject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MinestomCreaturesCommands {

    private final MinestomCreatureManager manager;

    public MinestomCreaturesCommands(MinestomCreatureManager manager) {
        this.manager = manager;
    }

    @Suggestions("creature")
    public List<String> creatureSuggestion(CommandContext<Audience> sender, String input) {
        return manager.creatures().stream()
                .map(PersistentCreature::name)
                .filter(name -> name.startsWith(input))
                .collect(Collectors.toList());
    }

    @Suggestions("spawn")
    public List<String> spawnSuggestion(CommandContext<Audience> sender, String input) {
        return manager.spawns().stream()
                .map(PersistentSpawn::name)
                .filter(name -> name.startsWith(input))
                .collect(Collectors.toList());
    }

    @Suggestions("entityType")
    public List<String> entityTypeSuggestion(CommandContext<Audience> sender, String input) {
        return EntityType.values().stream()
                .map(ProtocolObject::name)
                .filter(name -> name.startsWith(input))
                .collect(Collectors.toList());
    }

    @CommandMethod("bc creature create <name> <type>")
    public void creatureCreate(Audience sender, @Argument(value = "name") String name, @Argument(value = "type") String type) {
        if ( manager.creature(name).isPresent() ) {
            I18nAPI.get(this).send(sender, "cmd.creature.create.invalid", name);
            return;
        }

        manager.persist(name, EntityType.fromNamespaceId(type));
        I18nAPI.get(this).send(sender, "cmd.creature.create", name, type);
    }

    @CommandMethod("bc creature delete <creature>")
    public void creatureDelete(Audience sender, @Argument(value = "creature", suggestions = "creature") String name) {
        Optional<PersistentCreature> creature = manager.creature(name);
        if (creature.isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.args.creature", name);
            return;
        }

        manager.remove(creature.get());
        I18nAPI.get(this).send(sender, "cmd.creature.delete", name);
    }

    @CommandMethod("bc spawn create <name> <creature>")
    public void spawnCreate(Player sender, @Argument(value = "name") String name, @Argument(value = "creature", suggestions = "creature") String creature) {
        if ( manager.spawn(name).isPresent() ) {
            I18nAPI.get(this).send(sender, "cmd.spawn.create.invalid", name);
            return;
        }

        Optional<PersistentCreature> creatureOptional = manager.creature(creature);
        if (creatureOptional.isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.args.creature", name);
            return;
        }

        manager.persist(name, creatureOptional.get(), manager.position(sender.getPosition()), sender.getInstance());
        I18nAPI.get(this).send(sender, "cmd.spawn.create", name, creatureOptional.get().name());
    }

    @CommandMethod("bc spawn delete <spawn>")
    public void spawnDelete(Audience sender, @Argument(value = "spawn", suggestions = "spawn") String name) {
        Optional<PersistentSpawn> spawn = manager.spawn(name);
        if (spawn.isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.args.spawn", name);
            return;
        }

        manager.remove(spawn.get());
        I18nAPI.get(this).send(sender, "cmd.spawn.delete", name);
    }
}

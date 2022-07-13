package com.guflimc.brick.creatures.minestom;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import com.google.gson.Gson;
import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.api.domain.TraitKey;
import com.guflimc.brick.creatures.common.BrickCreaturesConfig;
import com.guflimc.brick.creatures.common.BrickCreaturesDatabaseContext;
import com.guflimc.brick.creatures.common.arguments.CreatureArgument;
import com.guflimc.brick.creatures.common.arguments.TraitKeyArgument;
import com.guflimc.brick.creatures.common.commands.CreatureCommands;
import com.guflimc.brick.creatures.minestom.api.MinestomCreatureAPI;
import com.guflimc.brick.creatures.minestom.api.domain.MinestomCreature;
import com.guflimc.brick.creatures.minestom.arguments.MetadataMethodArgument;
import com.guflimc.brick.creatures.minestom.commands.MinestomCreatureCommands;
import com.guflimc.brick.creatures.minestom.traits.LookAtClosestPlayer;
import com.guflimc.brick.i18n.minestom.api.MinestomI18nAPI;
import com.guflimc.brick.i18n.minestom.api.namespace.MinestomNamespace;
import com.guflimc.cloud.minestom.MinestomCommandManager;
import io.leangen.geantyref.TypeToken;
import net.minestom.server.command.CommandSender;
import net.minestom.server.extensions.Extension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.function.Function;

public class MinestomBrickCreatures extends Extension {

    private static final Gson gson = new Gson();

    private BrickCreaturesDatabaseContext databaseContext;
    private MinestomCommandManager<CommandSender> commandManager;

    @Override
    public void initialize() {
        getLogger().info("Enabling " + nameAndVersion() + ".");

        // load config
        BrickCreaturesConfig config;
        try (
                InputStream is = getResource("config.json");
                InputStreamReader isr = new InputStreamReader(is)
        ) {
            config = gson.fromJson(isr, BrickCreaturesConfig.class);
        } catch (IOException e) {
            getLogger().error("Cannot load configuration.", e);
            return;
        }

        // DATABASE
        databaseContext = new BrickCreaturesDatabaseContext(config.database);

        MinestomBrickCreatureManager manager = new MinestomBrickCreatureManager(databaseContext);
        MinestomCreatureAPI.registerManager(manager);

        // TRAITS
        manager.registerTrait(LookAtClosestPlayer.KEY);

        // LOAD CREATURES
        manager.reload();

        // TRANSLATIONS
        MinestomNamespace namespace = new MinestomNamespace(this, Locale.ENGLISH);
        namespace.loadValues(this, "languages");
        MinestomI18nAPI.get().register(namespace);

        // COMMAND MANAGER
        commandManager = new MinestomCommandManager<>(
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity()
        );

        commandManager.parserRegistry().registerParserSupplier(TypeToken.get(Creature.class), parserParameters ->
                new CreatureArgument.CreatureParser<>());

        commandManager.parserRegistry().registerParserSupplier(TypeToken.get(MinestomCreature.class), parserParameters ->
                new CreatureArgument.CreatureParser<>());

        commandManager.parserRegistry().registerParserSupplier(TypeToken.get(TraitKey.class), parserParameters ->
                new TraitKeyArgument.TraitKeyParser<>());

        commandManager.parserRegistry().registerNamedParserSupplier("metadata", parserParameters ->
                new MetadataMethodArgument.MetadataMethodParser<>());

        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                commandManager,
                CommandSender.class,
                parameters -> SimpleCommandMeta.empty()
        );

        annotationParser.parse(new CreatureCommands(manager));
        annotationParser.parse(new MinestomCreatureCommands(manager));

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void terminate() {

        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getOrigin().getName() + " v" + getOrigin().getVersion();
    }

}

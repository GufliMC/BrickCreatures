package com.guflimc.brick.creatures.minestom;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import com.google.gson.Gson;
import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.common.BrickCreaturesConfig;
import com.guflimc.brick.creatures.common.BrickDatabaseContext;
import com.guflimc.brick.creatures.common.metadata.MetadataSerializer;
import com.guflimc.brick.creatures.minestom.api.MinestomCreaturesAPI;
import com.guflimc.brick.creatures.minestom.arguments.PersistentCreatureArgument;
import com.guflimc.brick.creatures.minestom.commands.MinestomCreaturesCommands;
import com.guflimc.brick.creatures.minestom.metadata.MinestomMetadataSerializer;
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

    private BrickDatabaseContext databaseContext;
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
        databaseContext = new BrickDatabaseContext(config.database);
        MetadataSerializer.SERIALIZER = new MinestomMetadataSerializer();

        MinestomBrickCreatureManager manager = new MinestomBrickCreatureManager(databaseContext);
        MinestomCreaturesAPI.registerManager(manager);

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

        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(PersistentCreature.class), parserParameters ->
                new PersistentCreatureArgument.CreatureParser<>());

        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                commandManager,
                CommandSender.class,
                parameters -> SimpleCommandMeta.empty()
        );

        annotationParser.parse(new MinestomCreaturesCommands(manager));

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

package com.guflimc.brick.creatures.spigot;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import com.google.gson.Gson;
import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.common.BrickCreaturesConfig;
import com.guflimc.brick.creatures.common.BrickCreaturesDatabaseContext;
import com.guflimc.brick.creatures.common.arguments.CreatureArgument;
import com.guflimc.brick.creatures.common.commands.CreatureCommands;
import com.guflimc.brick.creatures.spigot.api.SpigotCreatureAPI;
import com.guflimc.brick.creatures.spigot.api.domain.SpigotCreature;
import com.guflimc.brick.creatures.spigot.arguments.MetadataKeyArgument;
import com.guflimc.brick.creatures.spigot.commands.SpigotCreatureCommands;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Locale;
import java.util.function.Function;

public class SpigotBrickCreatures extends JavaPlugin {

    private static final Gson gson = new Gson();

    private BrickCreaturesConfig config;
    private BrickCreaturesDatabaseContext databaseContext;
    private SpigotBrickCreatureManager manager;

    @Override
    public void onEnable() {
        saveResource("config.json", false);
        try (
                InputStream is = new FileInputStream(new File(getDataFolder(), "config.json"));
                InputStreamReader isr = new InputStreamReader(is)
        ) {
            config = gson.fromJson(isr, BrickCreaturesConfig.class);
        } catch (IOException e) {
            getLogger().warning("Cannot load configuration.");
            return;
        }

        // DATABASE
        databaseContext = new BrickCreaturesDatabaseContext(config.database);

        // MANAGER
        manager = new SpigotBrickCreatureManager(databaseContext);
        SpigotCreatureAPI.registerManager(manager);

        // TRANSLATIONS
        SpigotNamespace namespace = new SpigotNamespace(this, Locale.ENGLISH);
        namespace.loadValues(this, "languages");
        SpigotI18nAPI.get().register(namespace);

        // COMMANDS
        setupCommands();

        // EVENTS
        PluginManager pm = getServer().getPluginManager();


        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void onDisable() {
        if (databaseContext != null) {
            databaseContext.shutdown();
        }

        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getDescription().getName() + " v" + getDescription().getVersion();
    }

    private void setupCommands() {
        // COMMANDS
        try {
            BukkitCommandManager<CommandSender> commandManager = new BukkitCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );

            commandManager.parserRegistry().registerParserSupplier(TypeToken.get(Creature.class),
                    ps -> new CreatureArgument.CreatureParser<>());

            commandManager.parserRegistry().registerParserSupplier(TypeToken.get(SpigotCreature.class),
                    ps -> new CreatureArgument.CreatureParser<>());

            commandManager.parserRegistry().registerNamedParserSupplier("metadata", parserParameters ->
                    new MetadataKeyArgument.MetadataKeyParser<>());


            AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                    commandManager,
                    CommandSender.class,
                    parameters -> SimpleCommandMeta.empty()
            );

            annotationParser.parse(new CreatureCommands());
            annotationParser.parse(new SpigotCreatureCommands());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

package com.guflimc.brick.creatures.spigot.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.guflimc.brick.creatures.spigot.api.domain.SpigotCreature;
import com.guflimc.brick.creatures.spigot.proxy.SpigotCreatureProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiFunction;

public final class MetadataKeyArgument<C> extends CommandArgument<C, String> {

    private MetadataKeyArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                    List<String>> suggestionsProvider
    ) {
        super(required, name, new MetadataKeyParser<>(), defaultValue, String.class, suggestionsProvider);
    }

    //

    public static final class MetadataKeyParser<C> implements ArgumentParser<C, String> {

        @Override
        public @NotNull ArgumentParseResult<String> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        MetadataKeyParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            Optional<SpigotCreature> creature = commandContext.getOptional("creature");
            if (creature.isEmpty()) {
                return ArgumentParseResult.failure(new Exception("creature is invalid."));
            }

            SpigotCreatureProxy proxy = (SpigotCreatureProxy) creature.get();
            String key = proxy.nbt().getKeys().stream().filter(k -> k.equalsIgnoreCase(input))
                    .findFirst().orElse(null);

            if (key == null) {
                return ArgumentParseResult.failure(new Exception("Metadata key cannot be set: " + input + "."));
            }

            return ArgumentParseResult.success(key);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            Optional<SpigotCreature> creature = commandContext.getOptional("creature");
            if (creature.isEmpty()) {
                return Collections.emptyList();
            }

            SpigotCreatureProxy proxy = (SpigotCreatureProxy) creature.get();
            return List.copyOf(proxy.nbt().getKeys());
        }

    }

}
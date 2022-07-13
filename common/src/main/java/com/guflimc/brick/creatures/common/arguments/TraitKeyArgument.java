package com.guflimc.brick.creatures.common.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.guflimc.brick.creatures.api.CreatureAPI;
import com.guflimc.brick.creatures.api.domain.TraitKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class TraitKeyArgument<C> extends CommandArgument<C, TraitKey> {

    private TraitKeyArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider
    ) {
        super(required, name, new TraitKeyParser<>(), defaultValue, TraitKey.class, suggestionsProvider);
    }

    //

    public static final class TraitKeyParser<C> implements ArgumentParser<C, TraitKey> {

        @Override
        public @NotNull ArgumentParseResult<TraitKey> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        TraitKeyParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            TraitKey<?> trait = CreatureAPI.get().findTrait(input).orElse(null);
            if (trait == null) {
                return ArgumentParseResult.failure(new Exception("Cannot find trait with name: " + input + "."));
            }

            return ArgumentParseResult.success(trait);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();

            for (TraitKey<?> trait : CreatureAPI.get().traits()) {
                output.add(trait.name());
            }

            return output;
        }

    }

}
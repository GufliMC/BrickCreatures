package com.guflimc.brick.creatures.common.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.guflimc.brick.creatures.api.CreatureAPI;
import com.guflimc.brick.creatures.api.domain.Creature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class CreatureArgument<C> extends CommandArgument<C, Creature> {

    private CreatureArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                    List<String>> suggestionsProvider
    ) {
        super(required, name, new CreatureParser<>(), defaultValue, Creature.class, suggestionsProvider);
    }

    //

    public static final class CreatureParser<C> implements ArgumentParser<C, Creature> {

        @Override
        public @NotNull ArgumentParseResult<Creature> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        CreatureParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            Creature creature = CreatureAPI.get().findCreature(input).orElse(null);
            if (creature == null) {
                return ArgumentParseResult.failure(new Exception("Cannot find creature with name: " + input + "."));
            }

            return ArgumentParseResult.success(creature);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();

            for (Creature creature : CreatureAPI.get().creatures()) {
                output.add(creature.name());
            }

            return output;
        }

    }

}
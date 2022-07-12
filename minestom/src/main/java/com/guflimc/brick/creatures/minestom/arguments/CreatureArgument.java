package com.guflimc.brick.creatures.minestom.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.minestom.api.MinestomCreaturesAPI;
import com.guflimc.cloud.minestom.caption.MinestomCaptionKeys;
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

    public static <C> Builder<C> newBuilder(final @NotNull String name) {
        return new Builder<>(name);
    }

    public static <C> CommandArgument<C, Creature> of(final @NotNull String name) {
        return CreatureArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> CommandArgument<C, Creature> optional(final @NotNull String name) {
        return CreatureArgument.<C>newBuilder(name).asOptional().build();
    }

    public static <C> CommandArgument<C, Creature> optional(
            final @NotNull String name,
            final @NotNull String defaultPlayer
    ) {
        return CreatureArgument.<C>newBuilder(name).asOptionalWithDefault(defaultPlayer).build();
    }

    //

    public static final class Builder<C> extends CommandArgument.Builder<C, Creature> {

        private Builder(final @NotNull String name) {
            super(Creature.class, name);
        }

        @Override
        public @NotNull CreatureArgument<C> build() {
            return new CreatureArgument<>(this.isRequired(), this.getName(), this.getDefaultValue(), this.getSuggestionsProvider());
        }

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

            Creature creature = MinestomCreaturesAPI.get().find(input).orElse(null);
            if (creature == null) {
                return ArgumentParseResult.failure(new CreatureParseException(input, commandContext));
            }

            return ArgumentParseResult.success(creature);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();

            for ( Creature creature : MinestomCreaturesAPI.get().creatures() ) {
                output.add(creature.name());
            }

            return output;
        }

    }

    //

    public static final class CreatureParseException extends ParserException {

        private static final long serialVersionUID = -6910194590022835513L;
        private final String input;

        public CreatureParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    CreatureParser.class,
                    context,
                    MinestomCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER,
                    CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        public @NotNull String getInput() {
            return input;
        }

    }

}
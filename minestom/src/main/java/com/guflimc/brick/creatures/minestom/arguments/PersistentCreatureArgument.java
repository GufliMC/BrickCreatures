package com.guflimc.brick.creatures.minestom.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.minestom.api.MinestomCreaturesAPI;
import com.guflimc.cloud.minestom.caption.MinestomCaptionKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class PersistentCreatureArgument<C> extends CommandArgument<C, PersistentCreature> {

    private PersistentCreatureArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                                List<String>> suggestionsProvider
    ) {
        super(required, name, new CreatureParser<>(), defaultValue, PersistentCreature.class, suggestionsProvider);
    }

    //

    public static <C> Builder<C> newBuilder(final @NotNull String name) {
        return new Builder<>(name);
    }

    public static <C> CommandArgument<C, PersistentCreature> of(final @NotNull String name) {
        return PersistentCreatureArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> CommandArgument<C, PersistentCreature> optional(final @NotNull String name) {
        return PersistentCreatureArgument.<C>newBuilder(name).asOptional().build();
    }

    public static <C> CommandArgument<C, PersistentCreature> optional(
            final @NotNull String name,
            final @NotNull String defaultPlayer
    ) {
        return PersistentCreatureArgument.<C>newBuilder(name).asOptionalWithDefault(defaultPlayer).build();
    }

    //

    public static final class Builder<C> extends CommandArgument.Builder<C, PersistentCreature> {

        private Builder(final @NotNull String name) {
            super(PersistentCreature.class, name);
        }

        @Override
        public @NotNull PersistentCreatureArgument<C> build() {
            return new PersistentCreatureArgument<>(this.isRequired(), this.getName(), this.getDefaultValue(), this.getSuggestionsProvider());
        }

    }

    //

    public static final class CreatureParser<C> implements ArgumentParser<C, PersistentCreature> {

        @Override
        public @NotNull ArgumentParseResult<PersistentCreature> parse(
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

            PersistentCreature creature = MinestomCreaturesAPI.get().creature(input).orElse(null);
            if (creature == null) {
                return ArgumentParseResult.failure(new PersistentCreatureParseException(input, commandContext));
            }

            return ArgumentParseResult.success(creature);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();

            for ( PersistentCreature creature : MinestomCreaturesAPI.get().creatures() ) {
                output.add(creature.name());
            }

            return output;
        }

    }

    //

    public static final class PersistentCreatureParseException extends ParserException {

        private static final long serialVersionUID = -6910194590022835513L;
        private final String input;

        public PersistentCreatureParseException(
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
package com.guflimc.brick.creatures.minestom.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.guflimc.brick.creatures.minestom.api.domain.MinestomCreature;
import com.guflimc.cloud.minestom.caption.MinestomCaptionKeys;
import net.minestom.server.entity.metadata.EntityMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public final class MetadataMethodArgument<C> extends CommandArgument<C, Method> {

    private MetadataMethodArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                    List<String>> suggestionsProvider
    ) {
        super(required, name, new MetadataMethodParser<>(), defaultValue, Method.class, suggestionsProvider);
    }

    //

    public static final class MetadataMethodParser<C> implements ArgumentParser<C, Method> {

        @Override
        public @NotNull ArgumentParseResult<Method> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        MetadataMethodParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            Optional<MinestomCreature> creature = commandContext.getOptional("creature");
            if (creature.isEmpty()) {
                return ArgumentParseResult.failure(new Exception("creature is invalid."));
            }

            EntityMeta em = creature.get().entity().getEntityMeta();
            Method method = Arrays.stream(em.getClass().getMethods())
                    .filter(m -> m.getName().equals("set" + input))
                    .findFirst().orElse(null);

            if (method == null) {
                return ArgumentParseResult.failure(new Exception("Metadata key cannot be set: " + input + "."));
            }

            return ArgumentParseResult.success(method);
        }

        static Map<Class<?>, List<String>> suggestionCache = new ConcurrentHashMap<>();

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            Optional<MinestomCreature> creature = commandContext.getOptional("creature");
            if (creature.isEmpty()) {
                return Collections.emptyList();
            }

            EntityMeta em = creature.get().entity().getEntityMeta();
            if ( suggestionCache.containsKey(em.getClass()) ) {
                return suggestionCache.get(em.getClass());
            }

            List<String> suggestions = Arrays.stream(em.getClass().getMethods())
                    .filter(m -> m.getName().startsWith("set"))
                    .map(m -> m.getName().substring(3))
                    .toList();

            suggestionCache.put(em.getClass(), suggestions);

            return suggestions;
        }

    }

}
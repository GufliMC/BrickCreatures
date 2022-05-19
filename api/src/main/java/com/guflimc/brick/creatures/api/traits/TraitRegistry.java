package com.guflimc.brick.creatures.api.traits;

import com.guflimc.brick.creatures.api.domain.Trait;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class TraitRegistry {

    private static final Set<Trait> traits = new CopyOnWriteArraySet<>();

    public static void register(Trait trait) {
        traits.add(trait);
    }

    public static void unregister(Trait trait) {
        traits.remove(trait);
    }

    public static Collection<Trait> traits() {
        return Collections.unmodifiableSet(traits);
    }

    public static Optional<Trait> find(String id) {
        return traits.stream().filter(trait -> trait.id().equals(id)).findFirst();
    }

}

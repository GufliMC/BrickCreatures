package com.guflimc.brick.creatures.minestom.api.domain;

import com.guflimc.brick.creatures.api.domain.TraitLifecycle;

public abstract class MinestomTraitLifecycle extends TraitLifecycle<MinestomCreature> {

    protected MinestomTraitLifecycle(MinestomCreature creature) {
        super(creature);
    }

}

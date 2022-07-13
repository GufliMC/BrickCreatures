package com.guflimc.brick.creatures.api;

import org.jetbrains.annotations.ApiStatus;

public class CreatureAPI {

    private CreatureAPI() {}

    private static CreatureManager<?, ?> creatureManager;

    @ApiStatus.Internal
    public static void setCreatureManager(CreatureManager<?, ?> manager) {
        creatureManager = manager;
    }

    //

    public static CreatureManager<?, ?> get() {
        return creatureManager;
    }
    
}

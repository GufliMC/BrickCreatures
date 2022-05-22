package com.guflimc.brick.creatures.minestom.api;

import org.jetbrains.annotations.ApiStatus;

public class MinestomCreaturesAPI {

    private static MinestomCreatureManager creatureManager;

    @ApiStatus.Internal
    public static void registerManager(MinestomCreatureManager manager) {
        creatureManager = manager;
    }

    //

    public static MinestomCreatureManager get() {
        return creatureManager;
    }

}

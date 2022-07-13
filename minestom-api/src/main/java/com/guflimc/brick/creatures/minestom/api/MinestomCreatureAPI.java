package com.guflimc.brick.creatures.minestom.api;

import com.guflimc.brick.creatures.api.CreatureAPI;
import org.jetbrains.annotations.ApiStatus;

public class MinestomCreatureAPI {

    private static MinestomCreatureManager creatureManager;

    @ApiStatus.Internal
    public static void registerManager(MinestomCreatureManager manager) {
        CreatureAPI.setCreatureManager(manager);
        creatureManager = manager;
    }

    //

    public static MinestomCreatureManager get() {
        return creatureManager;
    }

}

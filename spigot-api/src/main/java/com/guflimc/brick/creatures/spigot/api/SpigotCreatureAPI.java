package com.guflimc.brick.creatures.spigot.api;

import com.guflimc.brick.creatures.api.CreatureAPI;
import org.jetbrains.annotations.ApiStatus;

public class SpigotCreatureAPI {

    private static SpigotCreatureManager creatureManager;

    @ApiStatus.Internal
    public static void registerManager(SpigotCreatureManager manager) {
        CreatureAPI.setCreatureManager(manager);
        creatureManager = manager;
    }

    //

    public static SpigotCreatureManager get() {
        return creatureManager;
    }

}

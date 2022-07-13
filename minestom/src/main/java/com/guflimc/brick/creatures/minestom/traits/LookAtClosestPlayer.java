package com.guflimc.brick.creatures.minestom.traits;

import com.guflimc.brick.creatures.api.domain.TraitKey;
import com.guflimc.brick.creatures.minestom.api.domain.MinestomCreature;
import com.guflimc.brick.creatures.minestom.api.domain.MinestomTraitLifecycle;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.Comparator;

public class LookAtClosestPlayer extends MinestomTraitLifecycle {

    public static final TraitKey<MinestomCreature> KEY = new TraitKey<>("LookAtClosestPlayer", LookAtClosestPlayer::new);

    private Player player;
    private int count;

    private LookAtClosestPlayer(MinestomCreature creature) {
        super(creature);
    }

    @Override
    public void onTick() {
        count++;

        if (player == null || count >= 20) {
            player = MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                    .min(Comparator.comparingDouble(p -> p.getPosition().distance(creature.entity().getPosition())))
                    .orElse(null);
            count = 0;
        }

        if ( player == null ) {
            return;
        }

        if (!player.isOnline()) {
            player = null;
            return;
        }

        creature.entity().lookAt(player.getPosition().add(0, 0.8, 0));
    }
}

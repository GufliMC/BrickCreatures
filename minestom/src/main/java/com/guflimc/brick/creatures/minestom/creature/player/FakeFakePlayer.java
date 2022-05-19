package com.guflimc.brick.creatures.minestom.creature.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.PlayerInfoPacket;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.time.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FakeFakePlayer extends EntityCreature {

    private final String username;
    private final Entity passenger;

    private PlayerSkin skin;

    public FakeFakePlayer() {
        super(EntityType.PLAYER, UUID.randomUUID());
        this.username = RandomStringUtils.randomAlphanumeric(8);

        setBoundingBox(0.6f, 1.8f, 0.6f);
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2f);

        // hide default nametag
        passenger = new Entity(EntityType.ARMOR_STAND);
        ArmorStandMeta armorStandMeta = (ArmorStandMeta) passenger.getEntityMeta();
        armorStandMeta.setNotifyAboutChanges(false);
        armorStandMeta.setSmall(true);
        armorStandMeta.setInvisible(true);
        passenger.setAutoViewable(true);

        // send add player packet
        PacketUtils.broadcastPacket(getAddPlayerPacket());
    }

    private Pos nametagPositionFrom(Pos playerPosition) {
        return playerPosition.add(0, 1.8, 0);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance) {
        passenger.setInstance(instance);
        return super.setInstance(instance).thenRun(() -> {
            addPassenger(passenger);
        });
    }

    @Override
    public void remove() {
        super.remove();
        passenger.remove();
    }

    protected @NotNull PlayerInfoPacket getAddPlayerPacket() {
        List<PlayerInfoPacket.AddPlayer.Property> properties = new ArrayList<>();

        if ( this.skin != null ) {
            properties.add(new PlayerInfoPacket.AddPlayer.Property("textures", skin.textures(), skin.signature()));
        }

        return new PlayerInfoPacket(PlayerInfoPacket.Action.ADD_PLAYER,
                new PlayerInfoPacket.AddPlayer(getUuid(), username, properties, GameMode.SURVIVAL, 0, getCustomName()));
    }

    /**
     * Gets the packet to remove the player from the tab-list.
     *
     * @return a {@link PlayerInfoPacket} to remove the player
     */
    protected @NotNull PlayerInfoPacket getRemovePlayerPacket() {
        return new PlayerInfoPacket(PlayerInfoPacket.Action.REMOVE_PLAYER, new PlayerInfoPacket.RemovePlayer(getUuid()));
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        player.sendPacket(getAddPlayerPacket());
        super.updateNewViewer(player);

        // remove from tablist
        MinecraftServer.getSchedulerManager().buildTask(() -> player.sendPacket(getRemovePlayerPacket()))
                .delay(20, TimeUnit.SERVER_TICK).schedule();
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        player.sendPacket(getRemovePlayerPacket());
        super.updateOldViewer(player);
    }

    /**
     * Gets the player skin.
     *
     * @return the player skin object,
     * null means that the player has his {@link #getUuid()} default skin
     */
    public @Nullable PlayerSkin getSkin() {
        return skin;
    }

    /**
     * Changes the player skin.
     * <p>
     * This does remove the player for all viewers to spawn it again with the correct new skin.
     */
    public synchronized void setSkin(@Nullable PlayerSkin skin) {
        this.skin = skin;
        if (instance == null)
            return;

        getViewers().forEach(this::updateOldViewer);
        getViewers().forEach(this::updateNewViewer);

        teleport(getPosition());
    }

}

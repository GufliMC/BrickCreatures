package com.guflimc.brick.creatures.minestom.entity;

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

public class CreatureHuman extends CreatureEntity {

    private final String username;
    private String skinTexture;
    private String skinSignature;

    public CreatureHuman() {
        super(EntityType.PLAYER, UUID.randomUUID());
        this.username = RandomStringUtils.randomAlphanumeric(8);

        setBoundingBox(0.6f, 1.8f, 0.6f);
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2f);

        // send add player packet
        PacketUtils.broadcastPacket(getAddPlayerPacket());
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
     * Changes the player skin.
     * <p>
     * This does remove the player for all viewers to spawn it again with the correct new skin.
     */
    public synchronized void setSkin(@Nullable String skinTexture, @Nullable String skinSignature) {
        this.skinTexture = skinTexture;
        this.skinSignature = skinSignature;

        if (instance == null)
            return;

        getViewers().forEach(this::updateOldViewer);
        getViewers().forEach(this::updateNewViewer);

        teleport(getPosition());
    }

    // PACKETS

    protected @NotNull PlayerInfoPacket getAddPlayerPacket() {
        List<PlayerInfoPacket.AddPlayer.Property> properties = new ArrayList<>();

        if ( this.skinTexture != null ) {
            properties.add(new PlayerInfoPacket.AddPlayer.Property("textures", skinTexture, skinSignature));
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

}

package com.guflimc.brick.creatures.minestom.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.PlayerInfoPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.time.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreatureHuman extends CreatureEntity {

    private static int COUNTER = 0;

    private final String username;
    private Component customName;

    private String skinTextures;
    private String skinSignature;

    private final Team team;

    public CreatureHuman() {
        super(EntityType.PLAYER, UUID.randomUUID());
        this.username = invisName();

        team = MinecraftServer.getTeamManager().createTeam(RandomStringUtils.randomAlphanumeric(8));
        team.setNameTagVisibility(TeamsPacket.NameTagVisibility.NEVER);
        team.addMember(username);

        setBoundingBox(0.6f, 1.8f, 0.6f);
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2f);

        // send add player packet
        PacketUtils.broadcastPacket(getAddPlayerPacket());
    }

    private static String invisName() {
        String str = Integer.toHexString(++COUNTER);
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append("\u00A7").append(c);
        }
        return sb.toString().trim();
    }

    @Override
    public void despawn() {
        super.despawn();

        MinecraftServer.getTeamManager().deleteTeam(team);
    }

    @Override
    public void update(long time) {
        super.update(time);

        if (getCustomName() != customName) {
            customName = getCustomName();

            team.setNameTagVisibility(customName != null ? TeamsPacket.NameTagVisibility.ALWAYS : TeamsPacket.NameTagVisibility.NEVER);
            team.updatePrefix(customName);
        }
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
    public synchronized void setSkin(@Nullable String skinTextures, @Nullable String skinSignature) {
        this.skinTextures = skinTextures;
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

        if (this.skinTextures != null) {
            properties.add(new PlayerInfoPacket.AddPlayer.Property("textures", skinTextures, skinSignature));
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

package com.guflimc.brick.creatures.minestom.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.minestom.MinestomBrickCreatureManager;
import com.guflimc.brick.creatures.minestom.api.domain.MinestomCreature;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.maths.minestom.api.MinestomMaths;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.position.PositionUtils;

import java.util.stream.Collectors;

public class MinestomCreaturesCommands {

    private final MinestomBrickCreatureManager manager;

    public MinestomCreaturesCommands(MinestomBrickCreatureManager manager) {
        this.manager = manager;
    }

    @CommandMethod("bc reload")
    public void reload(Audience sender) {
        manager.reload();
        I18nAPI.get(this).send(sender, "cmd.reload");
    }

    @CommandMethod("bc create <name> <type>")
    public void creatureCreate(Player sender,
                               @Argument(value = "name") String name,
                               @Argument(value = "type") EntityType type
    ) {
        if (manager.find(name).isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.create.invalid", name);
            return;
        }

        try {
            MinestomCreature mc = manager.create(name, type);

            mc.setPosition(MinestomMaths.toPosition(sender.getPosition()));
            mc.setInstance(sender.getInstance());

            manager.persist(mc);

            I18nAPI.get(this).send(sender, "cmd.create", name, type.name());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @CommandMethod("bc delete <creature>")
    public void creatureDelete(Audience sender,
                               @Argument(value = "creature") MinestomCreature creature
    ) {
        manager.remove(creature);
        I18nAPI.get(this).send(sender, "cmd.delete", creature.name());
    }

    @CommandMethod("bc list")
    public void creatureList(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.list",
                manager.creatures().stream().map(Creature::name).collect(Collectors.toList()));
    }

    @CommandMethod("bc edit tphere <creature>")
    public void creatureTeleporthere(Player sender,
                                     @Argument(value = "creature") MinestomCreature creature
    ) {
        creature.setPosition(MinestomMaths.toPosition(sender.getPosition()));
        creature.setInstance(sender.getInstance());

        manager.merge(creature);
        I18nAPI.get(this).send(sender, "cmd.edit.teleporthere", creature.name());
    }

    @CommandMethod("bc edit lookhere <creature>")
    public void creatureLookhere(Player sender,
                                 @Argument(value = "creature") MinestomCreature creature) {

        EntityType type = creature.entity().getEntityType();

        Pos playerpos = sender.getPosition().add(0, 1.6, 0);
        Pos entitypos = MinestomMaths.toPos(creature.position()).add(0, type.height() * 0.85, 0);
        Vec delta = playerpos.sub(entitypos).asVec().normalize();

        Pos result = MinestomMaths.toPos(creature.position());
        result = result.withYaw(PositionUtils.getLookYaw(delta.x(), delta.z()));
        result = result.withPitch(PositionUtils.getLookPitch(delta.x(), delta.y(), delta.z()));

        creature.setPosition(MinestomMaths.toPosition(result));
        manager.merge(creature);

        I18nAPI.get(this).send(sender, "cmd.edit.lookhere", creature.name());
    }
}

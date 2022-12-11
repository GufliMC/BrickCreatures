package com.guflimc.brick.creatures.common.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.guflimc.brick.creatures.api.CreatureAPI;
import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.i18n.api.I18nAPI;
import net.kyori.adventure.audience.Audience;

import java.util.stream.Collectors;

public class CreatureCommands {

    @CommandMethod("bc delete <creature>")
    @CommandPermission("brick.creatures.delete")
    public void creatureDelete(Audience sender,
                               @Argument(value = "creature") Creature creature
    ) {
        CreatureAPI.get().remove(creature);
        I18nAPI.get(this).send(sender, "cmd.delete", creature.name());
    }

    @CommandMethod("bc list")
    @CommandPermission("brick.creatures.list")
    public void creatureList(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.list",
                CreatureAPI.get().creatures().stream().map(Creature::name).collect(Collectors.toList()));
    }

}

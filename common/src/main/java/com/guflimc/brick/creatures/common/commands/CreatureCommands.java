package com.guflimc.brick.creatures.common.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import com.guflimc.brick.creatures.api.CreatureManager;
import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.api.domain.TraitKey;
import com.guflimc.brick.i18n.api.I18nAPI;
import net.kyori.adventure.audience.Audience;

import java.util.stream.Collectors;

public class CreatureCommands {

    private final CreatureManager<?, ?> manager;

    public CreatureCommands(CreatureManager<?, ?> manager) {
        this.manager = manager;
    }

    @CommandMethod("bc reload")
    public void reload(Audience sender) {
        manager.reload();
        I18nAPI.get(this).send(sender, "cmd.reload");
    }

    @CommandMethod("bc delete <creature>")
    public void creatureDelete(Audience sender,
                               @Argument(value = "creature") Creature creature
    ) {
        manager.remove(creature);
        I18nAPI.get(this).send(sender, "cmd.delete", creature.name());
    }

    @CommandMethod("bc list")
    public void creatureList(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.list",
                manager.creatures().stream().map(Creature::name).collect(Collectors.toList()));
    }

    @CommandMethod("bc trait list <creature>")
    public void traitList(Audience sender,
                                  @Argument(value = "creature") Creature creature
    ) {
        I18nAPI.get(this).send(sender, "cmd.trait.list", creature.name(),
                creature.traits().stream().map(TraitKey::name).collect(Collectors.toList()));
    }

    @CommandMethod("bc trait add <creature> <trait>")
    public void traitAdd(Audience sender,
                          @Argument(value = "creature") Creature creature,
                         @Argument(value = "trait") TraitKey trait
    ) {
        creature.addTrait(trait);
        manager.merge(creature);
        I18nAPI.get(this).send(sender, "cmd.trait.add", creature.name(), trait.name());
    }

    @CommandMethod("bc trait remove <creature> <trait>")
    public void traitRemove(Audience sender,
                         @Argument(value = "creature") Creature creature,
                         @Argument(value = "trait") TraitKey trait
    ) {
        creature.removeTrait(trait);
        manager.merge(creature);
        I18nAPI.get(this).send(sender, "cmd.trait.remove", creature.name(), trait.name());
    }

}

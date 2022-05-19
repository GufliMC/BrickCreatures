package com.guflimc.brick.creatures.api.domain;

import com.guflimc.brick.creatures.api.meta.PlayerSkin;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface PersistentCreature {

    String name();

    String type();

    Component hologram();

    void setHologram(Component hologram);

    PlayerSkin skin();

    void setSkin(PlayerSkin skin);

    Object metadata();

    void setMetadata(Object metadata);

    List<String> traits();

    void addTrait(Trait trait);

    void removeTrait(Trait trait);

}

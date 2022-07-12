package com.guflimc.brick.creatures.api.domain;

import com.guflimc.brick.maths.api.geo.Position;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.List;
import java.util.UUID;

public interface Creature {

    UUID id();

    String name();

    Position position();

    void setPosition(Position position);

    NBTCompound nbt();

    void setNBT(NBTCompound nbt);

    List<String> traits();

    void addTrait(String trait);

    void removeTrait(String trait);

}

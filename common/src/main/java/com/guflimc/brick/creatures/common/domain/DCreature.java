package com.guflimc.brick.creatures.common.domain;

import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.creatures.common.converters.NBTConverter;
import com.guflimc.brick.maths.api.geo.Location;
import com.guflimc.brick.maths.api.geo.Position;
import com.guflimc.brick.maths.database.api.LocationConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "creatures", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class DCreature implements Creature {

    @Id
    @Basic
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Convert(converter = LocationConverter.class)
    @Column(nullable = false)
    public Location location = new Location(null, 0, 0, 0, 0, 0);

    @Convert(converter = NBTConverter.class)
    private NBTCompound nbt;

    @OneToMany(targetEntity = DCreatureTrait.class, mappedBy = "creature",
            orphanRemoval = true, fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private final List<DCreatureTrait> traits = new ArrayList<>();

    //

    private DCreature() {}

    public DCreature(String type) {
        this.type = type;
    }

    public DCreature(String name, String type) {
        this(type);
        this.name = name;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Position position() {
        return location;
    }

    @Override
    public void setPosition(Position position) {
        this.location = location.withPosition(position);
    }

    @Override
    public NBTCompound nbt() {
        return nbt;
    }

    @Override
    public void setNBT(NBTCompound nbt) {
        this.nbt = nbt;
    }

    public String type() {
        return type;
    }

    public List<String> traits() {
        return traits.stream().map(DCreatureTrait::trait).toList();
    }

    @Override
    public void addTrait(String trait) {
        traits.add(new DCreatureTrait(this, trait));
    }

    @Override
    public void removeTrait(String trait) {
        traits.removeIf(t -> t.trait().equals(trait));
    }

}

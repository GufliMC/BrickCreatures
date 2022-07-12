package com.guflimc.brick.creatures.common.domain;

import com.guflimc.brick.creatures.api.domain.Creature;
import com.guflimc.brick.maths.api.geo.Location;
import com.guflimc.brick.maths.api.geo.Position;
import com.guflimc.brick.maths.database.api.LocationConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
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
    private Location location = new Location(null, 0, 0, 0, 0, 0);

    @Column(length = 65565)
    private String metadata;

    @Column(length = 2048)
    private String humanSkinTextures;

    @Column(length = 2048)
    private String humanSkinSignature;

    @OneToMany(targetEntity = DCreatureTrait.class, mappedBy = "creature",
            orphanRemoval = true, fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private final List<DCreatureTrait> traits = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updateAt;

    //

    private DCreature() {
    }

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
        setLocation(location.withPosition(position));
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location location() {
        return location;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String metadata() {
        return metadata;
    }

    public String type() {
        return type;
    }

    public String humanSkinTextures() {
        return humanSkinTextures;
    }

    public String humanSkinSignature() {
        return humanSkinSignature;
    }

    @Override
    public void setHumanSkin(String textures, String signature) {
        this.humanSkinTextures = textures;
        this.humanSkinSignature = signature;
    }

    @Override
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

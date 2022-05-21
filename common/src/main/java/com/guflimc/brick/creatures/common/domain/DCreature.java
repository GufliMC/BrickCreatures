package com.guflimc.brick.creatures.common.domain;

import com.guflimc.brick.creatures.api.domain.PersistentCreature;
import com.guflimc.brick.creatures.api.domain.Trait;
import com.guflimc.brick.creatures.api.meta.PlayerSkin;
import com.guflimc.brick.creatures.common.converters.ComponentConverter;
import com.guflimc.brick.creatures.common.converters.MetadataConverter;
import com.guflimc.brick.creatures.common.converters.PlayerSkinConverter;
import jakarta.persistence.*;
import net.kyori.adventure.text.Component;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "creatures", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class DCreature implements PersistentCreature {

    @Id
    @Basic
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Convert(converter = ComponentConverter.class)
    private Component hologram;

    @Convert(converter = PlayerSkinConverter.class)
    @Column(length = 65535)
    private PlayerSkin skin;

    @Convert(converter = MetadataConverter.class)
    private Object metadata;

    @OneToMany(targetEntity = DCreatureTrait.class, mappedBy = "creature", orphanRemoval = true,
            cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    private final List<DCreatureTrait> traits = new ArrayList<>();

    //

    private DCreature() {}

    public DCreature(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    @Override
    public Component hologram() {
        return hologram;
    }

    @Override
    public PlayerSkin skin() {
        return skin;
    }

    @Override
    public void setSkin(PlayerSkin skin) {
        this.skin = skin;
    }

    @Override
    public Object metadata() {
        return metadata;
    }

    @Override
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    @Override
    public void setHologram(Component hologram) {
        this.hologram = hologram;
    }

    public List<String> traits() {
        return traits.stream().map(DCreatureTrait::traitId).toList();
    }

    @Override
    public void addTrait(Trait trait) {
        traits.add(new DCreatureTrait(this, trait.id()));
    }

    @Override
    public void removeTrait(Trait trait) {
        traits.removeIf(t -> t.traitId().equals(trait.id()));
    }


}

package com.guflimc.brick.creatures.common.domain;

import com.guflimc.brick.creatures.api.domain.PersistentSpawn;
import com.guflimc.brick.creatures.api.meta.Position;
import com.guflimc.brick.creatures.common.converters.PositionConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "spawns", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class DSpawn implements PersistentSpawn {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(targetEntity = DCreature.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DCreature creature;

    @Convert(converter = PositionConverter.class)
    @Column(nullable = false)
    private Position position;

    //

    private DSpawn() {}

    public DSpawn(String name, DCreature creature, Position position) {
        this.name = name;
        this.creature = creature;
        this.position = position;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public DCreature creature() {
        return creature;
    }

    @Override
    public Position position() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }


}

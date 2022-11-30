package com.guflimc.brick.creatures.common.domain;

import com.guflimc.brick.creatures.api.domain.Creature;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "creatures", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class DCreature implements Creature {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(length = 8192)
    private String metadata;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
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

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String metadata() {
        return metadata;
    }

    public String type() {
        return type;
    }

}

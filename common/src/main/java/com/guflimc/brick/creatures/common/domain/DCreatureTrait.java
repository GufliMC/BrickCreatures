package com.guflimc.brick.creatures.common.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "creature_traits",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"trait_id", "creature_id"})
        }
)
public class DCreatureTrait {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(targetEntity = DCreature.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DCreature creature;

    @Column(nullable = false, name = "trait_id")
    private String traitId;

    //

    private DCreatureTrait() {}

    DCreatureTrait(DCreature creature, String traitId) {
        this.creature = creature;
        this.traitId = traitId;
    }

    //

    public DCreature creature() {
        return creature;
    }

    public String traitId() {
        return traitId;
    }

}

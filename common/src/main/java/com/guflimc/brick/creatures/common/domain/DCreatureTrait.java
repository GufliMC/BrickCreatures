package com.guflimc.brick.creatures.common.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "creature_traits",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"trait", "creature_id"})
        }
)
public class DCreatureTrait {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = DCreature.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DCreature creature;

    @Column(nullable = false)
    private String trait;

    //

    private DCreatureTrait() {
    }

    DCreatureTrait(DCreature creature, String trait) {
        this.creature = creature;
        this.trait = trait;
    }

    //

    public DCreature creature() {
        return creature;
    }

    public String trait() {
        return trait;
    }

}

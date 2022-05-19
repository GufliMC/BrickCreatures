package com.guflimc.brick.creatures.common;

import com.guflimc.brick.creatures.common.database.HibernateConfig;
import com.guflimc.brick.creatures.common.database.HibernateDatabaseContext;
import com.guflimc.brick.creatures.common.domain.DCreature;
import com.guflimc.brick.creatures.common.domain.DCreatureTrait;
import com.guflimc.brick.creatures.common.domain.DSpawn;

public class BrickDatabaseContext extends HibernateDatabaseContext {

    public BrickDatabaseContext(HibernateConfig config) {
        super(config);
    }

    public BrickDatabaseContext(HibernateConfig config, int poolSize) {
        super(config, poolSize);
    }

    @Override
    protected Class<?>[] entityClasses() {
        return new Class[] {
                DCreature.class,
                DCreatureTrait.class,
                DSpawn.class
        };
    }

}

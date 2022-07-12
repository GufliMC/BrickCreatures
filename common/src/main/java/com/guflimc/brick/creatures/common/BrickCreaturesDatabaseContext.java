package com.guflimc.brick.creatures.common;

import com.guflimc.brick.creatures.common.domain.DCreature;
import com.guflimc.brick.creatures.common.domain.DCreatureTrait;
import com.guflimc.brick.orm.database.HibernateConfig;
import com.guflimc.brick.orm.database.HibernateDatabaseContext;

public class BrickCreaturesDatabaseContext extends HibernateDatabaseContext {

    public BrickCreaturesDatabaseContext(HibernateConfig config) {
        super(config);
    }

    public BrickCreaturesDatabaseContext(HibernateConfig config, int poolSize) {
        super(config, poolSize);
    }

    @Override
    protected Class<?>[] entityClasses() {
        return new Class[] {
                DCreature.class,
                DCreatureTrait.class
        };
    }

}

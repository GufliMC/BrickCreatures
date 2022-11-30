package com.guflimc.brick.creatures.common;

import com.guflimc.brick.creatures.common.domain.DCreature;
import com.guflimc.brick.orm.ebean.database.EbeanConfig;
import com.guflimc.brick.orm.ebean.database.EbeanDatabaseContext;
import com.guflimc.brick.orm.ebean.database.EbeanMigrations;
import io.ebean.annotation.Platform;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;

public class BrickCreaturesDatabaseContext extends EbeanDatabaseContext {

    public final static String DATASOURCE_NAME = "BrickCreatures";

    public BrickCreaturesDatabaseContext(EbeanConfig config) {
        super(config, DATASOURCE_NAME);
    }

    @Override
    protected Class<?>[] applicableClasses() {
        return APPLICABLE_CLASSES;
    }

    private final static Class<?>[] APPLICABLE_CLASSES = new Class[]{
            DCreature.class
    };

    public static void main(String[] args) throws IOException, SQLException {
        EbeanMigrations generator = new EbeanMigrations(
                DATASOURCE_NAME,
                Path.of("BrickCreatures/common/src/main/resources"),
                Platform.H2, Platform.MYSQL
        );
        Arrays.stream(APPLICABLE_CLASSES).forEach(generator::addClass);
        generator.generate();
    }

}

package com.loglrs.memberservice.config;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.spatial.dialect.mysql.MySQL8SpatialDialect;
import org.hibernate.type.DoubleType;
import org.hibernate.type.StringType;

public class MySQLDialectConfig extends MySQL8SpatialDialect {

    public MySQLDialectConfig() {
        super();

        this.registerFunction("ST_Distance_Sphere", new StandardSQLFunction("ST_Distance_Sphere", new DoubleType()));
        this.registerFunction("ST_GeomFromText", new StandardSQLFunction("ST_GeomFromText", new StringType()));
        this.registerFunction("POINT", new StandardSQLFunction("POINT", new StringType()));

    }
}

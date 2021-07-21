package org.tdf.sim.util;

import org.hibernate.dialect.MySQL57Dialect;

public class CustomMySQLDialect extends MySQL57Dialect {
    @Override
    public String getTableTypeString() {
        return "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci";
    }
}


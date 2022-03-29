package cz.xrosecky.terraingen.utils;

import cz.xrosecky.terraingen.data.DataStorage;

import java.sql.DriverManager;

public class StaticDB {
    private static java.sql.Connection conn = null;

    public static java.sql.Connection get() {
        if (conn == null) {

        }
        return conn;
    }
}

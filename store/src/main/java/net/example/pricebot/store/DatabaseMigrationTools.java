package net.example.pricebot.store;

import org.flywaydb.core.Flyway;

public class DatabaseMigrationTools {


    public static void updateDatabaseVersion(String dbUrl, String username, String password) {
        Flyway flyway = Flyway.configure().dataSource(dbUrl, username, password).load();
        flyway.migrate();

    }
}

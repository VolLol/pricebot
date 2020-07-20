package net.example.pricebot.store.entities;

import net.example.pricebot.store.exceptions.ProblemConnectionToDatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionFactory {
    private static final String DB_NAME = "pricebot";
    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/" + DB_NAME;
    private static final String USER = "postgres";
    private static final String PASS = "password";
    private static final Object sinc = new Object();


    public static Connection getConnection() throws ProblemConnectionToDatabaseException {
        synchronized (sinc) {
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                return connection;
            } catch (SQLException e) {
                throw new ProblemConnectionToDatabaseException("Can't connect to database", e);
            } catch (ClassNotFoundException e) {
                throw new ProblemConnectionToDatabaseException("Database driver not found", e);
            }
        }
    }

}

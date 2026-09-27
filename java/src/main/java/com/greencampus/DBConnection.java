package com.greencampus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/green_campus";

    private static final String USER =
            "root";

    private static final String PASSWORD =
            getPassword();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "MySQL JDBC Driver not found. "
                    + "Make sure mysql-connector-j is present in Maven dependencies."
            );
        }
    }

    private DBConnection() {
        // Prevent object creation
    }

    private static String getPassword() {

        String property =
                System.getProperty("green.campus.db.password");

        if (property != null && !property.isBlank()) {
            return property;
        }

        String environment =
                System.getenv("GREEN_CAMPUS_DB_PASSWORD");

        if (environment != null && !environment.isBlank()) {
            return environment;
        }

        throw new IllegalStateException(
                "MySQL password not configured. "
                + "Set GREEN_CAMPUS_DB_PASSWORD "
                + "or -Dgreen.campus.db.password."
        );
    }

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}
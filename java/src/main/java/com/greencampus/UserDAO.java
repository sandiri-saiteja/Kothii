package com.greencampus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public boolean userCodeExists(String userCode) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE user_code = ? LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userCode);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users "
                + "(full_name, user_code, email, department, campus, password_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getFullName());
            statement.setString(2, user.getUserCode());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getDepartment());
            statement.setString(5, user.getCampus());
            statement.setString(6, user.getPasswordHash());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                }
            }
        }

        return user;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, full_name, user_code, email, "
                + "department, campus, password_hash "
                + "FROM users WHERE email = ? LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return new User(
                        result.getInt("user_id"),
                        result.getString("full_name"),
                        result.getString("user_code"),
                        result.getString("email"),
                        result.getString("department"),
                        result.getString("campus"),
                        result.getString("password_hash")
                );
            }
        }
    }
}

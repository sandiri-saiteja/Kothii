package com.greencampus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class ReportDAO {

    public int createReport(String email, String metricName,
                            double metricValue, LocalDate reportDate,
                            String description) throws SQLException {

        String findUserSql = "SELECT user_id FROM users WHERE email = ? LIMIT 1";
        String insertSql = "INSERT INTO sustainability_reports "
                + "(user_id, metric_name, metric_value, report_date, description) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {
            int userId;

            try (PreparedStatement findUser = connection.prepareStatement(findUserSql)) {
                findUser.setString(1, email);

                try (ResultSet result = findUser.executeQuery()) {
                    if (!result.next()) {
                        throw new SQLException("Registered user was not found.");
                    }
                    userId = result.getInt("user_id");
                }
            }

            try (PreparedStatement insert = connection.prepareStatement(
                    insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insert.setInt(1, userId);
                insert.setString(2, metricName);
                insert.setDouble(3, metricValue);
                insert.setDate(4, Date.valueOf(reportDate));
                insert.setString(5, description);
                insert.executeUpdate();

                try (ResultSet keys = insert.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        }

        return -1;
    }
}

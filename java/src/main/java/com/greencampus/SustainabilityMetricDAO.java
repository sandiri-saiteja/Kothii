package com.greencampus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SustainabilityMetricDAO {

    // =====================================================
    // CREATE
    // =====================================================

    public void addMetric(
            SustainabilityMetric metric) {

        String sql =
                "INSERT INTO sustainability_metrics " +
                "(metric_name, metric_value, unit, " +
                "metric_date, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(
                    1,
                    metric.getMetricName()
            );

            statement.setDouble(
                    2,
                    metric.getMetricValue()
            );

            statement.setString(
                    3,
                    metric.getUnit()
            );

            statement.setDate(
                    4,
                    Date.valueOf(
                            metric.getMetricDate()
                    )
            );

            statement.setString(
                    5,
                    metric.getDescription()
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                try (ResultSet keys =
                             statement.getGeneratedKeys()) {

                    if (keys.next()) {

                        metric.setMetricId(
                                keys.getInt(1)
                        );
                    }
                }

                System.out.println(
                        "Sustainability metric added successfully."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error adding sustainability metric: "
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // READ
    // =====================================================

    public List<SustainabilityMetric> getAllMetrics() {

        List<SustainabilityMetric> metrics =
                new ArrayList<>();

        String sql =
                "SELECT metric_id, metric_name, " +
                "metric_value, unit, metric_date, " +
                "description " +
                "FROM sustainability_metrics " +
                "ORDER BY metric_id";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                SustainabilityMetric metric =
                        new SustainabilityMetric(

                                result.getInt(
                                        "metric_id"
                                ),

                                result.getString(
                                        "metric_name"
                                ),

                                result.getDouble(
                                        "metric_value"
                                ),

                                result.getString(
                                        "unit"
                                ),

                                result.getDate(
                                        "metric_date"
                                ).toLocalDate(),

                                result.getString(
                                        "description"
                                )
                        );

                metrics.add(metric);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error reading sustainability metrics: "
                            + e.getMessage()
            );
        }

        return metrics;
    }


    // =====================================================
    // UPDATE
    // =====================================================

    public void updateMetric(
            SustainabilityMetric metric) {

        String sql =
                "UPDATE sustainability_metrics SET " +
                "metric_name = ?, " +
                "metric_value = ?, " +
                "unit = ?, " +
                "metric_date = ?, " +
                "description = ? " +
                "WHERE metric_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    metric.getMetricName()
            );

            statement.setDouble(
                    2,
                    metric.getMetricValue()
            );

            statement.setString(
                    3,
                    metric.getUnit()
            );

            statement.setDate(
                    4,
                    Date.valueOf(
                            metric.getMetricDate()
                    )
            );

            statement.setString(
                    5,
                    metric.getDescription()
            );

            statement.setInt(
                    6,
                    metric.getMetricId()
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Sustainability metric updated successfully."
                );

            } else {

                System.out.println(
                        "Metric ID not found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error updating sustainability metric: "
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // DELETE
    // =====================================================

    public void deleteMetric(
            int metricId) {

        String sql =
                "DELETE FROM sustainability_metrics " +
                "WHERE metric_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    metricId
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Sustainability metric deleted successfully."
                );

            } else {

                System.out.println(
                        "Metric ID not found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error deleting sustainability metric: "
                            + e.getMessage()
            );
        }
    }
}
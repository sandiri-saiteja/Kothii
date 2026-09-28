package com.greencampus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Database service used by the servlet controller.
 *
 * The existing DAO classes remain unchanged for the standalone CRUD
 * experiment. This service is intentionally separate so the new Servlet
 * stage can be added without disturbing the earlier implementation.
 */
public class GreenCampusServletService {

    // =========================================================
    // RESOURCES
    // =========================================================

    public List<Resource> listResources() throws SQLException {
        String sql = "SELECT resource_id, resource_name, resource_type, unit, description "
                + "FROM resources ORDER BY resource_id";

        List<Resource> resources = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                resources.add(new Resource(
                        result.getInt("resource_id"),
                        result.getString("resource_name"),
                        result.getString("resource_type"),
                        result.getString("unit"),
                        result.getString("description")
                ));
            }
        }

        return resources;
    }

    public Resource getResource(int resourceId) throws SQLException {
        String sql = "SELECT resource_id, resource_name, resource_type, unit, description "
                + "FROM resources WHERE resource_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, resourceId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new Resource(
                            result.getInt("resource_id"),
                            result.getString("resource_name"),
                            result.getString("resource_type"),
                            result.getString("unit"),
                            result.getString("description")
                    );
                }
            }
        }

        return null;
    }

    public Resource createResource(Resource resource) throws SQLException {
        String sql = "INSERT INTO resources "
                + "(resource_name, resource_type, unit, description) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, resource.getResourceName());
            statement.setString(2, resource.getResourceType());
            statement.setString(3, resource.getUnit());
            statement.setString(4, resource.getDescription());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    resource.setResourceId(keys.getInt(1));
                }
            }
        }

        return resource;
    }

    public boolean updateResource(Resource resource) throws SQLException {
        String sql = "UPDATE resources SET resource_name = ?, resource_type = ?, "
                + "unit = ?, description = ? WHERE resource_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, resource.getResourceName());
            statement.setString(2, resource.getResourceType());
            statement.setString(3, resource.getUnit());
            statement.setString(4, resource.getDescription());
            statement.setInt(5, resource.getResourceId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteResource(int resourceId) throws SQLException {
        String sql = "DELETE FROM resources WHERE resource_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, resourceId);
            return statement.executeUpdate() > 0;
        }
    }

    // =========================================================
    // CONSUMPTION RECORDS
    // =========================================================

    public List<ConsumptionRecord> listConsumptionRecords() throws SQLException {
        String sql = "SELECT record_id, resource_id, consumption_value, "
                + "consumption_date, campus_location "
                + "FROM consumption_records ORDER BY record_id";

        List<ConsumptionRecord> records = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                records.add(new ConsumptionRecord(
                        result.getInt("record_id"),
                        result.getInt("resource_id"),
                        result.getDouble("consumption_value"),
                        result.getDate("consumption_date").toLocalDate(),
                        result.getString("campus_location")
                ));
            }
        }

        return records;
    }

    public ConsumptionRecord getConsumptionRecord(int recordId) throws SQLException {
        String sql = "SELECT record_id, resource_id, consumption_value, "
                + "consumption_date, campus_location "
                + "FROM consumption_records WHERE record_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, recordId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new ConsumptionRecord(
                            result.getInt("record_id"),
                            result.getInt("resource_id"),
                            result.getDouble("consumption_value"),
                            result.getDate("consumption_date").toLocalDate(),
                            result.getString("campus_location")
                    );
                }
            }
        }

        return null;
    }

    public ConsumptionRecord createConsumptionRecord(ConsumptionRecord record)
            throws SQLException {

        String sql = "INSERT INTO consumption_records "
                + "(resource_id, consumption_value, consumption_date, campus_location) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, record.getResourceId());
            statement.setBigDecimal(2, BigDecimal.valueOf(record.getConsumptionValue()));
            statement.setDate(3, Date.valueOf(record.getConsumptionDate()));
            statement.setString(4, record.getCampusLocation());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    record.setRecordId(keys.getInt(1));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException(
                    "Resource ID " + record.getResourceId() + " does not exist."
            );
        }

        return record;
    }

    public boolean updateConsumptionRecord(ConsumptionRecord record)
            throws SQLException {

        String sql = "UPDATE consumption_records SET resource_id = ?, "
                + "consumption_value = ?, consumption_date = ?, campus_location = ? "
                + "WHERE record_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, record.getResourceId());
            statement.setBigDecimal(2, BigDecimal.valueOf(record.getConsumptionValue()));
            statement.setDate(3, Date.valueOf(record.getConsumptionDate()));
            statement.setString(4, record.getCampusLocation());
            statement.setInt(5, record.getRecordId());

            try {
                return statement.executeUpdate() > 0;
            } catch (SQLIntegrityConstraintViolationException e) {
                throw new IllegalArgumentException(
                        "Resource ID " + record.getResourceId() + " does not exist."
                );
            }
        }
    }

    public boolean deleteConsumptionRecord(int recordId) throws SQLException {
        String sql = "DELETE FROM consumption_records WHERE record_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, recordId);
            return statement.executeUpdate() > 0;
        }
    }

    // =========================================================
    // SUSTAINABILITY METRICS
    // =========================================================

    public List<SustainabilityMetric> listMetrics() throws SQLException {
        String sql = "SELECT metric_id, metric_name, metric_value, unit, "
                + "metric_date, description FROM sustainability_metrics ORDER BY metric_id";

        List<SustainabilityMetric> metrics = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                metrics.add(new SustainabilityMetric(
                        result.getInt("metric_id"),
                        result.getString("metric_name"),
                        result.getDouble("metric_value"),
                        result.getString("unit"),
                        result.getDate("metric_date").toLocalDate(),
                        result.getString("description")
                ));
            }
        }

        return metrics;
    }

    public SustainabilityMetric getMetric(int metricId) throws SQLException {
        String sql = "SELECT metric_id, metric_name, metric_value, unit, "
                + "metric_date, description FROM sustainability_metrics "
                + "WHERE metric_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, metricId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new SustainabilityMetric(
                            result.getInt("metric_id"),
                            result.getString("metric_name"),
                            result.getDouble("metric_value"),
                            result.getString("unit"),
                            result.getDate("metric_date").toLocalDate(),
                            result.getString("description")
                    );
                }
            }
        }

        return null;
    }

    public SustainabilityMetric createMetric(SustainabilityMetric metric)
            throws SQLException {

        String sql = "INSERT INTO sustainability_metrics "
                + "(metric_name, metric_value, unit, metric_date, description) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, metric.getMetricName());
            statement.setBigDecimal(2, BigDecimal.valueOf(metric.getMetricValue()));
            statement.setString(3, metric.getUnit());
            statement.setDate(4, Date.valueOf(metric.getMetricDate()));
            statement.setString(5, metric.getDescription());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    metric.setMetricId(keys.getInt(1));
                }
            }
        }

        return metric;
    }

    public boolean updateMetric(SustainabilityMetric metric) throws SQLException {
        String sql = "UPDATE sustainability_metrics SET metric_name = ?, "
                + "metric_value = ?, unit = ?, metric_date = ?, description = ? "
                + "WHERE metric_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, metric.getMetricName());
            statement.setBigDecimal(2, BigDecimal.valueOf(metric.getMetricValue()));
            statement.setString(3, metric.getUnit());
            statement.setDate(4, Date.valueOf(metric.getMetricDate()));
            statement.setString(5, metric.getDescription());
            statement.setInt(6, metric.getMetricId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteMetric(int metricId) throws SQLException {
        String sql = "DELETE FROM sustainability_metrics WHERE metric_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, metricId);
            return statement.executeUpdate() > 0;
        }
    }

    // =========================================================
    // VALIDATION HELPERS
    // =========================================================

    public static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
    }

    public static void requirePositive(double value, String field) {
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException(field + " must be greater than zero.");
        }
    }

    public static int positiveId(int value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be a positive integer.");
        }
        return value;
    }

    public static LocalDate parseDate(String value, String field) {
        requireText(value, field);
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    field + " must use YYYY-MM-DD format."
            );
        }
    }
}

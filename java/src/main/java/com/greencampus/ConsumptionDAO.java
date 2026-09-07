package com.greencampus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConsumptionDAO {

    // =====================================================
    // CREATE
    // =====================================================

    public void addRecord(ConsumptionRecord record) {

        String sql =
                "INSERT INTO consumption_records " +
                "(resource_id, consumption_value, " +
                "consumption_date, campus_location) " +
                "VALUES (?, ?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(
                    1,
                    record.getResourceId()
            );

            statement.setDouble(
                    2,
                    record.getConsumptionValue()
            );

            statement.setDate(
                    3,
                    Date.valueOf(
                            record.getConsumptionDate()
                    )
            );

            statement.setString(
                    4,
                    record.getCampusLocation()
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                try (ResultSet keys =
                             statement.getGeneratedKeys()) {

                    if (keys.next()) {

                        record.setRecordId(
                                keys.getInt(1)
                        );
                    }
                }

                System.out.println(
                        "Consumption record added successfully."
                );

            }

        } catch (SQLException e) {

            System.out.println(
                    "Error adding consumption record: "
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // READ
    // =====================================================

    public List<ConsumptionRecord> getAllRecords() {

        List<ConsumptionRecord> records =
                new ArrayList<>();

        String sql =
                "SELECT record_id, resource_id, " +
                "consumption_value, consumption_date, " +
                "campus_location " +
                "FROM consumption_records " +
                "ORDER BY record_id";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                ConsumptionRecord record =
                        new ConsumptionRecord(

                                result.getInt(
                                        "record_id"
                                ),

                                result.getInt(
                                        "resource_id"
                                ),

                                result.getDouble(
                                        "consumption_value"
                                ),

                                result.getDate(
                                        "consumption_date"
                                ).toLocalDate(),

                                result.getString(
                                        "campus_location"
                                )
                        );

                records.add(record);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error reading consumption records: "
                            + e.getMessage()
            );
        }

        return records;
    }


    // =====================================================
    // UPDATE
    // =====================================================

    public void updateRecord(
            ConsumptionRecord record) {

        String sql =
                "UPDATE consumption_records SET " +
                "resource_id = ?, " +
                "consumption_value = ?, " +
                "consumption_date = ?, " +
                "campus_location = ? " +
                "WHERE record_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    record.getResourceId()
            );

            statement.setDouble(
                    2,
                    record.getConsumptionValue()
            );

            statement.setDate(
                    3,
                    Date.valueOf(
                            record.getConsumptionDate()
                    )
            );

            statement.setString(
                    4,
                    record.getCampusLocation()
            );

            statement.setInt(
                    5,
                    record.getRecordId()
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Consumption record updated successfully."
                );

            } else {

                System.out.println(
                        "Consumption record ID not found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error updating consumption record: "
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // DELETE
    // =====================================================

    public void deleteRecord(int recordId) {

        String sql =
                "DELETE FROM consumption_records " +
                "WHERE record_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    recordId
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Consumption record deleted successfully."
                );

            } else {

                System.out.println(
                        "Consumption record ID not found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error deleting consumption record: "
                            + e.getMessage()
            );
        }
    }
}
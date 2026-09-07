package com.greencampus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    // =====================================================
    // CREATE
    // =====================================================

    public void addResource(Resource resource) {

        String sql =
                "INSERT INTO resources " +
                "(resource_name, resource_type, unit, description) " +
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

            statement.setString(
                    1,
                    resource.getResourceName()
            );

            statement.setString(
                    2,
                    resource.getResourceType()
            );

            statement.setString(
                    3,
                    resource.getUnit()
            );

            statement.setString(
                    4,
                    resource.getDescription()
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                try (ResultSet keys =
                             statement.getGeneratedKeys()) {

                    if (keys.next()) {

                        resource.setResourceId(
                                keys.getInt(1)
                        );
                    }
                }

                System.out.println(
                        "Resource added successfully."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error adding resource: "
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // READ
    // =====================================================

    public List<Resource> getAllResources() {

        List<Resource> resources =
                new ArrayList<>();

        String sql =
                "SELECT resource_id, resource_name, " +
                "resource_type, unit, description " +
                "FROM resources " +
                "ORDER BY resource_id";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                Resource resource =
                        new Resource(
                                result.getInt(
                                        "resource_id"
                                ),

                                result.getString(
                                        "resource_name"
                                ),

                                result.getString(
                                        "resource_type"
                                ),

                                result.getString(
                                        "unit"
                                ),

                                result.getString(
                                        "description"
                                )
                        );

                resources.add(resource);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error reading resources: "
                            + e.getMessage()
            );
        }

        return resources;
    }


    // =====================================================
    // UPDATE
    // =====================================================

    public void updateResource(Resource resource) {

        String sql =
                "UPDATE resources SET " +
                "resource_name = ?, " +
                "resource_type = ?, " +
                "unit = ?, " +
                "description = ? " +
                "WHERE resource_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    resource.getResourceName()
            );

            statement.setString(
                    2,
                    resource.getResourceType()
            );

            statement.setString(
                    3,
                    resource.getUnit()
            );

            statement.setString(
                    4,
                    resource.getDescription()
            );

            statement.setInt(
                    5,
                    resource.getResourceId()
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Resource updated successfully."
                );

            } else {

                System.out.println(
                        "Resource ID not found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error updating resource: "
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // DELETE
    // =====================================================

    public void deleteResource(int resourceId) {

        String sql =
                "DELETE FROM resources " +
                "WHERE resource_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    resourceId
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Resource deleted successfully."
                );

            } else {

                System.out.println(
                        "Resource ID not found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error deleting resource: "
                            + e.getMessage()
            );
        }
    }
}
package com.greencampus;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class GreenCampusApp {

    // =====================================================
    // SCANNER
    // =====================================================

    private final Scanner scanner = new Scanner(System.in);


    // =====================================================
    // DAO OBJECTS
    // =====================================================

    private final ResourceDAO resourceDAO =
            new ResourceDAO();

    private final ConsumptionDAO consumptionDAO =
            new ConsumptionDAO();

    private final SustainabilityMetricDAO metricDAO =
            new SustainabilityMetricDAO();


    // =====================================================
    // START APPLICATION
    // =====================================================

    public void start() {

        boolean running = true;

        while (running) {

            printMainMenu();

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1 -> resourceMenu();

                case 2 -> consumptionMenu();

                case 3 -> metricMenu();

                case 0 -> {

                    running = false;

                    System.out.println();
                    System.out.println(
                            "=============================================="
                    );
                    System.out.println(
                            " Green Campus application closed."
                    );
                    System.out.println(
                            "=============================================="
                    );
                }

                default -> {

                    System.out.println();
                    System.out.println(
                            "Invalid choice. Please try again."
                    );
                }
            }
        }

        scanner.close();
    }


    // =====================================================
    // MAIN MENU
    // =====================================================

    private void printMainMenu() {

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "    GREEN CAMPUS SUSTAINABILITY PORTAL"
        );
        System.out.println(
                "=============================================="
        );

        System.out.println(
                "1. Resource Management"
        );

        System.out.println(
                "2. Consumption Records"
        );

        System.out.println(
                "3. Sustainability Metrics"
        );

        System.out.println(
                "0. Exit"
        );

        System.out.println(
                "=============================================="
        );
    }


    // =====================================================
    // RESOURCE MANAGEMENT MENU
    // =====================================================

    private void resourceMenu() {

        boolean back = false;

        while (!back) {

            System.out.println();
            System.out.println(
                    "------------- RESOURCE MANAGEMENT -------------"
            );

            System.out.println(
                    "1. Add Resource"
            );

            System.out.println(
                    "2. View Resources"
            );

            System.out.println(
                    "3. Update Resource"
            );

            System.out.println(
                    "4. Delete Resource"
            );

            System.out.println(
                    "0. Back"
            );

            System.out.println(
                    "-----------------------------------------------"
            );

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1 -> addResource();

                case 2 -> viewResources();

                case 3 -> updateResource();

                case 4 -> deleteResource();

                case 0 -> back = true;

                default ->
                        System.out.println(
                                "Invalid choice."
                        );
            }
        }
    }


    // =====================================================
    // CREATE RESOURCE
    // =====================================================

    private void addResource() {

        System.out.println();
        System.out.println(
                "------------- ADD RESOURCE -------------"
        );

        String name =
                readText(
                        "Resource name: "
                );

        String type =
                readText(
                        "Resource type: "
                );

        String unit =
                readText(
                        "Unit: "
                );

        String description =
                readText(
                        "Description: "
                );


        Resource resource =
                new Resource(
                        name,
                        type,
                        unit,
                        description
                );


        resourceDAO.addResource(resource);


        if (resource.getResourceId() > 0) {

            System.out.println(
                    "Generated Resource ID: "
                            + resource.getResourceId()
            );
        }
    }


    // =====================================================
    // READ RESOURCES
    // =====================================================

    private void viewResources() {

        System.out.println();
        System.out.println(
                "------------- ALL RESOURCES -------------"
        );


        List<Resource> resources =
                resourceDAO.getAllResources();


        if (resources.isEmpty()) {

            System.out.println(
                    "No resources found."
            );

            return;
        }


        System.out.println();

        for (Resource resource : resources) {

            System.out.println(resource);

        }
    }


    // =====================================================
    // UPDATE RESOURCE
    // =====================================================

    private void updateResource() {

        System.out.println();
        System.out.println(
                "------------- UPDATE RESOURCE -------------"
        );


        int id =
                readPositiveInt(
                        "Resource ID: "
                );


        String name =
                readText(
                        "New resource name: "
                );


        String type =
                readText(
                        "New resource type: "
                );


        String unit =
                readText(
                        "New unit: "
                );


        String description =
                readText(
                        "New description: "
                );


        Resource resource =
                new Resource(
                        id,
                        name,
                        type,
                        unit,
                        description
                );


        resourceDAO.updateResource(resource);
    }


    // =====================================================
    // DELETE RESOURCE
    // =====================================================

    private void deleteResource() {

        System.out.println();
        System.out.println(
                "------------- DELETE RESOURCE -------------"
        );


        int id =
                readPositiveInt(
                        "Enter Resource ID to delete: "
                );


        resourceDAO.deleteResource(id);
    }


    // =====================================================
    // CONSUMPTION RECORD MENU
    // =====================================================

    private void consumptionMenu() {

        boolean back = false;

        while (!back) {

            System.out.println();
            System.out.println(
                    "------------- CONSUMPTION RECORDS -------------"
            );

            System.out.println(
                    "1. Add Consumption Record"
            );

            System.out.println(
                    "2. View Consumption Records"
            );

            System.out.println(
                    "3. Update Consumption Record"
            );

            System.out.println(
                    "4. Delete Consumption Record"
            );

            System.out.println(
                    "0. Back"
            );

            System.out.println(
                    "------------------------------------------------"
            );


            int choice =
                    readInt(
                            "Enter your choice: "
                    );


            switch (choice) {

                case 1 ->
                        addConsumption();

                case 2 ->
                        viewConsumption();

                case 3 ->
                        updateConsumption();

                case 4 ->
                        deleteConsumption();

                case 0 ->
                        back = true;

                default ->
                        System.out.println(
                                "Invalid choice."
                        );
            }
        }
    }


    // =====================================================
    // SHOW AVAILABLE RESOURCES
    // =====================================================

    private void showAvailableResources() {

        System.out.println();
        System.out.println(
                "------------- AVAILABLE RESOURCES -------------"
        );


        List<Resource> resources =
                resourceDAO.getAllResources();


        if (resources.isEmpty()) {

            System.out.println(
                    "No resources are available."
            );

            System.out.println(
                    "Please add a resource first."
            );

            return;
        }


        System.out.printf(
                "%-10s %-25s %-20s%n",
                "ID",
                "Resource Name",
                "Resource Type"
        );


        System.out.println(
                "-------------------------------------------------------"
        );


        for (Resource resource : resources) {

            System.out.printf(
                    "%-10d %-25s %-20s%n",
                    resource.getResourceId(),
                    resource.getResourceName(),
                    resource.getResourceType()
            );
        }


        System.out.println(
                "-------------------------------------------------------"
        );
    }


    // =====================================================
    // CHECK WHETHER RESOURCE EXISTS
    // =====================================================

    private boolean resourceExists(int resourceId) {

        List<Resource> resources =
                resourceDAO.getAllResources();


        for (Resource resource : resources) {

            if (resource.getResourceId() == resourceId) {

                return true;
            }
        }


        return false;
    }


    // =====================================================
    // CREATE CONSUMPTION RECORD
    // =====================================================

    private void addConsumption() {

        System.out.println();
        System.out.println(
                "------------- ADD CONSUMPTION RECORD -------------"
        );


        // Show valid Resource IDs before asking for input
        showAvailableResources();


        List<Resource> resources =
                resourceDAO.getAllResources();


        if (resources.isEmpty()) {

            return;
        }


        int resourceId;


        while (true) {

            resourceId =
                    readPositiveInt(
                            "Resource ID: "
                    );


            if (resourceExists(resourceId)) {

                break;
            }


            System.out.println();
            System.out.println(
                    "Invalid Resource ID."
            );

            System.out.println(
                    "Please select an ID from the list above."
            );
        }


        double value =
                readPositiveDouble(
                        "Consumption value: "
                );


        LocalDate date =
                readDate(
                        "Date (YYYY-MM-DD): "
                );


        String campus =
                readText(
                        "Campus location: "
                );


        ConsumptionRecord record =
                new ConsumptionRecord(
                        resourceId,
                        value,
                        date,
                        campus
                );


        consumptionDAO.addRecord(record);


        if (record.getRecordId() > 0) {

            System.out.println();
            System.out.println(
                    "Consumption record added successfully."
            );

            System.out.println(
                    "Generated Record ID: "
                            + record.getRecordId()
            );
        }
    }


    // =====================================================
    // READ CONSUMPTION RECORDS
    // =====================================================

    private void viewConsumption() {

        System.out.println();
        System.out.println(
                "------------- ALL CONSUMPTION RECORDS -------------"
        );


        List<ConsumptionRecord> records =
                consumptionDAO.getAllRecords();


        if (records.isEmpty()) {

            System.out.println(
                    "No consumption records found."
            );

            return;
        }


        System.out.println();

        for (ConsumptionRecord record : records) {

            System.out.println(record);
        }
    }


    // =====================================================
    // UPDATE CONSUMPTION RECORD
    // =====================================================

    private void updateConsumption() {

        System.out.println();
        System.out.println(
                "------------- UPDATE CONSUMPTION RECORD -------------"
        );


        int recordId =
                readPositiveInt(
                        "Record ID: "
                );


        // Show valid resources before changing Resource ID
        showAvailableResources();


        List<Resource> resources =
                resourceDAO.getAllResources();


        if (resources.isEmpty()) {

            System.out.println(
                    "No resources are available."
            );

            return;
        }


        int resourceId;


        while (true) {

            resourceId =
                    readPositiveInt(
                            "New Resource ID: "
                    );


            if (resourceExists(resourceId)) {

                break;
            }


            System.out.println();
            System.out.println(
                    "Invalid Resource ID."
            );

            System.out.println(
                    "Please select an ID from the list above."
            );
        }


        double value =
                readPositiveDouble(
                        "New consumption value: "
                );


        LocalDate date =
                readDate(
                        "New date (YYYY-MM-DD): "
                );


        String campus =
                readText(
                        "New campus location: "
                );


        ConsumptionRecord record =
                new ConsumptionRecord(
                        recordId,
                        resourceId,
                        value,
                        date,
                        campus
                );


        consumptionDAO.updateRecord(record);
    }


    // =====================================================
    // DELETE CONSUMPTION RECORD
    // =====================================================

    private void deleteConsumption() {

        System.out.println();
        System.out.println(
                "------------- DELETE CONSUMPTION RECORD -------------"
        );


        int recordId =
                readPositiveInt(
                        "Enter Record ID to delete: "
                );


        consumptionDAO.deleteRecord(
                recordId
        );
    }


    // =====================================================
    // SUSTAINABILITY METRICS MENU
    // =====================================================

    private void metricMenu() {

        boolean back = false;

        while (!back) {

            System.out.println();
            System.out.println(
                    "------------- SUSTAINABILITY METRICS -------------"
            );

            System.out.println(
                    "1. Add Sustainability Metric"
            );

            System.out.println(
                    "2. View Sustainability Metrics"
            );

            System.out.println(
                    "3. Update Sustainability Metric"
            );

            System.out.println(
                    "4. Delete Sustainability Metric"
            );

            System.out.println(
                    "0. Back"
            );

            System.out.println(
                    "---------------------------------------------------"
            );


            int choice =
                    readInt(
                            "Enter your choice: "
                    );


            switch (choice) {

                case 1 ->
                        addMetric();

                case 2 ->
                        viewMetrics();

                case 3 ->
                        updateMetric();

                case 4 ->
                        deleteMetric();

                case 0 ->
                        back = true;

                default ->
                        System.out.println(
                                "Invalid choice."
                        );
            }
        }
    }


    // =====================================================
    // CREATE METRIC
    // =====================================================

    private void addMetric() {

        System.out.println();
        System.out.println(
                "------------- ADD SUSTAINABILITY METRIC -------------"
        );


        String name =
                readText(
                        "Metric name: "
                );


        double value =
                readPositiveDouble(
                        "Metric value: "
                );


        String unit =
                readText(
                        "Unit: "
                );


        LocalDate date =
                readDate(
                        "Date (YYYY-MM-DD): "
                );


        String description =
                readText(
                        "Description: "
                );


        SustainabilityMetric metric =
                new SustainabilityMetric(
                        name,
                        value,
                        unit,
                        date,
                        description
                );


        metricDAO.addMetric(metric);


        if (metric.getMetricId() > 0) {

            System.out.println(
                    "Generated Metric ID: "
                            + metric.getMetricId()
            );
        }
    }


    // =====================================================
    // READ METRICS
    // =====================================================

    private void viewMetrics() {

        System.out.println();
        System.out.println(
                "------------- ALL SUSTAINABILITY METRICS -------------"
        );


        List<SustainabilityMetric> metrics =
                metricDAO.getAllMetrics();


        if (metrics.isEmpty()) {

            System.out.println(
                    "No sustainability metrics found."
            );

            return;
        }


        System.out.println();

        for (SustainabilityMetric metric :
                metrics) {

            System.out.println(metric);
        }
    }


    // =====================================================
    // UPDATE METRIC
    // =====================================================

    private void updateMetric() {

        System.out.println();
        System.out.println(
                "------------- UPDATE SUSTAINABILITY METRIC -------------"
        );


        int id =
                readPositiveInt(
                        "Metric ID: "
                );


        String name =
                readText(
                        "New metric name: "
                );


        double value =
                readPositiveDouble(
                        "New metric value: "
                );


        String unit =
                readText(
                        "New unit: "
                );


        LocalDate date =
                readDate(
                        "New date (YYYY-MM-DD): "
                );


        String description =
                readText(
                        "New description: "
                );


        SustainabilityMetric metric =
                new SustainabilityMetric(
                        id,
                        name,
                        value,
                        unit,
                        date,
                        description
                );


        metricDAO.updateMetric(metric);
    }


    // =====================================================
    // DELETE METRIC
    // =====================================================

    private void deleteMetric() {

        System.out.println();
        System.out.println(
                "------------- DELETE SUSTAINABILITY METRIC -------------"
        );


        int id =
                readPositiveInt(
                        "Enter Metric ID to delete: "
                );


        metricDAO.deleteMetric(id);
    }


    // =====================================================
    // READ INTEGER
    // =====================================================

    private int readInt(String message) {

        while (true) {

            try {

                System.out.print(message);

                String input =
                        scanner.nextLine().trim();

                return Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid integer."
                );
            }
        }
    }


    // =====================================================
    // READ POSITIVE INTEGER
    // =====================================================

    private int readPositiveInt(String message) {

        while (true) {

            int value =
                    readInt(message);

            if (value > 0) {

                return value;
            }

            System.out.println(
                    "ID must be greater than zero."
            );
        }
    }


    // =====================================================
    // READ POSITIVE DOUBLE
    // =====================================================

    private double readPositiveDouble(
            String message) {

        while (true) {

            try {

                System.out.print(message);

                String input =
                        scanner.nextLine().trim();

                double value =
                        Double.parseDouble(input);

                if (value > 0) {

                    return value;
                }

                System.out.println(
                        "Value must be greater than zero."
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid positive number."
                );
            }
        }
    }


    // =====================================================
    // READ TEXT
    // =====================================================

    private String readText(String message) {

        while (true) {

            System.out.print(message);

            String value =
                    scanner.nextLine().trim();


            if (!value.isEmpty()) {

                return value;
            }


            System.out.println(
                    "This field cannot be empty."
            );
        }
    }


    // =====================================================
    // READ DATE
    // =====================================================

    private LocalDate readDate(
            String message) {

        while (true) {

            try {

                System.out.print(message);

                String input =
                        scanner.nextLine().trim();

                return LocalDate.parse(input);

            } catch (Exception e) {

                System.out.println(
                        "Invalid date."
                );

                System.out.println(
                        "Please use YYYY-MM-DD format."
                );
            }
        }
    }
}
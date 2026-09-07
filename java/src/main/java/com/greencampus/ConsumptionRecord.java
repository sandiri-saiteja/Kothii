package com.greencampus;

import java.time.LocalDate;

public class ConsumptionRecord {

    private int recordId;
    private int resourceId;
    private double consumptionValue;
    private LocalDate consumptionDate;
    private String campusLocation;

    // Empty constructor
    public ConsumptionRecord() {
    }

    // Constructor for CREATE
    public ConsumptionRecord(
            int resourceId,
            double consumptionValue,
            LocalDate consumptionDate,
            String campusLocation) {

        this.resourceId = resourceId;
        this.consumptionValue = consumptionValue;
        this.consumptionDate = consumptionDate;
        this.campusLocation = campusLocation;
    }

    // Constructor for UPDATE / READ
    public ConsumptionRecord(
            int recordId,
            int resourceId,
            double consumptionValue,
            LocalDate consumptionDate,
            String campusLocation) {

        this.recordId = recordId;
        this.resourceId = resourceId;
        this.consumptionValue = consumptionValue;
        this.consumptionDate = consumptionDate;
        this.campusLocation = campusLocation;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public double getConsumptionValue() {
        return consumptionValue;
    }

    public void setConsumptionValue(double consumptionValue) {
        this.consumptionValue = consumptionValue;
    }

    public LocalDate getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(LocalDate consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public String getCampusLocation() {
        return campusLocation;
    }

    public void setCampusLocation(String campusLocation) {
        this.campusLocation = campusLocation;
    }

    @Override
    public String toString() {

        return "Record ID: " + recordId
                + " | Resource ID: " + resourceId
                + " | Consumption: " + consumptionValue
                + " | Date: " + consumptionDate
                + " | Campus: " + campusLocation;
    }
}
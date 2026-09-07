package com.greencampus;

import java.time.LocalDate;

public class SustainabilityMetric {

    private int metricId;
    private String metricName;
    private double metricValue;
    private String unit;
    private LocalDate metricDate;
    private String description;

    // Empty constructor
    public SustainabilityMetric() {
    }

    // Constructor for CREATE
    public SustainabilityMetric(
            String metricName,
            double metricValue,
            String unit,
            LocalDate metricDate,
            String description) {

        this.metricName = metricName;
        this.metricValue = metricValue;
        this.unit = unit;
        this.metricDate = metricDate;
        this.description = description;
    }

    // Constructor for READ / UPDATE
    public SustainabilityMetric(
            int metricId,
            String metricName,
            double metricValue,
            String unit,
            LocalDate metricDate,
            String description) {

        this.metricId = metricId;
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.unit = unit;
        this.metricDate = metricDate;
        this.description = description;
    }

    public int getMetricId() {
        return metricId;
    }

    public void setMetricId(int metricId) {
        this.metricId = metricId;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getMetricDate() {
        return metricDate;
    }

    public void setMetricDate(LocalDate metricDate) {
        this.metricDate = metricDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {

        return "Metric ID: " + metricId
                + " | Name: " + metricName
                + " | Value: " + metricValue
                + " | Unit: " + unit
                + " | Date: " + metricDate
                + " | Description: " + description;
    }
}
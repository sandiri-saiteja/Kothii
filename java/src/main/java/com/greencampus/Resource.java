package com.greencampus;

public class Resource {

    private int resourceId;
    private String resourceName;
    private String resourceType;
    private String unit;
    private String description;

    public Resource() {
    }

    // Constructor for creating a new resource
    public Resource(
            String resourceName,
            String resourceType,
            String unit,
            String description) {

        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.unit = unit;
        this.description = description;
    }

    // Constructor for updating an existing resource
    public Resource(
            int resourceId,
            String resourceName,
            String resourceType,
            String unit,
            String description) {

        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.unit = unit;
        this.description = description;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {

        return "Resource ID: " + resourceId
                + " | Name: " + resourceName
                + " | Type: " + resourceType
                + " | Unit: " + unit
                + " | Description: " + description;
    }
}
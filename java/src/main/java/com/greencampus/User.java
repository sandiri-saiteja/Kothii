package com.greencampus;

public class User {

    private int userId;
    private String fullName;
    private String userCode;
    private String email;
    private String department;
    private String campus;
    private String passwordHash;

    public User(String fullName, String userCode, String email,
                String department, String campus, String passwordHash) {
        this(0, fullName, userCode, email, department, campus, passwordHash);
    }

    public User(int userId, String fullName, String userCode, String email,
                String department, String campus, String passwordHash) {
        this.userId = userId;
        this.fullName = fullName;
        this.userCode = userCode;
        this.email = email;
        this.department = department;
        this.campus = campus;
        this.passwordHash = passwordHash;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}

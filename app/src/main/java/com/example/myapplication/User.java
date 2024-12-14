package com.example.myapplication;

public class User {
    private String userId;
    private String cin;
    private String name;
    private String contact;
    private String role;

    public User() {}

    public User(String userId, String cin, String name, String contact, String role) {
        this.userId = userId;
        this.cin = cin;
        this.name = name;
        this.contact = contact;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

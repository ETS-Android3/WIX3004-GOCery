package com.example.gocery.manageprofiles.model;

public class Household {
    private String address;
    private String userID;

    public Household() {
    }

    public Household(String address, String userID) {
        this.address = address;
        this.userID = userID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

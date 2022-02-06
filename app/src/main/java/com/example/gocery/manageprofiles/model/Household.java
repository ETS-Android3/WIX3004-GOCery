package com.example.gocery.manageprofiles.model;

import com.google.firebase.database.Exclude;

public class Household {
    @Exclude
    private String key;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

package com.example.gocery.manageprofiles.model;

public class Household {
    private String address;

    public Household() {
    }

    public Household(String addres) {
        this.address = addres;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

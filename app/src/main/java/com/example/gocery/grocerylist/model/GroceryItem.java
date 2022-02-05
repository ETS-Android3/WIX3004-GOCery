package com.example.gocery.grocerylist.model;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

public class GroceryItem {

    @Exclude
    private String key;

    private String name;
    private int quantity;
    private String description;
    private String image;
    private String locationName;
    private int locationID;
    private String createdBy;
    private Boolean status;

    public GroceryItem() {
    }

    public GroceryItem(String name,
                       int quantity,
                       Boolean status,
                       @Nullable String description,
                       @Nullable String image,
                       @Nullable String locationName,
                       @Nullable int locationID) {
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.image = image;
        this.locationName = locationName;
        this.locationID = locationID;
        this.status = status;
    }

    public GroceryItem(String name,
                       int quantity,
                       Boolean status,
                       @Nullable String description,
                       @Nullable String image) {
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.image = image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocationLat() {
        return locationName;
    }

    public void setLocationLat(String locationLat) {
        this.locationName = locationLat;
    }

    public int getLocationLong() {
        return locationID;
    }

    public void setLocationLong(int locationLong) {
        this.locationID = locationLong;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "GroceryItem{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", locationLat='" + locationName + '\'' +
                ", locationLong='" + locationID + '\'' +
                ", status=" + status +
                '}';
    }
}

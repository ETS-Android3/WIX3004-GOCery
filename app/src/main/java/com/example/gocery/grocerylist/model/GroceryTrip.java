package com.example.gocery.grocerylist.model;

import com.google.firebase.database.Exclude;

import java.util.List;

public class GroceryTrip {

    @Exclude
    private String key;

    private String name;
    private String completedBy;

    private String datetime;
    private List<GroceryItem> groceryItems;

    public GroceryTrip() {
    }

    public GroceryTrip(String name, String completedBy, String datetime, List<GroceryItem> groceryItems) {
        this.name = name;
        this.completedBy = completedBy;
        this.datetime = datetime;
        this.groceryItems = groceryItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public List<GroceryItem> getGroceryItems() {
        return groceryItems;
    }

    public void setGroceryItems(List<GroceryItem> groceryItems) {
        this.groceryItems = groceryItems;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "GroceryTrip{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", completedBy='" + completedBy + '\'' +
                ", datetime=" + datetime +
                ", groceryItems=" + groceryItems +
                '}';
    }
}

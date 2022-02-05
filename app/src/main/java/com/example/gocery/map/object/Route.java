package com.example.gocery.map.object;

import java.util.ArrayList;
import java.util.HashMap;

public class Route {
    String distance;
    String time;
    ArrayList<HashMap<String, String>> coordinates;
    String destination;

    public Route() {
        distance = "";
        time = "";
        coordinates = new ArrayList<>();
        destination = "";
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<HashMap<String, String>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<HashMap<String, String>> coordinates) {
        this.coordinates = coordinates;
    }
}

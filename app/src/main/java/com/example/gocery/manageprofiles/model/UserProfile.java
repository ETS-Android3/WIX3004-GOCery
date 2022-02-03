package com.example.gocery.manageprofiles.model;

import com.google.firebase.database.Exclude;

public class UserProfile {

    @Exclude
    private String key;

    private String username;
    private int imgId;
    private String password;
    boolean isRepresentative;
    boolean isAdmin;

    public UserProfile(){

    }

    public UserProfile(String username, int imgId, String password, boolean isRepresentative, boolean isAdmin) {
        this.username = username;
        this.imgId = imgId;
        this.password = password;
        this.isRepresentative = isRepresentative;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRepresentative() {
        return isRepresentative;
    }

    public void setRepresentative(boolean representative) {
        isRepresentative = representative;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

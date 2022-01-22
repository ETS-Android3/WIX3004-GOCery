package com.example.gocery.manageprofiles.model;

public class ProfileData {

    private String username;
    private int imgId;

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public ProfileData(String username, int imgId){
        this.username = username;
        this.imgId = imgId;
    }
}

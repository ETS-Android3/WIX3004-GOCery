package com.example.gocery.authentication;

// this class is for singleton pattern
// to save the current user profile data
public class CurrentProfile {

    private static CurrentProfile uniqueInstance;
    private String profileName = null;

    public CurrentProfile() {
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public static CurrentProfile getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new CurrentProfile();
        }

        return uniqueInstance;
    }
}

package com.example.gocery.manageprofiles.dao;

import com.example.gocery.manageprofiles.model.UserProfile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DAOProfile {

    private DatabaseReference databaseReference;
    private String user;

    public DAOProfile(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(UserProfile.class.getSimpleName());
    }

    public Task<Void>add(UserProfile userProfile, String userID){
//        return databaseReference.push().setValue(userProfile);
        user = "user_"+userID;
        return databaseReference.child(user).push().setValue(userProfile);
    }

    public Query get(String userID){
        user = "user_"+userID;
        return databaseReference.child(user);
    }

}

package com.example.gocery.manageprofiles.dao;

import com.example.gocery.manageprofiles.model.ProfileData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOProfile {

    private DatabaseReference databaseReference;

    public DAOProfile(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(ProfileData.class.getSimpleName());
    }

    public Task<Void>add(ProfileData profileData){
        return databaseReference.push().setValue(profileData);
    }

}

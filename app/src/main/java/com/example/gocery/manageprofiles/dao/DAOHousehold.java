package com.example.gocery.manageprofiles.dao;

import com.example.gocery.manageprofiles.model.Household;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DAOHousehold {

    private DatabaseReference databaseReference;
    private String householdID;


    public DAOHousehold(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Household.class.getSimpleName());
        householdID =  "household_"+FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Task<Void> add(Household household, String userID){
        householdID = "household_"+userID;
        return databaseReference.child(householdID).push().setValue(household);
    }

    public Query get(){
        return databaseReference.child(householdID);
    }

    public Query getSingle(String household_key){
        return databaseReference.child(householdID).child(household_key);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(householdID).child(key).updateChildren(hashMap);
    }
}

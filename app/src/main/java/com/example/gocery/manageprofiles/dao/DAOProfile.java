package com.example.gocery.manageprofiles.dao;

import com.example.gocery.manageprofiles.model.UserProfile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DAOProfile {

    private DatabaseReference databaseReference;
    private String user;

    public DAOProfile(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(UserProfile.class.getSimpleName());
        user = "user_"+FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Task<Void>add(UserProfile userProfile, String userID){
//        return databaseReference.push().setValue(userProfile);
        return databaseReference.child(user).push().setValue(userProfile);
    }

    public Query get(){
        return databaseReference.child(user);
    }

    public Query getSingle(String profile_key){
        return databaseReference.child(user).child(profile_key);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(user).child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key){
        return databaseReference.child(user).child(key).removeValue();
    }

}

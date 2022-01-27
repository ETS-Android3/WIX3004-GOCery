package com.example.gocery.grocerylist.dao;

import android.util.Log;

import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DAOCurrentGroceryItem {

    private DatabaseReference databaseReference;
    private String currentUser;

    public DAOCurrentGroceryItem() {
        currentUser = "user_"+FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(GroceryItem.class.getSimpleName());
    }


    public Task<Void> add(GroceryItem groceryItem)
    {
        return databaseReference.child(currentUser).push().setValue(groceryItem);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(currentUser).child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key) {
        return databaseReference.child(currentUser).child(key).removeValue();
    }

    public Query get() {
        return databaseReference.child(currentUser);
    }

    public Query getSingle(String item_key){
        return databaseReference.child(currentUser).child(item_key);
    }




}

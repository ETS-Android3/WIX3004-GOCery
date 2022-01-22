package com.example.gocery.grocerylist.dao;

import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DAOCurrentGroceryItem {

    private DatabaseReference databaseReference;

    public DAOCurrentGroceryItem() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(GroceryItem.class.getSimpleName());
    }


    public Task<Void> add(GroceryItem groceryItem)
    {
        return databaseReference.push().setValue(groceryItem);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key) {
        return databaseReference.child(key).removeValue();
    }

    public Query get() {
        return databaseReference;
    }

    public Query getSingle(String item_key){
        return databaseReference.child(item_key);
    }




}

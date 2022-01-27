package com.example.gocery.grocerylist.dao;

import android.util.Log;

import com.example.gocery.grocerylist.model.GroceryItem;
import com.example.gocery.grocerylist.model.GroceryTrip;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

public class DAOFinalizeGroceryList {
    private DatabaseReference databaseReferenceGroceryItem, databaseReferenceGroceryTrip;
    private String currentUser;

    public DAOFinalizeGroceryList() {
        currentUser = "user_"+FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReferenceGroceryItem = db.getReference(GroceryItem.class.getSimpleName());
        databaseReferenceGroceryTrip = db.getReference(GroceryTrip.class.getSimpleName());
    }



    public Query getTickedItems(){
        return databaseReferenceGroceryItem.child(currentUser).orderByChild("status").equalTo(true);
    }

    public Task<Void> finalizeGrocery(GroceryTrip groceryTrip){

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String tripKey = "TIME"+ts.getTime();

        // remove data from current grocery items
        for (GroceryItem groceryItem: groceryTrip.getGroceryItems()) {
            databaseReferenceGroceryItem.child(currentUser).child(groceryItem.getKey()).removeValue();
        }

        //save new grocery trip
        return databaseReferenceGroceryTrip.child(currentUser).child(tripKey).setValue(groceryTrip);
    }



}

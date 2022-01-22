package com.example.gocery.grocerylist.dao;

import com.example.gocery.grocerylist.model.GroceryTrip;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DAOCompletedGroceryTrip {

    private DatabaseReference databaseReference;

    public DAOCompletedGroceryTrip() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(GroceryTrip.class.getSimpleName());
    }

    public Query get(){
        return databaseReference;
    }

    public Query getSingle(String groceryTripKey){
        return databaseReference.child(groceryTripKey);
    }


}

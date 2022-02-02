package com.example.gocery.grocerylist.dao;

import com.example.gocery.grocerylist.model.GroceryTrip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DAOCompletedGroceryTrip {

    private DatabaseReference databaseReference;
    private String currentUser;

    public DAOCompletedGroceryTrip() {
        currentUser = "user_"+FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(GroceryTrip.class.getSimpleName());
    }

    public Query get(){
        return databaseReference.child(currentUser);
    }

    public Query getSingle(String groceryTripKey){
        return databaseReference.child(currentUser).child(groceryTripKey);
    }


}

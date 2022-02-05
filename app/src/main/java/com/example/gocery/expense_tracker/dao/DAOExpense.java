package com.example.gocery.expense_tracker.dao;

import com.example.gocery.expense_tracker.model.Expense;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DAOExpense {

    private final DatabaseReference databaseReference;
    private String currentUser;

    public DAOExpense() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        currentUser = "user_" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = db.getReference(Expense.class.getSimpleName());
    }

    public Task<Void> add(Expense expenseItem) {
        return databaseReference.push().setValue(expenseItem);
//        return databaseReference.child(currentUser).push().setValue(expenseItem);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key) {
        return databaseReference.child(key).removeValue();
    }

    public Query get() {
        return databaseReference;
    }

    public Query getSingle(String item_key) {
        return databaseReference.child(item_key);
    }

}

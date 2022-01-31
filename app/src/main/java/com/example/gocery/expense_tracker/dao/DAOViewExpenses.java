package com.example.gocery.expense_tracker.dao;

import com.example.gocery.expense_tracker.model.Expense;
import com.example.gocery.expense_tracker.model.ExpensesList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DAOViewExpenses {

    private DatabaseReference databaseReference;

    public DAOViewExpenses() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(ExpensesList.class.getSimpleName());
    }

    public Query get() {
        return databaseReference;
    }

    public Query getSingle(String expensesListKey) {
        return databaseReference.child(expensesListKey);
    }
}

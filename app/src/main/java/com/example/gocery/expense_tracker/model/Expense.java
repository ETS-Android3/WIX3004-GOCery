package com.example.gocery.expense_tracker.model;

import com.google.firebase.database.Exclude;

import java.util.Date;

public class Expense {

    private String expenseName;
    private String expenseType;
    private String expenseDate;
    private String expenseDesc;
    private Float expenseTotalCost;

    @Exclude
    private String key;

    // An empty constructor is required when using
    // Firebase Realtime Database.
    public Expense() {

    }

    public Expense(String expenseName, String expenseType, String expenseDate, String expenseDesc, Float expenseTotalCost) {
        this.expenseName = expenseName;
        this.expenseType = expenseType;
        this.expenseDate = expenseDate;
        this.expenseDesc = expenseDesc;
        this.expenseTotalCost = expenseTotalCost;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getExpenseDesc() {
        return expenseDesc;
    }

    public void setExpenseDesc(String expenseDesc) {
        this.expenseDesc = expenseDesc;
    }

    public Float getExpenseTotalCost() {
        return expenseTotalCost;
    }

    public void setExpenseTotalCost(Float expenseTotalCost) {
        this.expenseTotalCost = expenseTotalCost;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

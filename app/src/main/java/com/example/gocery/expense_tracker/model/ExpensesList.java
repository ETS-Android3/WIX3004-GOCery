package com.example.gocery.expense_tracker.model;

import com.google.firebase.database.Exclude;

import java.util.List;

public class ExpensesList {

    @Exclude
    private String key;

    private String expenseName;
    private String expenseType;
    private String expenseDate;
    private String expenseDesc;
    private Float expenseTotalCost;
    private List<Expense> expenses;

    public ExpensesList() {
    }

    public ExpensesList(String expenseName, String expenseType, String expenseDate, String expenseDesc, Float expenseTotalCost, List<Expense> expenses) {
        this.expenseName = expenseName;
        this.expenseType = expenseType;
        this.expenseDate = expenseDate;
        this.expenseDesc = expenseDesc;
        this.expenseTotalCost = expenseTotalCost;
        this.expenses = expenses;
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

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

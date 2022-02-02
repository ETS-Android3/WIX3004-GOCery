package com.example.gocery.expense_tracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.gocery.R;
import com.example.gocery.expense_tracker.adapter.ExpensesLVAdapter;
import com.example.gocery.expense_tracker.dao.DAOExpense;
import com.example.gocery.expense_tracker.model.Expense;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExpenseHomeFragment extends Fragment {

    ListView expensesLV;
    DAOExpense dao;
    ExpensesLVAdapter adapter;

    // For pie chart
    PieChart PCExpense;

    public ExpenseHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expense_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // METHOD 1: Navigate Fragment using ID
        FloatingActionButton BtnAddExpense = view.findViewById(R.id.fabAddExpense);

        // Transition animations (https://stackoverflow.com/questions/52794596/how-to-add-animation-to-changing-fragments-using-navigation-component)
        NavOptions.Builder navBuilder = new NavOptions.Builder();
        navBuilder.setEnterAnim(R.anim.slide_from_left).setExitAnim(R.anim.slide_to_left);

        View.OnClickListener OCLAddExpense = v -> Navigation.findNavController(view).navigate(R.id.addExpenseFragment, null, navBuilder.build());
        BtnAddExpense.setOnClickListener(OCLAddExpense);

//        // METHOD 2: Navigate Fragment using Action
//        Button BtnDog = view.findViewById(R.id.BtnDog);
//
//        View.OnClickListener OCLDog = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(view).navigate(R.id.DestDog);
//            }
//        };
//        BtnDog.setOnClickListener(OCLDog);

        PCExpense = view.findViewById(R.id.PCExpense);
        initPieChart();

        // Initialise new DataAccessObject
        dao = new DAOExpense();

        // Initializing variables for listviews.
        expensesLV = view.findViewById(R.id.LVExpensesList);

        // Construct the data source
        ArrayList<Expense> expensesList = new ArrayList<>();

        // Create the adapter to convert the array to views
        adapter = new ExpensesLVAdapter(getContext(), expensesList);

        // Attach the adapter to a ListView
        expensesLV.setAdapter(adapter);

        // Calling a method to get data from
        // Firebase and set data to list view
        loadData();

        expensesLV.setLongClickable(true);
        expensesLV.setOnItemLongClickListener((parent, view1, position, id) -> {

            Expense expense = (Expense) adapter.getItem(position);
            new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()))
                    .setTitle("Delete Expense Record")
                    .setMessage("Are you sure you wan to delete " + expense.getExpenseName() + " record?")
                    .setNegativeButton("CANCEL", null)
                    .setPositiveButton("DELETE", (dialog, which) -> dao.remove(expense.getKey()).addOnSuccessListener(suc -> Toast.makeText(getActivity(), "Expense Record Deleted", Toast.LENGTH_SHORT).show()).addOnFailureListener(er -> Toast.makeText(getActivity(), "Error: Deletion Failed", Toast.LENGTH_SHORT).show()))
                    .show();
            return true;
        });

        expensesLV.setOnItemClickListener((parent, view12, position, id) -> {
            Expense expense = (Expense) adapter.getItem(position);

            // Pass data to the update item
            Bundle result = new Bundle();
            result.putString("ITEM_KEY", expense.getKey());
            getParentFragmentManager().setFragmentResult("updateExpenseFragment", result);
            Navigation.findNavController(view12).navigate(R.id.updateExpenseFragment);
        });
    }

    private void loadData() {
        dao.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Expense> expensesList = new ArrayList<>();
                ArrayList<PieEntry> expensePieEntry = new ArrayList<>();
                Map<String, Integer> typeAmountMap = new HashMap<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Expense expenses = data.getValue(Expense.class);
                    if (expenses != null) {
                        expenses.setKey(data.getKey());
                        expensesList.add(expenses);
                        typeAmountMap.put(expenses.getExpenseType(), typeAmountMap.getOrDefault(expenses.getExpenseType(), 0) + 1);
                    }
                }

                for (String type : typeAmountMap.keySet()) {
                    expensePieEntry.add(new PieEntry(Objects.requireNonNull(typeAmountMap.get(type)).floatValue(), type));
                }

                // Update custom adapter
                adapter.setExpenses(expensesList);
                adapter.notifyDataSetChanged();

                // Show pie chart
                showPieChart(expensePieEntry);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // References:
    // https://medium.com/@clyeung0714/using-mpandroidchart-for-android-application-piechart-123d62d4ddc0
    // https://www.android-examples.com/create-bar-chart-graph-using-mpandroidchart-library/
    // https://www.youtube.com/watch?v=C0O9u0jd6nQ
    private void showPieChart(ArrayList<PieEntry> expensePieEntry) {

        // Collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(expensePieEntry, "Type");
        // Setting text size of the value
        pieDataSet.setValueTextSize(12f);
        // Grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        // Showing the value of the entries, default true if not set
        pieData.setDrawValues(true);

        // Set data to percentage
        pieData.setValueFormatter(new PercentFormatter());

        pieDataSet.setColors(
                Color.parseColor("#6454AC"),
                Color.parseColor("#FC4444"),
                Color.parseColor("#FC6404"),
                Color.parseColor("#FCD444"),
                Color.parseColor("#8CC43C"),
                Color.parseColor("#029658"),
                Color.parseColor("#1ABC9C"),
                Color.parseColor("#5BC0DE")
        );

        PCExpense.setData(pieData);
        PCExpense.invalidate();
    }

    private void initPieChart() {
        // Using percentage as values instead of amount
        PCExpense.setUsePercentValues(true);

        // Remove the description label on the lower left corner, default true if not set
        PCExpense.getDescription().setEnabled(false);

        // Enabling the user to rotate the chart, default true
        PCExpense.setRotationEnabled(true);

        // Adding friction when rotating the pie chart
        PCExpense.setDragDecelerationFrictionCoef(0.9f);

        // Setting the first entry start from right hand side, default starting from top
        PCExpense.setRotationAngle(0);

        // Highlight the entry when it is tapped, default true if not set
        PCExpense.setHighlightPerTapEnabled(true);

        // Setting the color of the hole in the middle, default white
        PCExpense.setHoleColor(Color.parseColor("#FFFFFF"));

    }
}
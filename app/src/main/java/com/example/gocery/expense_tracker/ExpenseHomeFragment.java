package com.example.gocery.expense_tracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.gocery.R;
import com.example.gocery.expense_tracker.dao.DAOViewExpenses;
import com.example.gocery.expense_tracker.model.ExpensesList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseHomeFragment extends Fragment {

    ListView expensesLV;
    DAOViewExpenses dao;

    public ExpenseHomeFragment() {
        // Required empty public constructor
    }

    public static ExpenseHomeFragment newInstance(String param1, String param2) {
        ExpenseHomeFragment fragment = new ExpenseHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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

//        dao = new DAOViewExpenses();
//        // Initializing variables for listviews.
//        expensesLV = view.findViewById(R.id.LVExpensesList);
//        // Initializing our array list
//        ArrayList<ExpensesList> expensesList = new ArrayList<>();
//        adapter = new ExpensesLVAdapter(getContext(), expensesList);
//        expensesLV.setAdapter(adapter);
//
//        // Calling a method to get data from
//        // Firebase and set data to list view
//        loadData();
    }

//    private void loadData() {
//        dao.get().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<ExpensesList> expensesList = new ArrayList<>();
//
//                for(DataSnapshot data: snapshot.getChildren()){
//                    ExpensesList expenses = data.getValue(ExpensesList.class);
//                    expenses.setKey(data.getKey());
//                    expensesList.add(expenses);
//                }
//                adapter.setCurrentExpensesList(expensesList);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }
}
package com.example.gocery.expense_tracker;

import static java.util.Calendar.*;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.gocery.R;
import com.example.gocery.expense_tracker.dao.DAONewExpense;
import com.example.gocery.expense_tracker.model.Expense;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddExpenseFragment extends Fragment {

    // Declaring layout components
    private TextInputEditText ETExpenseName, ETExpenseType, ETExpenseDesc, ETExpenseDate, ETExpenseTotalCost;
    private Button BtnAddExpense;
    final Calendar c = getInstance();

    // Firebase database and storage
    DAONewExpense dao;
    ProgressDialog progressDialog;

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    public static AddExpenseFragment newInstance(String param1, String param2) {
        AddExpenseFragment fragment = new AddExpenseFragment();
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
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Initialise DAO
        dao = new DAONewExpense();

        // Initializing EditText and buttons
        ETExpenseName = view.findViewById(R.id.ETExpenseName);
        ETExpenseType = view.findViewById(R.id.ETExpenseType);
        ETExpenseDesc = view.findViewById(R.id.ETExpenseDesc);
        ETExpenseDate = view.findViewById(R.id.ETExpenseDate);
        ETExpenseTotalCost = view.findViewById(R.id.ETExpenseTotalCost);
        BtnAddExpense = view.findViewById(R.id.BtnAddExpense);

        // Initializing DatePickerDialog
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                c.set(YEAR, year);
                c.set(MONTH, month);
                c.set(DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        // On click listener for displaying DatePickerDialog
        ETExpenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, c.get(YEAR), c.get(MONTH), c.get(DAY_OF_MONTH)).show();
            }
        });

        // On click listener for Add Expense button
        BtnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Getting string values from EditText fields
                String name = ETExpenseName.getText().toString().trim();
                String type = ETExpenseType.getText().toString().trim();
                String desc = ETExpenseDesc.getText().toString().trim();
                String date = ETExpenseDate.getText().toString().trim();
                String totalCostString = ETExpenseTotalCost.getText().toString().trim();

                // Checking whether fields are empty or not (server-side validation)
                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(type) && TextUtils.isEmpty(desc) && TextUtils.isEmpty(date) && TextUtils.isEmpty(totalCostString)) {
                    // If the text fields are empty then show the below message.
                    Toast.makeText(getActivity(), "Please ensure all fields are filled.", Toast.LENGTH_SHORT).show();
                } else {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateObject = null;

                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle("Saving Data...");
                    progressDialog.show();

                    try {
                        dateObject = formatter.parse(date);
                        String dateFormatted = new SimpleDateFormat("dd/MM/yyyy").format(dateObject);
                        Float totalCost = Float.parseFloat(totalCostString);

                        // Create new Expense instance
                        Expense expense = new Expense(
                                name,
                                type,
                                desc,
                                dateFormatted,
                                totalCost
                        );

                        // Save instance to Firebase Realtime DB using DAO
                        dao.add(expense).addOnSuccessListener(suc -> {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(er -> {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getActivity(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                        // Navigate to home module fragment
                        Navigation.findNavController(view).navigate(R.id.expenseHomeFragment);

                    } catch (ParseException e) {
                        // If the text fields are empty then show the message below.
                        Toast.makeText(getActivity(), "Please ensure all fields are filled.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        ETExpenseDate.setText(dateFormat.format(c.getTime()));
    }

}
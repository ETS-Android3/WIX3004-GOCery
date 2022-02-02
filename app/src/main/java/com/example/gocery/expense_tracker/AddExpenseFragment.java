package com.example.gocery.expense_tracker;

import static java.util.Calendar.*;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gocery.MainActivity;
import com.example.gocery.R;
import com.example.gocery.expense_tracker.dao.DAOExpense;
import com.example.gocery.expense_tracker.model.Expense;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddExpenseFragment extends Fragment {

    // Declaring layout components
    private TextInputEditText ETExpenseName, ETExpenseDesc, ETExpenseDate, ETExpenseTotalCost;
    private AutoCompleteTextView ACTVExpenseType;
    private Button BtnAddExpense;
    final Calendar c = getInstance();

    // Firebase database and storage
    DAOExpense dao;
    ProgressDialog progressDialog;

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    public static AddExpenseFragment newInstance() {
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
        dao = new DAOExpense();

        // Initializing EditText and buttons
        ETExpenseName = view.findViewById(R.id.ETExpenseName);
        ACTVExpenseType = view.findViewById(R.id.ACTVExpenseType);
        ETExpenseDesc = view.findViewById(R.id.ETExpenseDesc);
        ETExpenseDate = view.findViewById(R.id.ETExpenseDate);
        ETExpenseTotalCost = view.findViewById(R.id.ETExpenseTotalCost);
        BtnAddExpense = view.findViewById(R.id.BtnAddExpense);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.expense_type, R.layout.expense_type_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.expense_type_dropdown_item);
        // Apply the adapter to the spinner
        ACTVExpenseType.setAdapter(adapter);

        // Initializing DatePickerDialog
        DatePickerDialog.OnDateSetListener date = (view12, year, month, day) -> {
            c.set(YEAR, year);
            c.set(MONTH, month);
            c.set(DAY_OF_MONTH, day);
            updateLabel();
        };

        // On click listener for displaying DatePickerDialog
        ETExpenseDate.setOnClickListener(view1 -> new DatePickerDialog(getActivity(), date, c.get(YEAR), c.get(MONTH), c.get(DAY_OF_MONTH)).show());

        // On click listener for Add Expense button
        BtnAddExpense.setOnClickListener(v -> {

            // Getting string values from EditText fields
            String name = Objects.requireNonNull(ETExpenseName.getText()).toString().trim();
            String type = ACTVExpenseType.getText().toString().trim();
            String desc = Objects.requireNonNull(ETExpenseDesc.getText()).toString().trim();
            String date1 = Objects.requireNonNull(ETExpenseDate.getText()).toString().trim();
            String totalCostString = Objects.requireNonNull(ETExpenseTotalCost.getText()).toString().trim();

            // Checking whether fields are empty or not (server-side validation)
            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(type) && TextUtils.isEmpty(desc) && TextUtils.isEmpty(date1) && TextUtils.isEmpty(totalCostString)) {
                // If the text fields are empty then show the below message.
                Toast.makeText(getActivity(), "Please ensure all fields are filled.", Toast.LENGTH_SHORT).show();
            } else {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                Date dateObject;

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Saving Data...");
                progressDialog.show();

                try {
                    dateObject = formatter.parse(date1);
                    String dateFormatted = null;
                    if (dateObject != null) {
                        dateFormatted = new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(dateObject);
                    }
                    Float totalCost = Float.parseFloat(totalCostString);

                    // Create new Expense instance
                    Expense expense = new Expense(
                            name,
                            type,
                            dateFormatted,
                            desc,
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
        });
    }

    private void updateLabel() {
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.UK);
        ETExpenseDate.setText(dateFormat.format(c.getTime()));
    }

}
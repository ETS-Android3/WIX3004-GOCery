package com.example.gocery;

import android.app.DatePickerDialog;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;

import com.example.gocery.expense_tracker.model.Expense;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

public class AddExpenseActivity extends AppCompatActivity {

    // Declaring variables
    private TextInputEditText ETExpenseName, ETExpenseType, ETExpenseDesc, ETExpenseDate, ETExpenseTotalCost;
    private Button BtnAddExpense;
    final Calendar c = Calendar.getInstance();

    // Creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // Creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    // Creating a variable for
    // our object class
    Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // Showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Initializing EditText and buttons
        ETExpenseName = findViewById(R.id.ETExpenseName);
        ETExpenseType = findViewById(R.id.ETExpenseType);
        ETExpenseDesc = findViewById(R.id.ETExpenseDesc);
        ETExpenseDate = findViewById(R.id.ETExpenseDate);
        ETExpenseTotalCost = findViewById(R.id.ETExpenseTotalCost);
        BtnAddExpense = findViewById(R.id.BtnAddExpense);

        // Get instance of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance("https://gocery-825ca-default-rtdb.asia-southeast1.firebasedatabase.app/");

        // Get reference for our database.
        databaseReference = firebaseDatabase.getReference("ExpenseInfo");

        // Initializing our object class variable.
        expense = new Expense();

        // Initializing DatePickerDialog
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        // On click listener for displaying DatePickerDialog
        ETExpenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddExpenseActivity.this, date, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
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
                    Toast.makeText(AddExpenseActivity.this, "Please ensure all fields are filled.", Toast.LENGTH_SHORT).show();
                } else {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateObject = null;

                    try {
                        dateObject = formatter.parse(date);

                        String dateFormatted = new SimpleDateFormat("dd/MM/yyyy").format(dateObject);
                        Float totalCost = Float.parseFloat(totalCostString);

                        // Else call the method to add data to our database.
                        addDatatoFirebase(name, type, desc, dateFormatted, totalCost);
                    } catch (ParseException e) {
                        // If the text fields are empty then show the below message.
                        Toast.makeText(AddExpenseActivity.this, "Please ensure all fields are filled.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void addDatatoFirebase(String name, String type, String desc, String date, float totalCost) {
        // Set data in our object class.
        expense.setExpenseName(name);
        expense.setExpenseType(type);
        expense.setExpenseDesc(desc);
        expense.setExpenseDate(date);
        expense.setExpenseTotalCost(totalCost);

        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                // databaseReference.setValue(expense);
                databaseReference.child("users").child("expense").push().setValue(expense);

                // after adding this data we are showing toast message.
                Toast.makeText(AddExpenseActivity.this, "Data successfully added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(AddExpenseActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        ETExpenseDate.setText(dateFormat.format(c.getTime()));
    }

    // Enable the back function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
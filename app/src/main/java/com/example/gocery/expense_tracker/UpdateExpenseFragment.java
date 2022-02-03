package com.example.gocery.expense_tracker;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.gocery.R;
import com.example.gocery.expense_tracker.dao.DAOExpense;
import com.example.gocery.expense_tracker.model.Expense;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class UpdateExpenseFragment extends Fragment {


    // Declaring layout components
    private EditText ETExpenseName, ETExpenseDesc, ETExpenseTotalCost;
    private AutoCompleteTextView ACTVExpenseType;
    private MaterialTextView ETExpenseDate;
    private Button BtnUpdateExpense;
    final Calendar c = getInstance();

    // Firebase database and storage
    DAOExpense dao;
    String itemKey;

    public UpdateExpenseFragment() {
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
        return inflater.inflate(R.layout.fragment_update_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialise DAO
        dao = new DAOExpense();

        // Initializing EditText and buttons
        ETExpenseName = view.findViewById(R.id.ETExpenseName);
        ACTVExpenseType = view.findViewById(R.id.ACTVExpenseType);
        ETExpenseDesc = view.findViewById(R.id.ETExpenseDesc);
        ETExpenseDate = view.findViewById(R.id.ETExpenseDate);
        ETExpenseTotalCost = view.findViewById(R.id.ETExpenseTotalCost);
        BtnUpdateExpense = view.findViewById(R.id.BtnUpdateExpense);

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

        getParentFragmentManager().setFragmentResultListener("updateExpenseFragment", this, (requestKey, result) -> {
            setItemData("" + result.get("ITEM_KEY"));
//                Toast.makeText(getContext(), "RECEIVED: " + result.get("ITEM_KEY"), Toast.LENGTH_SHORT).show();
        });

        BtnUpdateExpense.setOnClickListener(v -> {
            saveData(v);

            // Transition animations (https://stackoverflow.com/questions/52794596/how-to-add-animation-to-changing-fragments-using-navigation-component)
//            NavOptions.Builder navBuilder = new NavOptions.Builder();
//            navBuilder.setEnterAnim(R.anim.fade_scale_in).setExitAnim(R.anim.fade_scale_out);
            Navigation.findNavController(v).navigate(R.id.expenseHomeFragment);
        });
    }

    private void saveData(View v) {
        HashMap<String, Object> hashMap = new HashMap<>();

        try {
            hashMap.put("expenseName", ETExpenseName.getText().toString());
            hashMap.put("expenseType", ACTVExpenseType.getText().toString());
            hashMap.put("expenseDesc", ETExpenseDesc.getText().toString());
            hashMap.put("expenseTotalCost", Float.parseFloat(ETExpenseTotalCost.getText().toString().trim()));

            String date = ETExpenseDate.getText().toString().trim();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
            Date dateObject = formatter.parse(date);
            String dateFormatted = new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Objects.requireNonNull(dateObject));
            hashMap.put("expenseDate", dateFormatted);

            Toast.makeText(getActivity(), "Expense record added successfully.", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DAOExpense dao = new DAOExpense();
        dao.update(this.itemKey, hashMap).addOnFailureListener(er -> Toast.makeText(v.getContext(), "Error", Toast.LENGTH_SHORT).show());
    }

    private void setItemData(String item_key) {
        this.itemKey = item_key;
        dao.getSingle(item_key).addValueEventListener(new ValueEventListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Expense expense = snapshot.getValue(Expense.class);

                try {
                    ETExpenseName.setText(Objects.requireNonNull(expense).getExpenseName());
                    ACTVExpenseType.setText(expense.getExpenseType());
                    ETExpenseDesc.setText(expense.getExpenseDesc());
                    ETExpenseTotalCost.setText(String.format("%.2f", expense.getExpenseTotalCost()));
                    ETExpenseDate.setText(expense.getExpenseDate());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateLabel() {
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.UK);
        ETExpenseDate.setText(dateFormat.format(c.getTime()));
    }
}
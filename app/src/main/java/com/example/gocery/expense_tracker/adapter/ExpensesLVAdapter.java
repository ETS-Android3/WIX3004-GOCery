package com.example.gocery.expense_tracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gocery.R;
import com.example.gocery.expense_tracker.model.Expense;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// References:
// https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
public class ExpensesLVAdapter extends BaseAdapter {

    List<Expense> expenses;
    Context context;
    LayoutInflater inflater;

    public ExpensesLVAdapter(Context context, List<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
        inflater = LayoutInflater.from(context);
    }

    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
    }

    @Override
    public int getCount() {
        return expenses.size();
    }

    @Override
    public Object getItem(int i) {
        return expenses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


//    // METHOD 1: Populating ListView using custom Adapter
//    @Override
//    public View getView(int i, View convertView, ViewGroup parent) {
//
//        // Get the data item for this position
//        Expense expense = (Expense) this.getItem(i);
//
//        // Check if an existing view is being reused, otherwise inflate the view
//        if (convertView == null) {
//            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.expenses_lv_item, parent, false);
//        }
//
//        // Lookup view for data population
//        TextView expenseName = convertView.findViewById(R.id.TVExpenseName);
//        TextView expenseTotalCost = convertView.findViewById(R.id.TVExpenseTotalCost);
//
//        // Populate the data into the template view using the data object
//        expenseName.setText(expense.getExpenseName().trim());
//        expenseTotalCost.setText(expense.getExpenseTotalCost().toString().trim());
//
//        // Return the completed view to render on screen
//        return convertView;
//    }

    // METHOD 2: Populating ListView using custom Adapter with ViewHolder pattern
    // To improve performance, we should modify the custom adapter by applying the
    // ViewHolder pattern which speeds up the population of the ListView considerably
    // by caching view lookups for smoother, faster item loading:
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Expense expense = (Expense) this.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.expenses_lv_item, parent, false);

            viewHolder.expenseName = convertView.findViewById(R.id.TVExpenseName);
            viewHolder.expenseTotalCost = convertView.findViewById(R.id.TVExpenseTotalCost);
            viewHolder.expenseType = convertView.findViewById(R.id.TVExpenseType);
            viewHolder.expenseDateDay = convertView.findViewById(R.id.TVExpenseDateDay);
            viewHolder.expenseDateMonth = convertView.findViewById(R.id.TVExpenseDateMonth);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.expenseName.setText(expense.getExpenseName());
        viewHolder.expenseType.setText(expense.getExpenseType());
        viewHolder.expenseTotalCost.setText("RM " + String.format("%.2f", expense.getExpenseTotalCost()));

        String expenseDate = expense.getExpenseDate();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        DateFormat monthFormatter = new SimpleDateFormat("MMM", Locale.UK);

        try {
            Date readDate = formatter.parse(expenseDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(readDate);

            String dayOfWeek = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            String month = monthFormatter.format(readDate).toUpperCase();
            viewHolder.expenseDateDay.setText(dayOfWeek);
            viewHolder.expenseDateMonth.setText(month);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the completed view to render on screen
        return convertView;
    }


    // View lookup cache
    private static class ViewHolder {
        TextView expenseName;
        TextView expenseType;
        TextView expenseDateMonth;
        TextView expenseDateDay;
        TextView expenseTotalCost;
    }
}


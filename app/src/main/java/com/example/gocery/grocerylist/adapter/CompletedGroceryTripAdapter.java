package com.example.gocery.grocerylist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gocery.R;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.example.gocery.grocerylist.model.GroceryTrip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CompletedGroceryTripAdapter extends BaseAdapter {

    List<GroceryTrip> groceryTrips;
    Context context;
    LayoutInflater inflater;

    public CompletedGroceryTripAdapter(Context context, List<GroceryTrip> groceryTrips) {
        this.context = context;
        this.groceryTrips = groceryTrips;
        inflater = LayoutInflater.from(context);
    }

    public void setCurrentGroceryTrips(ArrayList<GroceryTrip> groceryTrips) {
        this.groceryTrips = groceryTrips;
    }

    @Override
    public int getCount() {
        return groceryTrips.size();
    }

    @Override
    public Object getItem(int position) {
        return groceryTrips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        GroceryTrip groceryTrip = (GroceryTrip) this.getItem(position);

        if(convertView == null){
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_completed_grocery, parent, false);
        }

        TextView dateTime, tripName, completedBy;

        dateTime = convertView.findViewById(R.id.tv_completedDateAndTime);
        tripName = convertView.findViewById(R.id.tv_shoppingTripName);
        completedBy = convertView.findViewById(R.id.tv_completedBy);

        dateTime.setText(groceryTrip.getDatetime());
        tripName.setText(groceryTrip.getName());
        completedBy.setText(groceryTrip.getCompletedBy());

        return convertView;
    }
}

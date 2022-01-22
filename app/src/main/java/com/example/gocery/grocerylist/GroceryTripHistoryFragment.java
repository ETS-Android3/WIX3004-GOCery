package com.example.gocery.grocerylist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gocery.R;

import com.example.gocery.grocerylist.adapter.CompletedGroceryTripAdapter;
import com.example.gocery.grocerylist.adapter.FinalizeGroceryListAdapter;
import com.example.gocery.grocerylist.dao.DAOCompletedGroceryTrip;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.example.gocery.grocerylist.model.GroceryTrip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroceryTripHistoryFragment extends Fragment {


    ListView listview;
    DAOCompletedGroceryTrip dao;
    FinalizeGroceryListAdapter adapter;
    String TRIP_KEY;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = new DAOCompletedGroceryTrip();

        listview = view.findViewById(R.id.lv_groceryHistory);
        ArrayList<GroceryItem> groceryItems = new ArrayList<>();
        adapter = new FinalizeGroceryListAdapter(getContext(), groceryItems);
        listview.setAdapter(adapter);

        // get the passed data from the previous fragement
        getParentFragmentManager().setFragmentResultListener("viewGroceryTrip", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
//                TRIP_KEY = ""+result.get("TRIP_KEY");
                loadData(""+result.get("TRIP_KEY"));

                Toast.makeText(getContext(), "RECEIVED: "+result.get("TRIP_KEY"), Toast.LENGTH_SHORT).show();
            }
        });


//        loadData();



    }

    private void loadData(String key) {
        dao.getSingle(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<GroceryItem> cgis = new ArrayList<>();
                Log.e("DATA PULLED", snapshot.getValue(GroceryTrip.class).toString());
                GroceryTrip groceryTrip = snapshot.getValue(GroceryTrip.class);
                adapter.setCurrentGroceryItems((ArrayList<GroceryItem>) groceryTrip.getGroceryItems());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grocery_trip_history, container, false);
    }
}
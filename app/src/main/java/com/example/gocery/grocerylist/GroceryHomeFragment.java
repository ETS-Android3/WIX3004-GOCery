package com.example.gocery.grocerylist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gocery.R;

import com.example.gocery.grocerylist.adapter.CompletedGroceryTripAdapter;
import com.example.gocery.grocerylist.dao.DAOCompletedGroceryTrip;
import com.example.gocery.grocerylist.dao.DAOCurrentGroceryItem;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.example.gocery.grocerylist.model.GroceryTrip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GroceryHomeFragment extends Fragment {


    ListView listView;
    CompletedGroceryTripAdapter adapter;
    DAOCompletedGroceryTrip dao;
    DAOCurrentGroceryItem daoCurrentGroceryItem;

//    ExtendedFloatingActionButton startTripBtn;

    TextView numberOfItems;
    private static final int REQUEST_CODE = 99;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CardView cardView = view.findViewById(R.id.cv_currentGroceryList);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_viewCurrentGroceryList);
            }
        });


//        startTripBtn = view.findViewById(R.id.fabtn_startShopping);
//        startTripBtn.setOnClickListener(v -> {
//
//            if(ContextCompat.checkSelfPermission(getContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//            }else{
//                Navigation.findNavController(v).navigate(R.id.nav_sstartShopping);
//            }
//        });









        numberOfItems = view.findViewById(R.id.tv_currentItemInList);

        daoCurrentGroceryItem = new DAOCurrentGroceryItem();
        dao = new DAOCompletedGroceryTrip();
        listView = view.findViewById(R.id.lv_completedGroceryTrip);
        ArrayList<GroceryTrip> groceryTrips = new ArrayList<>();
        adapter = new CompletedGroceryTripAdapter(getContext(), groceryTrips);
        listView.setAdapter(adapter);
        loadData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroceryTrip groceryTrip = (GroceryTrip) adapter.getItem(position);

                // PASSING DATA TO THE NEXT FRAGMENT
                Bundle result = new Bundle();
                result.putString("TRIP_KEY", groceryTrip.getKey());
                getParentFragmentManager().setFragmentResult("viewGroceryTrip",result);
//                Toast.makeText(getContext(), "SENT: "+groceryTrip.getKey(), Toast.LENGTH_SHORT).show();

                Navigation.findNavController(view).navigate(R.id.nav_viewCompletedGroceryTrip);
            }
        });

    }

    private void loadData() {
        dao.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<GroceryTrip> groceryTrips = new ArrayList<>();
                for(DataSnapshot data: snapshot.getChildren()){
                    GroceryTrip groceryTrip = data.getValue(GroceryTrip.class);
                    groceryTrip.setKey(data.getKey());
                    groceryTrips.add(groceryTrip);
                }

                adapter.setCurrentGroceryTrips(groceryTrips);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        daoCurrentGroceryItem.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0){
                    numberOfItems.setText("No Item In List");
                }else{
                    numberOfItems.setText(snapshot.getChildrenCount()+" Items In List");
                }
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
        return inflater.inflate(R.layout.fragment_grocery_home, container, false);
    }



}
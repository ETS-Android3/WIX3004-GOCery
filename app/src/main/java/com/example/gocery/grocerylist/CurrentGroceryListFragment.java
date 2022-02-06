package com.example.gocery.grocerylist;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.gocery.grocerylist.adapter.CurrentGroceryListAdapter;
import com.example.gocery.grocerylist.dao.DAOCurrentGroceryItem;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CurrentGroceryListFragment extends Fragment {


    ExtendedFloatingActionButton btnActions;
    FloatingActionButton btnAddItem, btnCompleteGrocery, btnMap;
    TextView tvAddItem, tvCompleteGrocery, tvMap;

    ListView listView;
    CurrentGroceryListAdapter adapter;

    DAOCurrentGroceryItem dao;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        dao = new DAOCurrentGroceryItem();

        listView = view.findViewById(R.id.lv_currentGroceryList);
        ArrayList<GroceryItem> initialGroceryItems = new ArrayList<>();
        adapter = new CurrentGroceryListAdapter(getContext(), initialGroceryItems);
        listView.setAdapter(adapter);
        loadData();

        // LIST VIEW
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                GroceryItem cgi = (GroceryItem) adapter.getItem(position);
                new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Delete Item")
                    .setMessage("Deleting "+ cgi.getName()+". Are you sure?")
                    .setNegativeButton("CANCEL", null)
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String imagePath = null;
                            if(cgi.getImage() != null){
                                imagePath = cgi.getImage();
                            }

                            dao.remove(cgi.getKey(), imagePath).addOnSuccessListener(suc->{
                                Toast.makeText(getActivity(), "Item Deleted", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(er->{
                                Toast.makeText(getActivity(), "Error: Deletion Failed", Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .show();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroceryItem groceryItem = (GroceryItem) adapter.getItem(position);

                // Pass data to the update item
                Bundle result = new Bundle();
                result.putString("ITEM_KEY", groceryItem.getKey());
                getParentFragmentManager().setFragmentResult("updateGroceryItem", result);
                Navigation.findNavController(view).navigate(R.id.nav_updateGroceryItem);
            }
        });

        // ACTION BUTTONS
        btnActions = view.findViewById(R.id.fabtn_actions);

        btnAddItem = view.findViewById(R.id.fabtn_addNewItem);
        tvAddItem = view.findViewById(R.id.tv_fabtn_addNewItem);
        btnCompleteGrocery = view.findViewById(R.id.fabtn_completeGrocery);
        tvCompleteGrocery = view.findViewById(R.id.tv_fabtn_completeGrocery);
        btnMap = view.findViewById(R.id.fabtn_map);
        tvMap = view.findViewById(R.id.tv_fabtn_map);

        // initial view
        btnAddItem.setVisibility(View.GONE);
        btnCompleteGrocery.setVisibility(View.GONE);
        btnMap.setVisibility(View.GONE);
        tvAddItem.setVisibility(View.GONE);
        tvCompleteGrocery.setVisibility(View.GONE);
        tvMap.setVisibility(View.GONE);
        btnActions.shrink();

        View.OnClickListener expand_btn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnAddItem.getVisibility() == View.VISIBLE){
                    btnAddItem.hide();
                    btnCompleteGrocery.hide();
                    btnMap.hide();
                    tvAddItem.setVisibility(View.GONE);
                    tvCompleteGrocery.setVisibility(View.GONE);
                    tvMap.setVisibility(View.GONE);
                    btnActions.shrink();
                } else {
                    btnAddItem.show();
                    btnCompleteGrocery.show();
                    btnMap.show();
                    tvAddItem.setVisibility(View.VISIBLE);
                    tvCompleteGrocery.setVisibility(View.VISIBLE);
                    tvMap.setVisibility(View.VISIBLE);
                    btnActions.extend();
                }
            }
        };

        btnActions.setOnClickListener(expand_btn);


        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
                }else{
                    ArrayList<GroceryItem> list = (ArrayList<GroceryItem>) adapter.getGroceryItems();
                    ArrayList<String> places_id = new ArrayList<>();
                    for(GroceryItem groceryItem: list){
                        // Get the list of unpurchased item to be included in the route.
                        if(!groceryItem.getStatus()){
                            String place_id = groceryItem.getLocationID();
                            if(!places_id.contains(place_id) && place_id!=null){
                                places_id.add(place_id);
                            };
                        }
                    }

                    if (places_id.size() > 0){
                        // Pass data to the update item
                        Bundle result = new Bundle();
                        result.putStringArrayList("LOCATIONS", places_id);
                        getParentFragmentManager().setFragmentResult("locations", result);
                        Navigation.findNavController(v).navigate(R.id.nav_startMapFragment);
                    }else{
                        Toast.makeText(getContext(), "No Item with Location", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_addNewGroceryItem);
            }
        });

        btnCompleteGrocery.setOnClickListener(v -> {
            dao.getTickedItems().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() == 0){
                        Toast.makeText(getContext(), "No Item Ticked", Toast.LENGTH_SHORT).show();
                    }else{
                        Navigation.findNavController(v).navigate(R.id.nav_finalizeGroceryList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_grocery_list, container, false);
    }


    public void loadData(){
        dao.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<GroceryItem> cgis = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren()){
                    GroceryItem cgi = data.getValue(GroceryItem.class);
                    cgi.setKey(data.getKey());
                    cgis.add(cgi);
                }
                adapter.setCurrentGroceryItems(cgis);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
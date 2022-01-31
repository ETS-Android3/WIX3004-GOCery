package com.example.gocery.grocerylist;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gocery.R;

import com.example.gocery.grocerylist.adapter.CurrentGroceryListAdapter;
import com.example.gocery.grocerylist.dao.DAOCurrentGroceryItem;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CurrentGroceryListFragment extends Fragment {


    FloatingActionButton btnAddItem, btnCompleteGrocery;

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


        btnAddItem = view.findViewById(R.id.fabtn_addNewItem);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_addNewGroceryItem);
            }
        });


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



        btnCompleteGrocery = view.findViewById(R.id.fabtn_completeGrocery);
        btnCompleteGrocery.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_finalizeGroceryList);
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_grocery_list, container, false);
    }

    public void loadData(){
//        swipeRefreshLayout.setRefreshing(true);
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
//        swipeRefreshLayout.setRefreshing(false);
    }
}
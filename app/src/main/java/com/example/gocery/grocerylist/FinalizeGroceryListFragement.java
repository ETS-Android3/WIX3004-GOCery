package com.example.gocery.grocerylist;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gocery.R;

import com.example.gocery.grocerylist.adapter.FinalizeGroceryListAdapter;
import com.example.gocery.grocerylist.dao.DAOFinalizeGroceryList;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.example.gocery.grocerylist.model.GroceryTrip;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FinalizeGroceryListFragement extends Fragment {

    FloatingActionButton btnSave, btnGoToVoucher;

    ListView listView;
    FinalizeGroceryListAdapter adapter;

    DAOFinalizeGroceryList dao;

    //SAVING DATA
    GroceryTrip groceryTrip;
    Boolean saveStatus;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

            btnSave = view.findViewById(R.id.fbtn_saveShoppingList);
            btnGoToVoucher = view.findViewById(R.id.fbtn_goToVoucher);

            dao = new DAOFinalizeGroceryList();

            listView = view.findViewById(R.id.lv_finalizeShopping);
            ArrayList<GroceryItem> gi = new ArrayList<>();
            adapter = new FinalizeGroceryListAdapter(getContext(), gi);
            listView.setAdapter(adapter);
            loadData();


            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("SAVE", "BUTTON CLICKED");
                    showDialog(getContext());
                }
            });

    }

    private void showDialog(Context context) {
        Log.e("DIALOG", "Dialog is shown ");
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_finalize_grocery);

        final TextInputEditText tripName = dialog.findViewById(R.id.tiet_tripName);
        final Button cancel = dialog.findViewById(R.id.btn_dialogCancel);

        cancel.setOnClickListener(v -> {
            Log.e("DIALOG-CANCEL", "Dialog is DISMISS ");
            dialog.dismiss();
        });

        final Button save = dialog.findViewById(R.id.btn_dialogSave);
        save.setOnClickListener(v -> {

            if(tripName.getText().toString().isEmpty()){
                Toast.makeText(getContext(), "Please Enter Trip Name", Toast.LENGTH_SHORT).show();
            }else{
                groceryTrip = new GroceryTrip();
                groceryTrip.setName(tripName.getText().toString());

                groceryTrip.setCompletedBy("USER NAME HERE");

                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a, d MMMM yyyy");
                groceryTrip.setDatetime(sdf.format(new Timestamp(System.currentTimeMillis())));

                // get data from adapter
                final ArrayList<GroceryItem> groceryItems = (ArrayList<GroceryItem>) adapter.getList();
                groceryTrip.setGroceryItems(groceryItems);

                // SAVE THE DATA
                saveStatus = false;
                dao.finalizeGrocery(groceryTrip).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Trip Saved", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }

        });

        dialog.show();
    }


    private void loadData() {
        dao.getTickedItems().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<GroceryItem> gis = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren()){
                    GroceryItem gi = data.getValue(GroceryItem.class);
                    gi.setKey(data.getKey());
                    gis.add(gi);
                }
                adapter.setCurrentGroceryItems(gis);
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
        return inflater.inflate(R.layout.fragment_finalize_grocery_list, container, false);
    }
}
package com.example.gocery.grocerylist;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gocery.R;
import com.example.gocery.grocerylist.dao.DAOCurrentGroceryItem;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;


public class UpdateGroceryItemFragment extends Fragment {

    FloatingActionButton btnSave;
    FirebaseStorage firebaseStorage;
    Button btnAttachImage;
    TextInputEditText itemName, itemQuantity, itemDesc;
    //    EditText imgPath, locationData;

    GroceryItem initialGroceryItem;
    String itemKey;
    ImageView imageView;


    // database and storage
    DAOCurrentGroceryItem dao;
    StorageReference storageReference;
    ProgressDialog progressDialog;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        itemName = view.findViewById(R.id.tiet_itemName);
        itemQuantity = view.findViewById(R.id.tiet_itemQuantity);
        itemDesc = view.findViewById(R.id.tiet_itemDescription);
//        imgPath = view.findViewById(R.id.et_imagePath);
//        locationData = view.findViewById(R.id.et_locationData);

        btnAttachImage = view.findViewById(R.id.btn_attachImage);
        imageView = view.findViewById(R.id.iv_itemImagePreview);

        btnSave = view.findViewById(R.id.fabtn_groceryUpdate_save);


        dao = new DAOCurrentGroceryItem();

        getParentFragmentManager().setFragmentResultListener("updateGroceryItem", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                setItemData(""+result.get("ITEM_KEY"));
                Toast.makeText(getContext(), "RECEIVED: "+result.get("ITEM_KEY"), Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(v);
                Navigation.findNavController(v).navigate(R.id.nav_groceryItemUpdated);
            }

        });








    }

    private void saveData(View v) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("name", itemName.getText().toString());
        hashMap.put("quantity", Integer.parseInt(itemQuantity.getText().toString()));
        hashMap.put("description", itemDesc.getText().toString());

        DAOCurrentGroceryItem dao = new DAOCurrentGroceryItem();
        dao.update(this.itemKey, hashMap).addOnFailureListener(er->{
            Toast.makeText(v.getContext(), "Error", Toast.LENGTH_SHORT).show();
        });
    }

    private void setItemData(String item_key) {
        this.itemKey = item_key;
//        dao.getSingle(item_key).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                GroceryItem groceryItem = snapshot.getValue(GroceryItem.class);
////                Log.e("CURRENT ITEM", groceryItem.toString());
//                itemName.setText(groceryItem.getName());
//                itemQuantity.setText(""+groceryItem.getQuantity());
//                itemDesc.setText(groceryItem.getDescription());
//
//                // firebase image?
//                FirebaseApp firebaseApp = FirebaseApp.initializeApp(getContext());
//                firebaseStorage = FirebaseStorage.getInstance(firebaseApp);
//                StorageReference storageReference = firebaseStorage.getReference();
//
//                storageReference.child(""+groceryItem.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Glide.with(getContext())
//                                .load(uri)
//                                .placeholder(R.drawable.spinning_loading)
//                                .error(R.drawable.gocery_logo_only)
//                                .into(imageView);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });

        dao.getSingle(item_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GroceryItem groceryItem = snapshot.getValue(GroceryItem.class);
//                Log.e("CURRENT ITEM", groceryItem.toString());
                itemName.setText(groceryItem.getName());
                itemQuantity.setText(""+groceryItem.getQuantity());
                itemDesc.setText(groceryItem.getDescription());

                // firebase image?
                FirebaseApp firebaseApp = FirebaseApp.initializeApp(getContext());
                firebaseStorage = FirebaseStorage.getInstance(firebaseApp);
                StorageReference storageReference = firebaseStorage.getReference();

                storageReference.child(""+groceryItem.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getContext())
                                .load(uri)
                                .placeholder(R.drawable.spinning_loading)
                                .error(R.drawable.gocery_logo_only)
                                .into(imageView);
                    }
                });

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
        return inflater.inflate(R.layout.fragment_update_grocery_item, container, false);
    }
}
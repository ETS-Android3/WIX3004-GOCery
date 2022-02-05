package com.example.gocery.grocerylist;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gocery.R;

import com.example.gocery.grocerylist.dao.DAOCurrentGroceryItem;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;


public class AddGroceryItemFragment extends Fragment {


    final int REQUEST_IMAGE = 999;
    Uri imageUri;
    ImageView imageView;
    String locationName, locationID;

    FloatingActionButton btnSave;
    Button btnAttachImage, btnAttachLocation;
    TextInputEditText itemName, itemQuantity, itemDesc;
    TextView itemLocation;

    // database and storage
    DAOCurrentGroceryItem dao;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    HashMap<String, Object> tempGroceryItem;

    public AddGroceryItemFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        itemName = view.findViewById(R.id.tiet_itemName);
        itemQuantity = view.findViewById(R.id.tiet_itemQuantity);
        itemDesc = view.findViewById(R.id.tiet_itemDescription);
        itemLocation = view.findViewById(R.id.tv_locationName);

        btnAttachImage = view.findViewById(R.id.btn_attachImage);
        imageView = view.findViewById(R.id.iv_itemImagePreview);

        dao = new DAOCurrentGroceryItem();

        btnSave = view.findViewById(R.id.fabtn_groceryAdd_save);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Saving Data...");
                progressDialog.show();

                if(validate(v)){
                    String creatorName = "CREATOR_NAME";

                    if(imageUri != null){
                        String currentUser = "user_"+ FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Timestamp ts = new Timestamp(System.currentTimeMillis());
                        String fileName = "grocery_image/"+currentUser+"/"+creatorName+"-"+ts.getTime();

                        storageReference = FirebaseStorage.getInstance().getReference(fileName);
                        storageReference.putFile(imageUri).addOnSuccessListener(suc->{


                            GroceryItem groceryItem = new GroceryItem(
                                    itemName.getText().toString(),
                                    Integer.parseInt(itemQuantity.getText().toString()),
                                    false, // set checkbox to false
                                    itemDesc.getText().toString(), //nullable
                                    fileName
                            );

                            groceryItem.setCreatedBy(creatorName);
                            if(locationID != null){
                                groceryItem.setLocationID(locationID);
                                groceryItem.setLocationName(locationName);
                            }

                            dao.add(groceryItem).addOnSuccessListener(succ->{
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(getActivity(), "upload success", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.nav_groceryItemAdded);

                            }).addOnFailureListener(er->{
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(getActivity(), ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                            });


                        }).addOnFailureListener(er->{
                            Toast.makeText(getActivity(), "upload failed image", Toast.LENGTH_SHORT).show();
                        });

                    }else{

                        // saving data to database without image
                        GroceryItem groceryItem = new GroceryItem(
                                itemName.getText().toString(),
                                Integer.parseInt(itemQuantity.getText().toString()),
                                false, // set checkbox to false
                                itemDesc.getText().toString(), //nullable
                                null
                        );

                        groceryItem.setCreatedBy(creatorName);
                        if(locationID != null){
                            groceryItem.setLocationID(locationID);
                            groceryItem.setLocationName(locationName);
                        }

                        dao.add(groceryItem).addOnSuccessListener(suc->{
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            Navigation.findNavController(view).navigate(R.id.nav_groceryItemAdded);
                        }).addOnFailureListener(er->{
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getActivity(), ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    }

                }else{
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "Form Incomplete", Toast.LENGTH_SHORT).show();
                }
            }
        });




        // Selecting Image
        btnAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        btnAttachLocation = view.findViewById(R.id.btn_attachLocation);
        btnAttachLocation.setOnClickListener(v -> {

            if(ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 42);
            }else{
                HashMap<String, Object> tempGroceryItem = new HashMap<>();

                tempGroceryItem.put("name", itemName.getText().toString());
                tempGroceryItem.put("quantity", Integer.parseInt(itemQuantity.getText().toString().isEmpty() ? "0" : itemQuantity.getText().toString()));
                tempGroceryItem.put("desc", itemDesc.getText().toString());
                tempGroceryItem.put("img", imageUri);

                // to tell where to go next
                tempGroceryItem.put("PREV_PAGE", (String) "add_item");

                // Pass data to the update item
                Bundle result = new Bundle();
                result.putSerializable("GROCERY_HASHMAP", tempGroceryItem);
                getParentFragmentManager().setFragmentResult("TEMP_GROCERY_ITEM", result);
                Navigation.findNavController(view).navigate(R.id.nav_selectLocation_add);
            }


        });

        getParentFragmentManager().setFragmentResultListener("SELECTED_LOCATION", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                tempGroceryItem = (HashMap<String, Object>) result.get("GROCERY_HASHMAP");


                itemName.setText((String) tempGroceryItem.get("name"));
                itemQuantity.setText(""+tempGroceryItem.get("quantity"));
                itemDesc.setText((String) tempGroceryItem.get("desc"));
                itemLocation.setText((String) tempGroceryItem.get("locationName"));
                locationID = (String) tempGroceryItem.get("locationID");
                locationName = (String) tempGroceryItem.get("locationName");

                if (tempGroceryItem.get("img")!=null){
                    imageUri = (Uri) tempGroceryItem.get("img");
                    imageView.setImageURI(imageUri);
                }


                Log.e("Passed Data LS",tempGroceryItem.toString());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_grocery_item, container, false);
    }


    public boolean validate(View v){
        String name, desc;
        int quantity;

        name = itemName.getText().toString();
        if(name.isEmpty()){
            itemName.setError("Please Enter Value");
            return false;
        }

        quantity = Integer.parseInt(itemQuantity.getText().toString().isEmpty() ? "0" : itemQuantity.getText().toString());
        if(quantity <= 0){
            itemQuantity.setError("Minimum Quantity is 1");
            return false;
        }
        return true;
    }


    public void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE && data != null && data.getData() != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }else{
            imageUri = null;
            imageView.setImageURI(null);
        }
    }


}
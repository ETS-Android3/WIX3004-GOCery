package com.example.gocery.grocerylist;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gocery.R;
import com.example.gocery.grocerylist.dao.DAOCurrentGroceryItem;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.util.HashMap;


public class UpdateGroceryItemFragment extends Fragment {

    FloatingActionButton btnSave;
    FirebaseStorage firebaseStorage;
    Button btnAttachImage, btnAttachLocation;
    TextInputEditText itemName, itemQuantity, itemDesc;
    TextView itemLocation;

    GroceryItem groceryItem;
    String itemKey;

    final int REQUEST_IMAGE = 1;
    Uri imageUri;
    String imageURL;
    ImageView imageView;

    String locationName, locationID;

    // database and storage
    DAOCurrentGroceryItem dao;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    HashMap<String, Object> tempGroceryItem;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        itemName = view.findViewById(R.id.tiet_itemName);
        itemQuantity = view.findViewById(R.id.tiet_itemQuantity);
        itemDesc = view.findViewById(R.id.tiet_itemDescription);
        itemLocation = view.findViewById(R.id.tv_locationName);

        btnAttachImage = view.findViewById(R.id.btn_attachImage);
        imageView = view.findViewById(R.id.iv_itemImagePreview);
        btnSave = view.findViewById(R.id.fabtn_groceryUpdate_save);


        dao = new DAOCurrentGroceryItem();

        getParentFragmentManager().setFragmentResultListener("updateGroceryItem", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                setItemData(""+result.get("ITEM_KEY"));
//                Toast.makeText(getContext(), "RECEIVED: "+result.get("ITEM_KEY"), Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveData(v);
//                Navigation.findNavController(v).navigate(R.id.nav_groceryItemUpdated);

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


                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("name", itemName.getText().toString());
                            hashMap.put("quantity", Integer.parseInt(itemQuantity.getText().toString()));
                            hashMap.put("description", itemDesc.getText().toString());
                            hashMap.put("image", imageURL);

                            Log.e("HASHMAP", hashMap.toString());

                            if(locationID != null){
                                hashMap.put("locationName", locationName);
                                hashMap.put("locationID", locationID);
                            }

                            DAOCurrentGroceryItem dao = new DAOCurrentGroceryItem();
                            dao.update(itemKey, hashMap, fileName).addOnSuccessListener(succ -> {
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Navigation.findNavController(view).navigate(R.id.nav_groceryItemUpdated);
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

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("name", itemName.getText().toString());
                        hashMap.put("quantity", Integer.parseInt(itemQuantity.getText().toString()));
                        hashMap.put("description", itemDesc.getText().toString());

                        Log.e("HASHMAP", hashMap.toString());
                        if(locationID != null){
                            hashMap.put("locationName", locationName);
                            hashMap.put("locationID", locationID);
                        }


                        DAOCurrentGroceryItem dao = new DAOCurrentGroceryItem();
                        dao.update(itemKey, hashMap, null).addOnSuccessListener(succ -> {
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            Navigation.findNavController(view).navigate(R.id.nav_groceryItemUpdated);
                        }).addOnFailureListener(er->{
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getActivity(), ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    }

                }else{
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

            HashMap<String, Object> tempGroceryItem = new HashMap<>();

            tempGroceryItem.put("name", itemName.getText().toString());
            tempGroceryItem.put("quantity", Integer.parseInt(itemQuantity.getText().toString().isEmpty() ? "0" : itemQuantity.getText().toString()));
            tempGroceryItem.put("desc", itemDesc.getText().toString());
            tempGroceryItem.put("img", imageUri);
            tempGroceryItem.put("imageURL", imageURL);
            tempGroceryItem.put("key", itemKey);


            // to tell where to go next
            tempGroceryItem.put("PREV_PAGE", (String) "update_item");

            // Pass data to the update item
            Bundle result = new Bundle();
            result.putSerializable("GROCERY_HASHMAP", tempGroceryItem);
            getParentFragmentManager().setFragmentResult("TEMP_GROCERY_ITEM", result);
            Navigation.findNavController(view).navigate(R.id.nav_selectLocation_update);
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
                itemKey = (String) tempGroceryItem.get("key");

                if (tempGroceryItem.get("img")!=null){
                    imageUri = (Uri) tempGroceryItem.get("img");
                    imageView.setImageURI(imageUri);
                }else if(tempGroceryItem.get("imageURL")!= null){
                    loadImage((String) tempGroceryItem.get("imageURL"));
                }


                Log.e("Passed Data LS",tempGroceryItem.toString());
            }
        });


    }



    private void setItemData(String item_key) {
        this.itemKey = item_key;
        dao.getSingle(item_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groceryItem = snapshot.getValue(GroceryItem.class);
                itemName.setText(groceryItem.getName());
                itemQuantity.setText(""+groceryItem.getQuantity());
                itemDesc.setText(groceryItem.getDescription());

                // firebase image
                if(groceryItem.getImage() != null){
                    loadImage(groceryItem.getImage());
                }

                if(groceryItem.getLocationID() != null){
                    locationID = groceryItem.getLocationID();
                    locationName = groceryItem.getLocationName();
                    itemLocation.setText(locationName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadImage(String URL){
        imageURL = URL;
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(getContext());
        firebaseStorage = FirebaseStorage.getInstance(firebaseApp);
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(URL).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    private boolean validate(View v){
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_grocery_item, container, false);
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
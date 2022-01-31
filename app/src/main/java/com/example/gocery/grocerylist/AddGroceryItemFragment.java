package com.example.gocery.grocerylist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gocery.R;

import com.example.gocery.grocerylist.dao.DAOCurrentGroceryItem;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddGroceryItemFragment extends Fragment {


    final int REQUEST_IMAGE = 1;
    Uri imageUri;
    ImageView imageView;
    String fileUrl;

    FloatingActionButton btnSave;
    Button btnAttachImage;
    TextInputEditText itemName, itemQuantity, itemDesc;

    // database and storage
    DAOCurrentGroceryItem dao;
    StorageReference storageReference;
    ProgressDialog progressDialog;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        itemName = view.findViewById(R.id.tiet_itemName);
        itemQuantity = view.findViewById(R.id.tiet_itemQuantity);
        itemDesc = view.findViewById(R.id.tiet_itemDescription);
//        locationData = view.findViewById(R.id.et_locationData);

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
                                    fileName,
                                    null,
                                    null
                            );
                            groceryItem.setCreatedBy(creatorName);

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
                                null,
                                null,
                                null
                        );

                        groceryItem.setCreatedBy(creatorName);

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
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddGroceryItemFragment extends Fragment {


//    public AddGroceryItemFragment() {
//        // Required empty public constructor
//    }

    final int REQUEST_IMAGE = 1;
    Uri imageUri;
    ImageView imageView;
    String fileUrl;

    FloatingActionButton btnSave;
    Button btnAttachImage;
    TextInputEditText itemName, itemQuantity, itemDesc;
//    EditText imgPath, locationData;

    // database and storage
    DAOCurrentGroceryItem dao;
    StorageReference storageReference;
    ProgressDialog progressDialog;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        itemName = view.findViewById(R.id.tiet_itemName);
        itemQuantity = view.findViewById(R.id.tiet_itemQuantity);
        itemDesc = view.findViewById(R.id.tiet_itemDescription);
//        imgPath = view.findViewById(R.id.et_imagePath);
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


                String file = null;
                if(imageUri != null){
                    file =  uploadImage();
                }
                if(validate(v)){
                    // saving data to database
                    GroceryItem gi = new GroceryItem(
                            itemName.getText().toString(),
                            Integer.parseInt(itemQuantity.getText().toString()),
                            false, // set checkbox to false
                            itemDesc.getText().toString(), //nullable
                            file, // file path
                            null,
                            null
                    );
                    dao.add(gi).addOnSuccessListener(suc->{
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(er->{
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                    });



                    Navigation.findNavController(view).navigate(R.id.nav_groceryItemAdded);
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


    public String uploadImage(){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.UK);
        Date now = new Date();
        Log.e("IMAGE EXT", imageUri.toString());
        String fileName = "grocery_image/"+formatter.format(now);
        this.fileUrl = fileName;

        storageReference = FirebaseStorage.getInstance().getReference(fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(suc->{
            Toast.makeText(getActivity(), "upload success", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(er->{
            Toast.makeText(getActivity(), "upload failed", Toast.LENGTH_SHORT).show();
            this.fileUrl = null;
        });

        return this.fileUrl;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE && data != null && data.getData() != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }


    }
}
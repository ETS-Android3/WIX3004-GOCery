package com.example.gocery.manageprofiles;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gocery.R;
import com.example.gocery.manageprofiles.dao.DAOProfile;
import com.example.gocery.manageprofiles.model.UserProfile;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;


public class AddProfileFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    TextInputEditText ETUsername;
    CheckBox CBAdmin;
    CheckBox CBRep;
    SwitchMaterial SWLock;
    TextInputLayout TILPassword;
    TextInputEditText ETPassword;

    final int REQUEST_IMAGE = 999;
    Uri imageUri;
    ImageView IVProfileAdd;

    Button BTNSave;
    Button BTNCancel;
    Boolean isAdmin = false;
    Boolean isRep = false;

    StorageReference storageReference;

    public AddProfileFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ETUsername = view.findViewById(R.id.ETAddress);
        CBAdmin = view.findViewById(R.id.CBAdmin);
        CBRep = view.findViewById(R.id.CBRep);
        SWLock = view.findViewById(R.id.SWLock);
        ETPassword = view.findViewById(R.id.ETPassword);
        TILPassword = view.findViewById(R.id.TILPassword);
        BTNSave = view.findViewById(R.id.BTNSave);
        BTNCancel = view.findViewById(R.id.BTNDelete);
        IVProfileAdd = view.findViewById(R.id.IVProfileEdit);

        CBAdmin.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            isAdmin = isChecked;
        });

        CBRep.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            isRep = isChecked;
        });

        SWLock.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                TILPassword.setVisibility(view.VISIBLE);
                ETPassword.setVisibility(view.VISIBLE);
            }else{
                TILPassword.setVisibility(view.GONE);
                ETPassword.setVisibility(view.GONE);
            }
        });

        BTNCancel.setOnClickListener(v -> {
//            Navigation.findNavController(view).navigate(R.id.DestHousehold);
            Navigation.findNavController(view).navigate(R.id.DestManageHousehold2);
        });

        BTNSave.setOnClickListener(v -> {
            createProfile(v);
        });

        IVProfileAdd.setOnClickListener(v->{
            if(ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 43);
            }else{
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
    }

    public void createProfile(View view){
        String username = ETUsername.getText().toString();
        String password = ETPassword.getText().toString();

        if(TextUtils.isEmpty(username)){
            ETUsername.setError("Username cannot be empty");
            ETUsername.requestFocus();
        }else if(SWLock.isChecked() && TextUtils.isEmpty(password)) {
            ETPassword.setError("Pin must not be empty");
            ETPassword.requestFocus();
        }else{
            String userID = mAuth.getCurrentUser().getUid();
            String currentUser = "user_"+userID;
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String fileName = "member_profile/"+currentUser+"/CREATOR"+ts.getTime();
//            if(!SWLock.isChecked()){
//                password = null;
//            }

            if(imageUri != null){
                storageReference = FirebaseStorage.getInstance().getReference(fileName);
                storageReference.putFile(imageUri).addOnSuccessListener(suc -> {

                    DAOProfile daoProfile = new DAOProfile();
                    UserProfile userProfile = new UserProfile(username, fileName, password, isRep, isAdmin);
                    daoProfile.add(userProfile, userID).addOnSuccessListener(v -> {
                        Toast.makeText(getActivity(), "Profile added successfully!", Toast.LENGTH_SHORT).show();
//                    Navigation.findNavController(view).navigate(R.id.DestHousehold);
                        Navigation.findNavController(view).navigate(R.id.DestManageHousehold2);
                    }).addOnFailureListener(err -> {
                        Toast.makeText(getActivity(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                }).addOnFailureListener(er->{
                    Toast.makeText(getActivity(), "Upload failed image", Toast.LENGTH_SHORT).show();
                });
            }else{
                DAOProfile daoProfile = new DAOProfile();
                UserProfile userProfile = new UserProfile(username, "member_profile/default.png", password, isRep, isAdmin);
                daoProfile.add(userProfile, userID).addOnSuccessListener(v -> {
                    Toast.makeText(getActivity(), "Profile added successfully!", Toast.LENGTH_SHORT).show();
//                    Navigation.findNavController(view).navigate(R.id.DestHousehold);
                    Navigation.findNavController(view).navigate(R.id.DestManageHousehold2);
                }).addOnFailureListener(err -> {
                    Toast.makeText(getActivity(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_profile, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE && data != null && data.getData() != null){
            imageUri = data.getData();
            IVProfileAdd.setImageURI(imageUri);
        }
//        else{
//            imageUri = null;
//            IVProfileAdd.setImageURI(null);
//        }
    }
}
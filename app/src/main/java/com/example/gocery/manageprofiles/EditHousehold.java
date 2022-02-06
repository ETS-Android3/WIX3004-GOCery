package com.example.gocery.manageprofiles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gocery.R;
import com.example.gocery.manageprofiles.dao.DAOHousehold;
import com.example.gocery.manageprofiles.dao.DAOProfile;
import com.example.gocery.manageprofiles.model.Household;
import com.example.gocery.manageprofiles.model.UserProfile;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.sql.Timestamp;
import java.util.HashMap;


public class EditHousehold extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DAOHousehold daoHousehold =  new DAOHousehold();
    Household household;
    TextInputEditText ETAddress;
    Button BTNSaveAddress;
    String addressID;

    ProgressDialog progressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ETAddress = view.findViewById(R.id.ETAddress);
        BTNSaveAddress = view.findViewById(R.id.BTNSaveAddress);

        getParentFragmentManager().setFragmentResultListener("editAddress", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                ETAddress.setText(""+result.get("HOUSEHOLD_ADDRESS"));
                addressID = ""+result.get("HOUSEHOLD_ADDRESSID");
            }
        });
        BTNSaveAddress.setOnClickListener(v->{
            String address = ETAddress.getText().toString();
            if(TextUtils.isEmpty(address)){
                ETAddress.setError("Address cannot be empty");
            }else{
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Updating profile...");
                progressDialog.show();
                String userID = mAuth.getCurrentUser().getUid();

                HashMap<String, Object> hashMap = new HashMap<>();

                DAOHousehold daoHousehold = new DAOHousehold();

                hashMap.put("address", address);
                daoHousehold.update(addressID, hashMap).addOnSuccessListener(suc -> {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), "Household updated successfully!", Toast.LENGTH_SHORT).show();
//                Navigation.findNavController(view).navigate(R.id.DestHousehold);
                        Navigation.findNavController(view).navigate(R.id.DestManageHousehold2);
                    }).addOnFailureListener(err -> {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

            });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_household, container, false);
    }
}
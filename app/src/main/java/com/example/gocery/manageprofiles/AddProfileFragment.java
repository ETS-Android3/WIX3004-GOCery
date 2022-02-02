package com.example.gocery.manageprofiles;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.gocery.R;
import com.example.gocery.manageprofiles.dao.DAOProfile;
import com.example.gocery.manageprofiles.model.UserProfile;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicReference;


public class AddProfileFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    TextInputEditText ETUsername;
    CheckBox CBAdmin;
    CheckBox CBRep;
    SwitchMaterial SWLock;
    TextInputLayout TILPassword;
    TextInputEditText ETPassword;

    Button BTNSave;
    Button BTNCancel;
    Boolean isAdmin = false;
    Boolean isRep = false;


    public AddProfileFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        ETUsername = view.findViewById(R.id.ETUsername);
        CBAdmin = view.findViewById(R.id.CBAdmin);
        CBRep = view.findViewById(R.id.CBRep);
        SWLock = view.findViewById(R.id.SWLock);
        ETPassword = view.findViewById(R.id.ETPassword);
        TILPassword = view.findViewById(R.id.TILPassword);
        BTNSave = view.findViewById(R.id.BTNSave);
        BTNCancel = view.findViewById(R.id.BTNCancel);

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
            Navigation.findNavController(view).navigate(R.id.DestHousehold);
        });

        BTNSave.setOnClickListener(v -> {
            createProfile(v);
        });
    }

    public void createProfile(View view){
        String username = ETUsername.getText().toString();
        String password = ETPassword.getText().toString();
        System.out.println(isAdmin.toString());
        System.out.println(isRep.toString());
        if(TextUtils.isEmpty(username)){
            ETUsername.setError("Username cannot be empty");
        }else if(SWLock.isChecked() && TextUtils.isEmpty(password)) {
            ETPassword.setError("Password must not be empty");
        }else{
            String userID = mAuth.getCurrentUser().getUid();

            DAOProfile daoProfile = new DAOProfile();
            if(!SWLock.isChecked()){
                password = null;
            }

            UserProfile userProfile = new UserProfile(username, R.drawable.ic_baseline_account_circle_24, password, isRep, isAdmin);
            daoProfile.add(userProfile, userID).addOnSuccessListener(v -> {
                Toast.makeText(getActivity(), "Profile added successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.DestHousehold);
            }).addOnFailureListener(err -> {
                Toast.makeText(getActivity(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_profile, container, false);
    }
}
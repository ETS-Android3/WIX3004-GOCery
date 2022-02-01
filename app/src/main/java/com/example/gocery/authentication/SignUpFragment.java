package com.example.gocery.authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.gocery.R;
import com.example.gocery.manageprofiles.dao.DAOHousehold;
import com.example.gocery.manageprofiles.dao.DAOProfile;
import com.example.gocery.manageprofiles.model.Household;
import com.example.gocery.manageprofiles.model.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpFragment extends Fragment {

    TextInputEditText ETSignUpEmail;
    TextInputEditText ETSignUpPassword;
    TextInputEditText ETSignUpConfirm;
    TextInputEditText ETSignUpAddress;
    Button BTNSignUp;
    FirebaseAuth mAuth;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        ETSignUpEmail = view.findViewById(R.id.ETSignUpEmail);
        ETSignUpPassword = view.findViewById(R.id.ETSignUpPassword);
        ETSignUpConfirm = view.findViewById(R.id.ETSignUpConfirm);
        ETSignUpAddress = view.findViewById(R.id.ETSignUpAddress);

        mAuth = FirebaseAuth.getInstance();
        BTNSignUp = view.findViewById(R.id.BTNSignUp);
        BTNSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(view);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    public void createUser(View view){
        String email = ETSignUpEmail.getText().toString();
        String password = ETSignUpPassword.getText().toString();
        String confirm = ETSignUpConfirm.getText().toString();
        String address = ETSignUpAddress.getText().toString();
        boolean result = false;
        if(TextUtils.isEmpty(email)){
            ETSignUpEmail.setError("Email cannot be empty");
        }else if(TextUtils.isEmpty(password)){
            ETSignUpPassword.setError("Password cannot be empty");
        }else if(TextUtils.isEmpty(confirm)){
            ETSignUpConfirm.setError("Please reenter your password");
        }else if(!TextUtils.equals(password,confirm)){
            ETSignUpPassword.setError("Passwords do not match");
            ETSignUpConfirm.setError("Passwords do not match");
        }else if(TextUtils.isEmpty(address)){
            ETSignUpAddress.setError("Address cannot be empty");
        }else{
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "User registered successfully!", Toast.LENGTH_SHORT).show();
                        String userID = task.getResult().getUser().getUid();
                        createHousehold(userID, address);
                        createDefaultProfile(userID);
                        Navigation.findNavController(view).navigate(R.id.DestSignIn);

                    }else{
                        Toast.makeText(getActivity(), "Registration Error: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void createHousehold(String userID, String address){
        DAOHousehold daoHousehold = new DAOHousehold();
        Household household = new Household(address, userID);
        daoHousehold.add(household, userID).addOnSuccessListener(v -> {
            Toast.makeText(getActivity(), "Household added successfully!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(err -> {
            Toast.makeText(getActivity(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void createDefaultProfile(String userID){
        DAOProfile daoProfile = new DAOProfile();
        UserProfile userProfile = new UserProfile("User", R.drawable.ic_baseline_account_circle_24, null, false, false);
        daoProfile.add(userProfile, userID).addOnSuccessListener(v -> {
            Toast.makeText(getActivity(), "Profile added successfully!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(err -> {
            Toast.makeText(getActivity(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }
}
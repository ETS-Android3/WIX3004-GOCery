package com.example.gocery.authentication;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {
    TextInputEditText ETSignInEmail;
    TextInputEditText ETSignInPassword;
//    Button BTNSignIn;
    FirebaseAuth mAuth;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        ETSignInEmail = view.findViewById(R.id.ETSignInEmail);
        ETSignInPassword = view.findViewById(R.id.ETSignInPassword);
        Button BTNSignIn = view.findViewById(R.id.BTNSignIn);

        mAuth = FirebaseAuth.getInstance();

        BTNSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.DestSignUp);
//                Navigation.findNavController(view).navigate(R.id.DestSelectProfile);
                loginUser(view);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    private void loginUser(View view){
        String email = ETSignInEmail.getText().toString();
        String password = ETSignInPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            ETSignInEmail.setError("Email cannot be empty");
            ETSignInEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            ETSignInPassword.setError("Password cannot be empty");
            ETSignInPassword.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Login success!", Toast.LENGTH_SHORT).show();;
                        Navigation.findNavController(view).navigate(R.id.DestSelectProfile);
                    }else{
                        Toast.makeText(getActivity(), "Registration Error: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
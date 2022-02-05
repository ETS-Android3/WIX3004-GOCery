package com.example.gocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();


//        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);
//        NavController navController = host.getNavController();


        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);
        NavController navController = host.getNavController();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            navController.navigate(R.id.DestSelectProfile);
        }
        else{
            navController.navigate(R.id.DestLanding);
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
//        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);
//        NavController navController = host.getNavController();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if(user != null){
//            navController.navigate(R.id.DestSelectProfile);
//        }
//        else{
//            navController.navigate(R.id.DestLanding);
//        }
    }


}
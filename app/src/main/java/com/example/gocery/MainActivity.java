package com.example.gocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);

        NavController navController = host.getNavController();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try{
            Navigation.findNavController(this, R.id.NHFMain).navigate(item.getItemId());
            return true;
        }catch(Exception ex){
            return super.onOptionsItemSelected(item);
        }
    }
}
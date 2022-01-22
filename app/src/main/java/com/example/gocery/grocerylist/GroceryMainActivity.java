package com.example.gocery.grocerylist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.gocery.R;

public class GroceryMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_main);

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

        NavController navController = host.getNavController();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try{
            Navigation.findNavController(this, R.id.fragmentContainerView).navigate(item.getItemId());
            return true;
        }catch(Exception ex){
            return super.onOptionsItemSelected(item);
        }
    }
}
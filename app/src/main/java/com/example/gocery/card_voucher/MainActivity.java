package com.example.gocery.card_voucher;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.gocery.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try{
            Navigation.findNavController(this, R.id.fragment_container_view_tag).navigate(item.getItemId());
            return true;
        }catch(Exception ex){
            return super.onOptionsItemSelected(item);
        }
    }
}
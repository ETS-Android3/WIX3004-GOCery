package com.example.gocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TOOLBAR CONFIGURATION
        Toolbar toolbar = findViewById(R.id.TBMainAct);
        setSupportActionBar(toolbar);

        // TOP NAVIGATION MENU CONFIGURATION
        // In the onCreate callback method in MainActivity.java,
        // you also want to bind the NavHostFragment with the
        // NavController.
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);

        // NavController is an object that manages the app
        // navigation within the NavHost.
        NavController navController = host.getNavController();

        // Bind toolbar with navController
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // BOTTOM NAVIGATION MENU CONFIGURATION
        // Set up bottom navigation menu
        setupBottomNavMenu(navController);

//        // HAMBURGER/SIDEBAR CONFIGURATION
//        DrawerLayout drawerLayout = findViewById(R.id.DLMain);
//        // This code adds a drawer listener to open and close the
//        // drawer when the hamburger icon is clicked
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                setDrawerIndicatorEnabled(true);
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//            }
//        };
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//        setupSideNavMenu(navController);


//        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                if(getSupportFragmentManager().getBackStackEntryCount() == 0){
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    toggle.setDrawerIndicatorEnabled(true);
//                }else{
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    toggle.setDrawerIndicatorEnabled(false);
//
//                }
//            }
//        });

//        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(MainActivity.this, " Back Pressed ", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar,
//                            R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//                    drawerLayout.addDrawerListener(toggle);
//                    toggle.syncState();
//                }
//            }
//        });

    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.DLMain);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add options in bottom_menu.xml file to overflow menu
        getMenuInflater().inflate(R.menu.menu_bottom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // link the selected option to the
        // NavHostFragment in activity_main.xml which is connected to
        // the navigation graph.
        try {
            Navigation.findNavController(this, R.id.NHFMain).navigate(item.getItemId());
            return true;
        } catch (Exception ex) {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Override the onSupportNavigateUp() method in
        // MainActivity.java to enable the up action in the
        // toolbar
        return Navigation.findNavController(this, R.id.NHFMain).navigateUp();
    }

    // FOR BOTTOM NAVIGATION MENU
    private void setupBottomNavMenu(NavController navController) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    // FOR SIDEBAR NAVIGATION MENU
    private void setupSideNavMenu(NavController navController) {
        NavigationView sideNav = findViewById(R.id.sideNav);
        NavigationUI.setupWithNavController(sideNav, navController);
    }

//    // creating a variable for
//    // our Firebase Database.
//    FirebaseDatabase firebaseDatabase;
//
//    // creating a variable for our
//    // Database Reference for Firebase.
//    DatabaseReference databaseReference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        FloatingActionButton fab = (FloatingActionButton)
//                findViewById(R.id.fabAdd);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(),
//                        AddExpenseActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        // below line is used to get the instance
//        // of our Firebase database.
//        firebaseDatabase = FirebaseDatabase.getInstance("https://gocery-825ca-default-rtdb.asia-southeast1.firebasedatabase.app/");
//
//        // below line is used to get
//        // reference for our database.
//        databaseReference = firebaseDatabase.getReference("Data");
//
//        // calling method
//        // for getting data.
//        getdata();
//    }
//
//    private void getdata() {
//
//        // calling add value event listener method
//        // for getting the values from database.
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // this method is call to get the realtime
//                // updates in the data.
//                // this method is called when the data is
//                // changed in our Firebase console.
//                // below line is for getting the data from
//                // snapshot of our database.
//                String value = snapshot.getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // calling on cancelled method when we receive
//                // any error or we are not able to get the data.
//                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
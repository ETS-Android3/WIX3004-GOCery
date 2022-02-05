package com.example.gocery.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gocery.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SelectLocationFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView tv_LocationName, tv_locationLatLon;
    Place selectedLocation;

    FloatingActionButton checkedBtn;


    HashMap<String, Object> tempGroceryItem = new HashMap<>();


    public SelectLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("TEMP_GROCERY_ITEM", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                tempGroceryItem = (HashMap<String, Object>) result.get("GROCERY_HASHMAP");
                Log.e("Passed Data",tempGroceryItem.toString());
            }
        });

        Places.initialize(getActivity(), getString(R.string.google_maps_key));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        tv_LocationName = view.findViewById(R.id.tv_SelectedLocationName);
        tv_locationLatLon = view.findViewById(R.id.tv_SelectedLocationLatLong);
        tv_LocationName.setVisibility(View.GONE);
        tv_locationLatLon.setVisibility(View.GONE);


        ActivityResultLauncher<Intent> autoCompleteActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Place place = Autocomplete.getPlaceFromIntent(result.getData());
                            locationSelected(place);
                        }
                    }
                });

        Button BTNSetLocation = (Button) getView().findViewById(R.id.BTNSetLocation);
        BTNSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(getActivity());
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                autoCompleteActivityResultLauncher.launch(intent);
            }
        });


        checkedBtn = view.findViewById(R.id.fabtn_confirmSelection);
        checkedBtn.setOnClickListener(v -> {
            if(selectedLocation == null){
                Toast.makeText(getContext(), "Please Select A Location", Toast.LENGTH_SHORT).show();
            }else{
                String prev = (String) tempGroceryItem.get("PREV_PAGE");
//                Log.e("PREV", tempGroceryItem.toString());
                HashMap<String, Object> settItems = tempGroceryItem;
//                Log.e("tempGroceryItem", tempGroceryItem.toString());

                if (prev.equals("add_item")){
                    settItems.put("locationID", selectedLocation.getId());
                    settItems.put("locationName", selectedLocation.getName());
                    Log.e("sending Data", settItems.toString());
                    Bundle result = new Bundle();
                    result.putSerializable("GROCERY_HASHMAP", settItems);
                    getParentFragmentManager().setFragmentResult("SELECTED_LOCATION", result);
                    Navigation.findNavController(view).navigate(R.id.nav_LocationSelected_add);
                }else if(prev.equals("update_item")){
                    settItems.put("locationID", selectedLocation.getId());
                    settItems.put("locationName", selectedLocation.getName());
                    Log.e("sending Data", settItems.toString());
                    Bundle result = new Bundle();
                    result.putSerializable("GROCERY_HASHMAP", settItems);
                    getParentFragmentManager().setFragmentResult("SELECTED_LOCATION", result);
                    Navigation.findNavController(view).navigate(R.id.nav_LocationSelected_update);
                }
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_location, container, false);
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(SelectLocationFragment.this);
        return v;
    }


    private void locationSelected(Place place) {
        if (place != null) {
            LatLng latLng = place.getLatLng();
            // Update both the list of locations and also the list of markers on the map

            selectedLocation = place;

            Log.e("Palce", ""+place.getId()+"|"+place.getName());

            tv_LocationName.setText(place.getName());
            tv_locationLatLon.setText(place.getLatLng().toString());

            tv_LocationName.setVisibility(View.VISIBLE);
            tv_locationLatLon.setVisibility(View.VISIBLE);

            //Refresh the map
            mMap.clear();
            MarkerOptions marker = new MarkerOptions().position(latLng).title(place.getName());
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12));
        }else{
            selectedLocation = null;
            tv_LocationName.setVisibility(View.GONE);
            tv_locationLatLon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            return;
        }
    }




}
package com.example.gocery.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gocery.R;
import com.example.gocery.map.adapter.DirectionListAdapter;
import com.example.gocery.map.adapter.LocationListAdapter;
import com.example.gocery.map.object.Route;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SelectLocationFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;

    TextView tv_LocationName, tv_locationLatLon;



    Place selectedLocation;


    public SelectLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

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
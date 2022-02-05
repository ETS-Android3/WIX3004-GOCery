package com.example.gocery.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.gocery.R;
import com.example.gocery.map.adapter.DirectionListAdapter;
import com.example.gocery.map.object.Route;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    ArrayList<Place> placeArrayList = new ArrayList<Place>();
    ArrayList<Route> routeArrayList = new ArrayList<Route>();
    ArrayList<MarkerOptions> markerOptionsArrayList = new ArrayList<MarkerOptions>();
    DirectionListAdapter directionListAdapter;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

        Places.initialize(getActivity(), getString(R.string.google_maps_key));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        PlacesClient placesClient = Places.createClient(getActivity());

        getParentFragmentManager().setFragmentResultListener("locations", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // add each places into the arraylist
                ArrayList<String> placesArrayList = (ArrayList<String>) result.get("LOCATIONS");
                if(placesArrayList != null || placesArrayList.size() > 0){
                    for( String placeID: placesArrayList){
                        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeID, placeFields);
                        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                            Place place = response.getPlace();
                            addLocation(place);
                        });
                    }
                }
            }
        });

        ActivityResultLauncher<Intent> autoCompleteActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Place place = Autocomplete.getPlaceFromIntent(result.getData());
                            addLocation(place);
                        }
                    }
                });
        getLastLocation();

        ListView LVDistance = (ListView) getView().findViewById(R.id.LVDistance);
        directionListAdapter = new DirectionListAdapter(getActivity(), R.layout.direction_list, routeArrayList);
        LVDistance.setAdapter(directionListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        return v;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            return;
        }
    }

    private void addLocation(Place place) {
        if (place != null) {
            LatLng latLng = place.getLatLng();
            // Update both the list of locations and also the list of markers on the map
            placeArrayList.add(place);
            markerOptionsArrayList.add(new MarkerOptions().position(latLng).title(place.getName()));
            //Refresh the map
            refreshMap();
        }
    }

    public void removeLocation(@NonNull Place place){
        Toast.makeText(getActivity(), "Location removed" ,Toast.LENGTH_SHORT).show();
        LatLng latLng = place.getLatLng();
        //Search
        for (int i = 0; i < markerOptionsArrayList.size(); i++){
            if(markerOptionsArrayList.get(i).getPosition().equals(latLng)){
                markerOptionsArrayList.remove(i);
                refreshMap();
                return;
            }
        }
    }

    // Construct route from placeArrayList (array of Places)
    private void createRoute() {
        //Construct calls
        String url = getRequestUrl(placeArrayList);
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);
    }

    // Call this method to redraw the map anytime the data is updated.
    private void refreshMap(){
        mMap.clear();
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        if(!markerOptionsArrayList.isEmpty()){
            for (MarkerOptions markerOptions : markerOptionsArrayList){
                mMap.addMarker(markerOptions);
            }
            MarkerOptions markerOne = markerOptionsArrayList.get(markerOptionsArrayList.size() - 1);
        }
        //Make a new route if there's at least 1 location.
        if(markerOptionsArrayList.size() > 0){
            createRoute();
        }
    }

    // Method to prepare URL to get directions from Google Directions API
    private String getRequestUrl(ArrayList<Place> placeArrayList) {
        //Value of origin
        String str_org = "origin=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        //Value of destination
        String str_dest = "destination=place_id:" + placeArrayList.get(placeArrayList.size()-1).getId();
        //Value of in between stops
        String str_waypoints = "";
        if(placeArrayList.size() > 1){
            str_waypoints = "&waypoints=";
            for (int i = 0; i < placeArrayList.size() - 1; i++){
                str_waypoints += "place_id:" + placeArrayList.get(i).getId();
                if(i != placeArrayList.size() - 1){
                    str_waypoints += "|";
                }
            }
        }
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Add key
        String key = "key=" + getString(R.string.google_maps_key);
        //Build the full param
        String param = str_org +"&" + str_dest + str_waypoints + "&" +sensor+ "&" + mode + "&" + key;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    // Method to connect to the Directions API and get the directions
    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    // Fetch current location of user, not working right for some reason.
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    if (supportMapFragment != null) {
                        supportMapFragment.getMapAsync(MapFragment.this);
                    }
                }
            }
        });
    }

    // Async task to execute get response from Direction API.
    // Only specific for getting directions!
    public class TaskRequestDirections extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    // Async Task to parse all information from json received from TaskRequestDirections into data.
    // Only specific for getting directions!
    public class TaskParser extends AsyncTask<String, Void, List<Route>  > {

        @Override
        protected List<Route>  doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<Route>  routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<Route>  lists) {
            //Get list route and display it into the map
            ArrayList points = new ArrayList();

            PolylineOptions polylineOptions = new PolylineOptions();
            routeArrayList.clear();
            for (Route path : lists) {

                // current format: {list of locations} => {list of lat lon}

                for (HashMap<String, String> point : path.getCoordinates()) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat,lon));
                }
            }

            //Replace each destination with the proper one
            for(int i = 0; i < lists.size(); i++){
                Route route = lists.get(i);
                route.setDestination(placeArrayList.get(i).getName());
                routeArrayList.add(route);
            }

            directionListAdapter.notifyDataSetChanged();
            polylineOptions.addAll(points);
            polylineOptions.width(15);
            polylineOptions.color(Color.BLUE);
            polylineOptions.geodesic(true);

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getActivity(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
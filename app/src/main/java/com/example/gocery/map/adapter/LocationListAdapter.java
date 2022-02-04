package com.example.gocery.map.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gocery.R;
import com.example.gocery.map.MapFragment;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class LocationListAdapter extends ArrayAdapter<Place> {

    Context mContext;
    MapFragment mFragment;

    public LocationListAdapter(@NonNull Context context, int resource, @NonNull List<Place> objects, MapFragment fragment) {
        super(context, resource, objects);
        mContext = context;
        mFragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Grab the place's information
        String name = getItem(position).getName();
        String latLng = getItem(position).getLatLng().toString();
        String address = getItem(position).getAddress();

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_list, parent, false);
        }

        TextView TVName, TVAddress, TVLatLng;
        TVName = (TextView) convertView.findViewById(R.id.TVName);
        TVAddress = (TextView) convertView.findViewById(R.id.TVAddress);
        TVLatLng = (TextView) convertView.findViewById(R.id.TVLatLng);

        TVName.setText(name);
        TVAddress.setText(address);
        TVLatLng.setText(latLng);

        Button BTNDelete =(Button) convertView.findViewById(R.id.BTNDelete);
        BTNDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Place toRemove = getItem(position);
                remove(toRemove);
                mFragment.removeLocation(toRemove);
            }
        });
        return convertView;
    }

    @Override
    public void remove(@Nullable Place object) {
        super.remove(object);
    }
}

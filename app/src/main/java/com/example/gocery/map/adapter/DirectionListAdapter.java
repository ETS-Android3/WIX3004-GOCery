package com.example.gocery.map.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gocery.R;
import com.example.gocery.map.object.Route;

import java.util.List;

public class DirectionListAdapter extends ArrayAdapter<Route> {

    Context mContext;

    public DirectionListAdapter(@NonNull Context context, int resource, @NonNull List<Route> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Grab the place's information
        String distance = getItem(position).getDistance();
        String destination = getItem(position).getDestination();
        String duration = getItem(position).getTime();

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.direction_list, parent, false);
        }

        TextView TVDistance, TVDestination, TVDuration;
        TVDistance = (TextView) convertView.findViewById(R.id.TVDistance);
        TVDestination = (TextView) convertView.findViewById(R.id.TVDestination);
        TVDuration = (TextView) convertView.findViewById(R.id.TVDuration);

        TVDistance.setText(distance);
        TVDestination.setText(destination);
        TVDuration.setText(duration);

        return convertView;
    }

    @Override
    public void remove(@Nullable Route object) {
        super.remove(object);
    }
}

package com.example.gocery.manageprofiles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocery.R;
import com.example.gocery.manageprofiles.model.ProfileData;

import java.util.ArrayList;

public class ProfileDataAdapter extends RecyclerView.Adapter<ProfileDataAdapter.RecyclerViewHolder> {
    private ArrayList<ProfileData> profileDataArrayList;
    private Context mccontext;

    public ProfileDataAdapter(ArrayList<ProfileData> profileDataArrayList, Context mcontext){
        this.profileDataArrayList = profileDataArrayList;
        this.mccontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        //Inflates layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_card, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position){
        // Sets the data for TV and IV
        ProfileData profileData = profileDataArrayList.get(position);
        holder.TVProfile.setText(profileData.getUsername());
        holder.IVProfile.setImageResource(profileData.getImgId());
    }

    @Override
    public int getItemCount(){
        return profileDataArrayList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView TVProfile;
        private ImageView IVProfile;

        public RecyclerViewHolder(@NonNull View itemView){
            super(itemView);
            TVProfile = itemView.findViewById(R.id.TVProfile);
            IVProfile = itemView.findViewById(R.id.IVProfile);
        }
    }

}

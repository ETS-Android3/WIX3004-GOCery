package com.example.gocery.authentication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gocery.R;
import com.example.gocery.manageprofiles.model.ProfileData;
import com.example.gocery.manageprofiles.adapter.ProfileDataAdapter;

import java.util.ArrayList;

public class SelectProfileFragment extends Fragment {

    public SelectProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        //initializes the profile list recycler view
        RecyclerView ProfileList = view.findViewById(R.id.RVProfileList);
        ArrayList<ProfileData> profileDataArrayList = new ArrayList<>();
        // Adds users to the list
        profileDataArrayList.add(new ProfileData("User", R.drawable.ic_baseline_account_circle_24));
        profileDataArrayList.add(new ProfileData("Mother", R.drawable.ic_baseline_account_circle_24));

        // Adds data from the list to the Adapter class
        ProfileDataAdapter adapter = new ProfileDataAdapter(profileDataArrayList, this.getActivity());

        // Sets grid layout manager to implement grid view
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(),2); //2 = number of columns in grid view

        ProfileList.setLayoutManager(layoutManager);
        ProfileList.setAdapter(adapter);

        Button BTNTemp = view.findViewById(R.id.BTNTemp);
        BTNTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.DestSignIn);
                Navigation.findNavController(view).navigate(R.id.action_auth_to_home);
//                Navigation.findNavController(view).navigate(R.id.DestHome);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_profile, container, false);
    }
}
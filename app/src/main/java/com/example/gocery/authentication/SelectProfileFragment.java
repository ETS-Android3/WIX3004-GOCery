package com.example.gocery.authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gocery.R;
import com.example.gocery.manageprofiles.adapter.UserProfileAdapter;
import com.example.gocery.manageprofiles.dao.DAOProfile;
import com.example.gocery.manageprofiles.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectProfileFragment extends Fragment {
    FirebaseAuth mAuth;
    DAOProfile daoProfile = new DAOProfile();
    ListView profileList;
    UserProfileAdapter adapter;
    ArrayList<UserProfile> userProfileArrayList = new ArrayList<>();

    public SelectProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        mAuth = FirebaseAuth.getInstance();
        //initializes the profile list recycler view
//        RecyclerView ProfileList = view.findViewById(R.id.RVProfileList);
        profileList = view.findViewById(R.id.LVProfiles);

        adapter = new UserProfileAdapter(getContext(),userProfileArrayList);
        profileList.setAdapter(adapter);
        loadData();

        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserProfile userProfile = (UserProfile) adapter.getItem(position);
//                Navigation.findNavController(view).navigate(R.id.action_auth_to_home);
                Navigation.findNavController(view).navigate(R.id.action_to_household_temp);

            }
        });


        Button BTNSignOut = view.findViewById(R.id.BTNSignOut);
        BTNSignOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(getActivity(), "You have been signed out", Toast.LENGTH_SHORT).show();;
                Navigation.findNavController(view).navigate(R.id.DestLanding);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_profile, container, false);
    }

    public void loadData(){
        daoProfile.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    UserProfile userProfile = data.getValue(UserProfile.class);
                    userProfile.setKey(data.getKey());
                    userProfileArrayList.add(userProfile);
                }
                adapter.setUserProfileList(userProfileArrayList);
                adapter.notifyDataSetChanged();
                // Adds data from the list to the Adapter class
//                ProfileDataAdapter adapter = new ProfileDataAdapter(userProfileArrayList, getActivity());
//                UserProfileAdapter adapter = new UserProfileAdapter(getContext(),userProfileArrayList);

                // Sets grid layout manager to implement grid view
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2); //2 = number of columns in grid view

//                ProfileList.setLayoutManager(layoutManager);
//                ProfileList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
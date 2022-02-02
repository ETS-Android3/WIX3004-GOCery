package com.example.gocery.manageprofiles;

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
import android.widget.ListView;

import com.example.gocery.R;
import com.example.gocery.manageprofiles.adapter.ManageProfileAdapter;
import com.example.gocery.manageprofiles.dao.DAOProfile;
import com.example.gocery.manageprofiles.model.UserProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageHouseholdFragment extends Fragment {
    FirebaseAuth mAuth;
    DAOProfile daoProfile = new DAOProfile();
    ListView memberList;
    ManageProfileAdapter adapter;
    ArrayList<UserProfile> userProfileArrayList = new ArrayList<>();
    FloatingActionButton FABAddMember;

    public ManageHouseholdFragment() {
        //Empty constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        FABAddMember = view.findViewById(R.id.FABAddMember);
        mAuth = FirebaseAuth.getInstance();
        memberList = view.findViewById(R.id.LVMembers);
        adapter = new ManageProfileAdapter(getContext(), userProfileArrayList);
        memberList.setAdapter(adapter);
        loadData();

        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserProfile userProfile = (UserProfile) adapter.getItem(position);
                //Navigation.findNavController(view).navigate(R.id.action_auth_to_home);
                //Navigation.findNavController(view).navigate(R.id.action_to_household_temp);

            }
        });

        FABAddMember.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_manage_to_add);
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_household, container, false);
    }

    public void loadData(){
        daoProfile.get(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    UserProfile userProfile = data.getValue(UserProfile.class);
                    userProfileArrayList.add(userProfile);
                }
                adapter.setUserProfileList(userProfileArrayList);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
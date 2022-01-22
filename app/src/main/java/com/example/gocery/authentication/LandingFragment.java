package com.example.gocery.authentication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.gocery.R;

public class LandingFragment extends Fragment {

    public LandingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        Button BtnSignIn = view.findViewById(R.id.BTNLogin);
        View.OnClickListener OCLSignIn = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.DestSignIn);
                //Navigation.findNavController(view).navigate(R.id.action_auth_to_home);
            }
        };
        BtnSignIn.setOnClickListener(OCLSignIn);

        TextView TVSignUp = (TextView) view.findViewById(R.id.TVRegister_link);
        View.OnClickListener OCLSignUp = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Navigation.findNavController(view).navigate(R.id.DestSignUp);
            }
        };
        TVSignUp.setOnClickListener(OCLSignUp);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landing_fragement, container, false);
    }
}
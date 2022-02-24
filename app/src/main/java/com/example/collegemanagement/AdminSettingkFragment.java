package com.example.collegemanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AdminSettingkFragment extends Fragment {

    Button logout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup v = (ViewGroup)  inflater.inflate(R.layout.fragment_admin_settingk,container,false);

        logout = v.findViewById(R.id.adminlogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(),"You are Logged Out",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginDecisionActivity.class);
                startActivity(intent);
                getActivity().onBackPressed();

            }
        });
        return v;
    }
}
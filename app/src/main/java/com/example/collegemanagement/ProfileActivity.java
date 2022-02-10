package com.example.collegemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        profileToolbar=findViewById(R.id.profile_toolbar);
        profileToolbar.setTitle("Profile");
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);





        //for usr profile and name
        email = findViewById(R.id.usr_email);
        name = findViewById(R.id.name_usr);
        userProfileImage = findViewById(R.id.profile_image);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personFamilyName = acct.getFamilyName();
            String personGivenName = acct.getGivenName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
            email.setText(personEmail);
            name.setText(personGivenName);
            Glide.with(this).load(String.valueOf(personPhoto)).into(userProfileImage);
        }
    }
}
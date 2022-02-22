package com.example.collegemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.HashMap;
import java.util.Map;

public class LoginDecisionActivity extends AppCompatActivity {

    Button loginbtn, registerbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_decision);


        registerbtn = findViewById(R.id.Register_btn);
        loginbtn = findViewById(R.id.login_btn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginDecisionActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginDecisionActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}
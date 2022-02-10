package com.example.collegemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginDecisionActivity extends AppCompatActivity {

    Button loginbtn;
    LinearLayout registertxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_decision);

        loginbtn = findViewById(R.id.normal_login);
        registertxt = findViewById(R.id.goto_signup_activity);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginDecisionActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


        registertxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginDecisionActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });


    }
}
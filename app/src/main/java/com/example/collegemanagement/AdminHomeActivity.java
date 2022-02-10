package com.example.collegemanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class AdminHomeActivity extends AppCompatActivity {

    SmoothBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        replace(new HomeFragment());
        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i){
                    case 0:
                        replace(new UploadQuestionPaperFragment());
                        break;

                    case 1:
                        replace(new UploadVideoLecturesFragment());
                        break;

                    case 2:
                        replace(new UploadNotesFragment());
                        break;
                }
                return true;
            }
        });

    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.bottombarframe,fragment);
        transaction.commit();
    }
}
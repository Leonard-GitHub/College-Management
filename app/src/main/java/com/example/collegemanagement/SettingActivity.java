package com.example.collegemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.collegemanagement.adapters.AdapterSubjects;
import com.example.collegemanagement.filters.FilterSubjects;
import com.example.collegemanagement.models.ModelSubject;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    Toolbar settingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settingToolbar=findViewById(R.id.setting_toolbar);
        settingToolbar.setTitle("Setting");
        setSupportActionBar(settingToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public static class UrAdapterSubjects extends RecyclerView.Adapter<AdapterSubjects.HolderSubjects> implements Filterable {

        private Context context;
        public ArrayList<ModelSubject> subjectArrayList, filterlist;

        private FilterSubjects filter;


        @Override
        public Filter getFilter() {
            return null;
        }

        @NonNull
        @Override
        public AdapterSubjects.HolderSubjects onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterSubjects.HolderSubjects holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
package com.example.collegemanagement;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegemanagement.adapters.AdapterSubjects;
import com.example.collegemanagement.filters.FilterSubjects;
import com.example.collegemanagement.models.ModelSubject;

import java.util.ArrayList;

public class UrAdapterSubjects extends RecyclerView.Adapter<AdapterSubjects.HolderSubjects> implements Filterable {

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

package com.example.collegemanagement.filters;

import android.widget.Filter;

import com.example.collegemanagement.adapters.AdapterSubjects;
import com.example.collegemanagement.models.ModelSubject;

import java.util.ArrayList;

public class FilterSubjects extends Filter {

    ArrayList<ModelSubject> filterlist;

    AdapterSubjects adapterSubjects;


    public FilterSubjects(ArrayList<ModelSubject> filterlist, AdapterSubjects adapterSubjects) {
        this.filterlist = filterlist;
        this.adapterSubjects = adapterSubjects;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results  = new FilterResults();
        if(constraint !=null && constraint.length()>0){

            //change to upper case, or lower to avoid case sentence
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelSubject> filteredModels = new ArrayList<>();
            for (int i=0; i<filterlist.size(); i++){
                if(filterlist.get(i).getSubject().toUpperCase().contains(constraint)){
                    filteredModels.add(filterlist.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;


        }else{
            results.count =filterlist.size();
            results.values = filterlist;

        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterSubjects.subjectArrayList = (ArrayList<ModelSubject>) results.values;
        adapterSubjects.notifyDataSetChanged();
    }
}

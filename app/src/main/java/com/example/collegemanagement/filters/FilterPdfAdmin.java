package com.example.collegemanagement.filters;

import android.widget.Filter;

import com.example.collegemanagement.adapters.AdapterPdfAdmin;
import com.example.collegemanagement.models.Modelpdf;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {

    ArrayList<Modelpdf> filterlist;

    AdapterPdfAdmin adapterPdfAdmin;


    public FilterPdfAdmin(ArrayList<Modelpdf> filterlist, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterlist = filterlist;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results  = new FilterResults();
        if(constraint !=null && constraint.length()>0){

            //change to upper case, or lower to avoid case sentence
            constraint = constraint.toString().toUpperCase();
            ArrayList<Modelpdf> filteredModels = new ArrayList<>();
            for (int i=0; i<filterlist.size(); i++){
                if(filterlist.get(i).getTitle().toUpperCase().contains(constraint)){
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
        adapterPdfAdmin.pdfArrayList = (ArrayList<Modelpdf>) results.values;
        adapterPdfAdmin.notifyDataSetChanged();
    }
}

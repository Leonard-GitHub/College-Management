package com.example.collegemanagement.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegemanagement.filters.FilterSubjects;
import com.example.collegemanagement.PdfListAdminActivity;
import com.example.collegemanagement.models.ModelSubject;
import com.example.collegemanagement.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterSubjects extends RecyclerView.Adapter<AdapterSubjects.HolderSubjects> implements Filterable {

    private Context context;
    public ArrayList<ModelSubject> subjectArrayList, filterlist;

    private FilterSubjects filter;

    public AdapterSubjects(Context context, ArrayList<ModelSubject> subjectArrayList) {
        this.context = context;
        this.subjectArrayList = subjectArrayList;
        this.filterlist = subjectArrayList;
    }

    @NonNull
    @Override
    public HolderSubjects onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_subjects,null);
        return new HolderSubjects(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSubjects holder, int position) {

        //get data
        ModelSubject model = subjectArrayList.get(position);
        String id = model.getId();
        String subject = model.getSubject();
        String imageurl = model.getImageurl();
        String  uid = model.getUid();
        long timestamp = model.getTimestamp();


        //set data
        holder.subjectTV.setText(subject);
        Glide.with(context)
                .load(model.getImageurl())
                .centerCrop()
                .placeholder(R.drawable.add_image)
                .into(holder.subjectIV);
        holder.subjectIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,""+subject, Toast.LENGTH_LONG).show();


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this Subject?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();
                                deleteCategory(model, holder);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PdfListAdminActivity.class);
                intent.putExtra("subjectId",id);
                intent.putExtra("subjectTitle",subject);
                context.startActivity(intent);
            }
        });

    }

    private void deleteCategory(ModelSubject model, HolderSubjects holder) {
        String id = model.getId();
        String subject = model.getSubject();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Subjects");
        ref.child(subject)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterSubjects(filterlist, this);
        }
        return filter;
    }


    class HolderSubjects extends RecyclerView.ViewHolder{

        TextView subjectTV;
        ImageButton subjectIB;
        ImageView subjectIV;

        public HolderSubjects(@NonNull View itemView) {
            super(itemView);

            //inti ui views
            subjectTV = itemView.findViewById(R.id.subjectTV);
            subjectIV = itemView.findViewById(R.id.subjectIV);
            subjectIB = itemView.findViewById(R.id.subjectIB);

        }
    }

}

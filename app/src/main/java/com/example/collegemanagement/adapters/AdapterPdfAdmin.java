package com.example.collegemanagement.adapters;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegemanagement.MyApplication;
import com.example.collegemanagement.databinding.ActivityRowPdfAdminBinding;
import com.example.collegemanagement.models.Modelpdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>{

    private Context context;
    private ArrayList<Modelpdf> pdfArrayList;
    private ActivityRowPdfAdminBinding binding;

    public AdapterPdfAdmin(Context context, ArrayList<Modelpdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }


    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=ActivityRowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        Modelpdf model= pdfArrayList.get(position);
        String title=model.getTitle();
        String description=model.getDescription();
        long timeStamp=model.getTimeStamp();

        String formattedDate= MyApplication.formatTimeStamp(timeStamp);

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        loadSubject(model, holder);
        loadPdfFromUrl(model, holder);
        loadPdfSize(model, holder);

    }

    private void loadPdfSize(Modelpdf model, HolderPdfAdmin holder) {
        String pdfUrl= model.getUrl();
        StorageReference ref= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();
                        double kb=bytes/1024;
                        double mb=kb/1024;

                        if (mb>=1){
                            holder.sizeTV.setText(String.format("%.2f", mb)+"MB");
                        }
                        else if(kb>=1){
                            holder.sizeTV.setText(String.format("%.2f", kb)+"KB");
                        }
                        else{
                            holder.sizeTV.setText(String.format("%.2f",bytes)+"bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void loadPdfFromUrl(Modelpdf model, HolderPdfAdmin holder) {
        String pdfUrl=model.getUrl();
        StorageReference ref= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)

    }

    private void loadSubject(Modelpdf model, HolderPdfAdmin holder) {
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    class HolderPdfAdmin extends RecyclerView.ViewHolder{

        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv, subjectTV, sizeTV, dateTv;
        ImageButton moreBtn;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);
            pdfView=binding.pdfView;
            progressBar=binding.progressBar;
            titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
            subjectTV=binding.subjectTV;
            sizeTV=binding.sizeTV;
            dateTv=binding.dateTv;
            moreBtn=binding.moreBtn;

        }
    }
}

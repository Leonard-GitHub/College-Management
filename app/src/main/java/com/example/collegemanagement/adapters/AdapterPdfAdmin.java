package com.example.collegemanagement.adapters;

import static com.example.collegemanagement.Constants.MAX_BYTES_PDF;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegemanagement.MyApplication;
import com.example.collegemanagement.databinding.ActivityRowPdfAdminBinding;
import com.example.collegemanagement.filters.FilterPdfAdmin;
import com.example.collegemanagement.models.Modelpdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    private Context context;
    public ArrayList<Modelpdf> pdfArrayList, filterList;
    private ActivityRowPdfAdminBinding binding;
    private FilterPdfAdmin filter;
    private static final String TAG="PDF_ADAPTER_TAG";

    public AdapterPdfAdmin(Context context, ArrayList<Modelpdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
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
                        Log.d(TAG, "onSuccess: "+model.getTitle()+" "+bytes);
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
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });


    }

    private void loadPdfFromUrl(Modelpdf model, HolderPdfAdmin holder) {
        String pdfUrl=model.getUrl();
        StorageReference ref= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: "+model.getTitle()+"Successfully files received");

                        holder.pdfView.fromBytes(bytes)
                                .pages(0)
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onError: "+t.getMessage());
                                    }
                                }).onPageError(new OnPageErrorListener() {
                            @Override
                            public void onPageError(int page, Throwable t) {
                                holder.progressBar.setVisibility(View.INVISIBLE);
                                Log.d(TAG, "onPageError: "+t.getMessage());
                            }
                        })
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                holder.progressBar.setVisibility(View.INVISIBLE);
                                Log.d(TAG, "loadComplete: Load complete");
                            }
                        })
                                .load();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onFailure: failed to get files due to:"+e.getMessage());

                    }
                });

    }

    private void loadSubject(Modelpdf model, HolderPdfAdmin holder) {
        String subjectId= model.getSubjectId();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Subjects");
        ref.child(subjectId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String subject=""+snapshot.child("subject").getValue();
                        holder.subjectTV.setText(subject);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterPdfAdmin(filterList, this);

        }
        return filter;
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

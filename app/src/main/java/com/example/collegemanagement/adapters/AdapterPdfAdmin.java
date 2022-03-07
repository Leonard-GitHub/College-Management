package com.example.collegemanagement.adapters;

import static com.example.collegemanagement.Constants.MAX_BYTES_PDF;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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
    private ProgressDialog progressDialog;

    public AdapterPdfAdmin(Context context, ArrayList<Modelpdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

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

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model, holder);
            }


        });

    }

    private void moreOptionsDialog(Modelpdf model, HolderPdfAdmin holder) {
        String[] options={"Delete"};
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Choose")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if(which==0){

                        }
                        else if(which==1){
                            deleteBook(model, builder);
                        }
                    }
                }).show();
    }

    private void deleteBook(Modelpdf model, AlertDialog.Builder builder) {
        String bookId= model.getId();
        String bookUrl= model.getUrl();
        String bookTitle= model.getTitle();

        Log.d(TAG, "deleteBook: Deleting.....");
        progressDialog.setMessage("Deleteing  "+bookTitle+"....");
        progressDialog.show();

        Log.d(TAG, "deleteBook: Deleting from storage");
        StorageReference storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Deleted from Storage");
                        Log.d(TAG, "onSuccess: Now deleting from Database");
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Deleted from DB too");
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Failed to delete from Database due to"+e.getMessage());
                                progressDialog.dismiss();
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to delete from storage due to "+e.getMessage());
                progressDialog.dismiss();
            }
        });

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

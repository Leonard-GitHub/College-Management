package com.example.collegemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.collegemanagement.databinding.ActivityPdfAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {

    private ActivityPdfAddBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<String> subjectTitleArrayList, subjectIDArrayList;
    private static final int PDF_PICK_CODE = 1000;
    private static final String TAG = "ADD_PDF_TAG";
    private Uri pdfUri=null;

    TextView categoryTV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        loadPdfCategories();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        categoryTV = findViewById(R.id.categoryTv);




        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
                
            }
        });

        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subjectPickDialog();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

    }

    private String title="", description="";
    private void validateData() {

        Log.d(TAG, "validateData: Validating Data");

        title=binding.titleEt.getText().toString().trim();
        description=binding.descriptionEt.getText().toString().trim();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "Enter Title.......", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter Description.....", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(selectedSubjectTitle)){
            Toast.makeText(this, "Enter Subject.....", Toast.LENGTH_SHORT).show();
        }
        else if(pdfUri==null){
            Toast.makeText(this, "Pick PDF", Toast.LENGTH_SHORT).show();
        }
        else{
            uploadPdfToStorage();
        }
    }

    private void uploadPdfToStorage() {
        Log.d(TAG, "uploadPdfToStorage: Uploading file to firebase");

        progressDialog.setMessage("Uploading Pdf");
        progressDialog.show();

        long timeStamp = System.currentTimeMillis();

        String filePathAndName= "Pdfs/"+timeStamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(PdfAddActivity.this, "Pdf Uploaded", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: Getting Pdf Url");
                        Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedPdfUrl = ""+uriTask.getResult();

                        uploadPdfInfoToDb(uploadedPdfUrl, timeStamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to Upload due to "+e.getMessage());
                        Toast.makeText(PdfAddActivity.this, "Pdf upload failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadPdfInfoToDb(String uploadedPdfUrl, long timeStamp) {
        Log.d(TAG, "uploadPdfInfoToDb: Uploading data to storage");
        progressDialog.setMessage("Uploading Pdf Info");
        String uid = firebaseAuth.getUid();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+uid);
        hashMap.put("id", ""+timeStamp);
        hashMap.put("title", ""+title);
        hashMap.put("Description", ""+description);
        hashMap.put("SubjectId",""+selectedSubjectId);
        hashMap.put("Url", ""+uploadedPdfUrl);
        hashMap.put("Time Stamp", timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Subjects/"+selectedSubjectTitle+"/"+"Question Paper");
        ref.child(""+title)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Sucessfully Uploaded");
                        Toast.makeText(PdfAddActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: Failed to Upload due to "+e.getMessage());
                Toast.makeText(PdfAddActivity.this, "Failed to Upload due to "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: loading pdf categories");
        subjectTitleArrayList =new ArrayList<>();
        subjectIDArrayList= new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Subjects");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectTitleArrayList.clear();
                subjectIDArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String subjectId=""+ds.child("id").getValue();
                    String subjectTitle=""+ds.child("subject").getValue();

                    subjectIDArrayList.add(subjectId);
                    subjectTitleArrayList.add(subjectTitle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private String selectedSubjectId, selectedSubjectTitle;
    private void subjectPickDialog() {
        Log.d(TAG, "subjectPickDialog: showing category pick dialog");
        String[] subjectArray=new String[subjectTitleArrayList.size()];
        for(int i = 0; i< subjectTitleArrayList.size(); i++){
            subjectArray[i]= subjectTitleArrayList.get(i);
        }
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Pick Subject")
                .setItems(subjectArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        selectedSubjectTitle = subjectTitleArrayList.get(which);
                        selectedSubjectId = subjectIDArrayList.get(which);
                        binding.categoryTv.setText(selectedSubjectTitle);

                        Log.d(TAG, "onClick: Selected Subject "+ selectedSubjectId+ " "+selectedSubjectTitle);
                    }
                }).show();
    }


    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select pdf"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==PDF_PICK_CODE){
                Log.d(TAG, "onActivityResult: PDF Picked");
                pdfUri=data.getData();
                Log.d(TAG, "onActivityResult: Uri: "+pdfUri);
            }
            else{
                Log.d(TAG, "onActivityResult: pdf picking cancelled");
                Toast.makeText(this, "Cancelled Picking PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
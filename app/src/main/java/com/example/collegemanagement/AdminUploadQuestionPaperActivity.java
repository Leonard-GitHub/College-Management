package com.example.collegemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import io.grpc.Context;

public class AdminUploadQuestionPaperActivity extends AppCompatActivity {

    //firebase auth
    private FirebaseAuth firebaseAuth;


    ProgressDialog progressDialog;
    Button submitbtn;
    EditText subjectEt;
    private String sujbect = "";
    ImageView imageView;
    private StorageReference reference = FirebaseStorage.getInstance().getReference("Images/SubjectViewImages");
    private Uri imageUri;
    private String imageUrlgot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upload_question_paper);

        submitbtn = findViewById(R.id.submitBtn);
        subjectEt = findViewById(R.id.subjectEt);
        imageView = findViewById(R.id.imageview);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);



        //select image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });



        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK && data!=null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void validateData() {
        sujbect = subjectEt.getText().toString().trim();
        if(imageUri == null){
            Toast.makeText(AdminUploadQuestionPaperActivity.this, "Please  Select Image", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(sujbect)){
            Toast.makeText(this, "Please Enter Subjects",Toast.LENGTH_SHORT).show();   //before addingvalidate data //getdata
        }
        else {
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri uri) {

        StorageReference fileref = reference.child(System.currentTimeMillis()+sujbect+"."+ getFileExtension(uri));
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                         imageUrlgot = uri.toString();
                        Toast.makeText(AdminUploadQuestionPaperActivity.this, "Uploaded Successfully",Toast.LENGTH_SHORT).show();
                        addSubjectFirebase();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressDialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AdminUploadQuestionPaperActivity.this, "Uploading Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }





    private void addSubjectFirebase() {
        //show Progresss
        progressDialog.setMessage("Adding Subject");
        progressDialog.show();

        //get timestamp
        long timestamp = System.currentTimeMillis();


        //setup info to add in Firebase dh

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+timestamp);
        hashMap.put("timestamp", timestamp);
        hashMap.put("Subject",""+sujbect);
        hashMap.put("imageurl", imageUrlgot);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        //add to firebase db..........Database Root>Categories>categoryID>categray info
        DatabaseReference ref   = FirebaseDatabase.getInstance().getReference("Subjects");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Subject added...",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AdminUploadQuestionPaperActivity.this, ""+e.getMessage() , Toast.LENGTH_SHORT).show();
            }


        });









    }
}
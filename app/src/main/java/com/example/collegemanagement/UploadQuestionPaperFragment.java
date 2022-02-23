package com.example.collegemanagement;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UploadQuestionPaperFragment extends Fragment {

    //for dropdown list
    String[] items =  {"JAVA","DCN","MALP","SE","CA"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;


    String subject;
    EditText editText;
    ImageView imageView;
    Button button;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (ViewGroup)  inflater.inflate(R.layout.fragment_upload_question_paper,container,false);


        autoCompleteTxt = view.findViewById(R.id.auto_complete_txt);
        editText = view.findViewById(R.id.edit_pdf_name);
        imageView = view.findViewById(R.id.uploadpdf);
        button = view.findViewById(R.id.uploadbtn);



        //dropdownmenu
        adapterItems = new ArrayAdapter<String>(getContext(),R.layout.dropdown_list_qp,items);
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                subject = item;
                Toast.makeText(getContext(),"Item: "+subject,Toast.LENGTH_SHORT).show();
            }
        });





        storageReference = FirebaseStorage.getInstance().getReference("QUESTION PAPER");
        databaseReference = FirebaseDatabase.getInstance().getReference("Question Papers/"+subject);

        button.setEnabled(false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });


        return view;
    }


    private void selectPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT PDF FILE"), 12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==12 & resultCode==RESULT_OK && data.getData()!=null){
            button.setEnabled(true);
            editText.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    uploadPDFfileFirebase(data.getData());
                }
            });
        }
    }

    private void uploadPDFfileFirebase(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("File is Uploading...");
        progressDialog.show();

        StorageReference reference = storageReference.child("QuestionPapers"+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri uri = uriTask.getResult();
                        putPDF putPDF = new putPDF(editText.getText().toString(), uri.toString());
                        databaseReference.child("12"+databaseReference.push().getKey()).setValue(putPDF);
                       Toast.makeText(getContext(),"File Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                double progress=(100.0*snapshot.getBytesTransferred())/ snapshot.getTotalByteCount();
                progressDialog.setMessage("File Uploading.."+(int) progress+"%");

            }
        });
    }
}
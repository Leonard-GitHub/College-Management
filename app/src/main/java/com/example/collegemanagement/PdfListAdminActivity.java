package com.example.collegemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.example.collegemanagement.adapters.AdapterPdfAdmin;
import com.example.collegemanagement.databinding.ActivityPdfListAdminBinding;
import com.example.collegemanagement.databinding.ActivityRowPdfAdminBinding;
import com.example.collegemanagement.models.Modelpdf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PdfListAdminActivity extends AppCompatActivity {

    private ActivityPdfListAdminBinding binding;
    public ArrayList<Modelpdf> pdfArrayList;
    private AdapterPdfAdmin adapterPdfAdmin;

    private String subjectId, subjectTitle;
    private static final String TAG = "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list_admin);
        binding= ActivityPdfListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent=getIntent();
        subjectId=intent.getStringExtra("subjectId");
        subjectTitle=intent.getStringExtra("subjectTitle");

        binding.subtitleTv.setText(subjectTitle);

        loadPdfList();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    adapterPdfAdmin.getFilter().filter(s);

                }catch (Exception e){
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void loadPdfList() {
        pdfArrayList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Subject/Question Paper");
        ref.orderByChild("subjectId").equalTo(subjectId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Modelpdf model=ds.getValue(Modelpdf.class);
                            pdfArrayList.add(model);
                            Log.d(TAG, "onDataChange: "+model.getId()+" "+ model.getTitle());
                        }
                        adapterPdfAdmin=new AdapterPdfAdmin(PdfListAdminActivity.this, pdfArrayList);
                        binding.bookRv.setAdapter(adapterPdfAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}
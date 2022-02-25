package com.example.collegemanagement;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


public class AdminViewQuestionPaperFragment extends Fragment {


    private ArrayList<ModelSubject> subjectArrayList;
    private AdapterSubjects adapterSubjects;
    RecyclerView subjectsRv;
    EditText searchbar;
    Button addSubjectBtn;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(R.layout.fragment_admin_view_question_paper, container, false);


        addSubjectBtn = view.findViewById(R.id.addSubjectBtn);
        subjectsRv = view.findViewById(R.id.subjectsRv);
        searchbar = view.findViewById(R.id.searchEt);


        addSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminUploadQuestionPaperActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);            }
        });


        loadSubjects();


        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterSubjects.getFilter().filter(s);
                }catch (Exception e){

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });






    return view;
    }

    private void loadSubjects() {

        subjectArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Subjects");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelSubject model = ds.getValue(ModelSubject.class);
                    subjectArrayList.add(model);
                }
                adapterSubjects = new AdapterSubjects(getContext(), subjectArrayList);
                subjectsRv.setAdapter(adapterSubjects);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
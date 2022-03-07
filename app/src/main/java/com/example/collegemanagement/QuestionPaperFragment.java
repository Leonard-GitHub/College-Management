package com.example.collegemanagement;

import static com.google.firebase.FirebaseApp.getInstance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.collegemanagement.adapters.AdapterSubjects;
import com.example.collegemanagement.models.ModelSubject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class QuestionPaperFragment extends Fragment {

    private ArrayList<ModelSubject> subjectArrayList;
    private AdapterSubjects adapterSubjects;
    RecyclerView subjectsRv;
    EditText searchbar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup v = (ViewGroup)  inflater.inflate(R.layout.fragment_question_paper,container,false);



        subjectsRv = v.findViewById(R.id.ursubjectsRv);
        searchbar = v.findViewById(R.id.ursearchEt);





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

        return v;
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
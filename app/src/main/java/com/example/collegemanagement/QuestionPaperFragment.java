package com.example.collegemanagement;

import static com.google.firebase.FirebaseApp.getInstance;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class QuestionPaperFragment extends Fragment {

    ListView listView;
    DatabaseReference databaseReference;
    List<putPDF> uploadedPDF;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup v = (ViewGroup)  inflater.inflate(R.layout.fragment_question_paper,container,false);


        listView = v.findViewById(R.id.listView);
        uploadedPDF = new ArrayList<>();

        retrivePdfFiles();

        return v;
    }

    private void retrivePdfFiles() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Question Papers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    uploadedPDF.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    putPDF putPDF=ds.getValue(com.example.collegemanagement.putPDF.class);
                    uploadedPDF.add(putPDF);
                }
                String[] uploadsName = new String[uploadedPDF.size()];
                for(int i=0; i<uploadsName.length;i++){
                    uploadsName[i]=uploadedPDF.get(i).getName();
                }

                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, uploadsName){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView)view
                                .findViewById(android.R.id.text1);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(20);
                        return view;
                    }
                };
                listView.setAdapter(arrayAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
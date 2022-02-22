package com.example.collegemanagement;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginRegisterBottomSheetDialog extends BottomSheetDialogFragment {

    EditText fullname, email, password;
    Button signup;
    Boolean valid=true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    AnimatedCheckBox isTeacher, isStudent;
    int checkstutea;


    private BottomSheetListener mlistner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_register_bottom_sheet_layout, container, false);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullname = v.findViewById(R.id.edit_text_name);
        email = v.findViewById(R.id.edit_text_emailaddress);
        password = v.findViewById(R.id.edit_text_password);
        signup = v.findViewById(R.id.normal_login);

        isTeacher = v.findViewById(R.id.teacher_chk);
        isStudent = v.findViewById(R.id.student_chk);


        isTeacher = v.findViewById(R.id.teacher_chk);
        isStudent = v.findViewById(R.id.student_chk);


        isTeacher.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked) {
                if(checkBox.isChecked()){
                    isStudent.setChecked(false);
                    checkstutea = 1;
                }
            }
        });

        isStudent.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked) {
                if(checkBox.isChecked()){
                    isTeacher.setChecked(false);
                    checkstutea = 0;
                }
            }
        });





        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!(isStudent.isChecked() || isTeacher.isChecked())){
                    Toast.makeText(getContext(), "Select Account Type", Toast.LENGTH_SHORT).show();
                    return;
                }



                mlistner.
                        onButtonClicked(fullname.getText().toString(),email.getText().toString(),password.getText().toString(),valid, checkstutea);
                dismiss();
            }
        });
        return v;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }



    public interface BottomSheetListener{
        void onButtonClicked(String fullna, String emai, String pd, Boolean vali, int stutea);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mlistner = (BottomSheetListener) context;
        } catch (ClassCastException e ) {
            throw  new ClassCastException(context.toString()+"Must implement BottomSheetListener");
        }
    }


    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }
        return valid;
    }
}

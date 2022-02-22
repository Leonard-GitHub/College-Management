package com.example.collegemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText fullname, email, password;
    TransitionButton signup;
    Boolean valid=true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    AnimatedCheckBox isTeacher, isStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullname = findViewById(R.id.edit_text_name);
        email = findViewById(R.id.edit_text_emailaddress);
        password = findViewById(R.id.edit_text_password);
        signup = findViewById(R.id.normal_login);

        isTeacher = findViewById(R.id.teacher_chk);
        isStudent = findViewById(R.id.student_chk);



        isTeacher.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {




            @Override
            public void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked) {
                if(checkBox.isChecked()){
                    isStudent.setChecked(false);
                }
            }
        });

        isStudent.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked) {
                if(checkBox.isChecked()){
                    isTeacher.setChecked(false);
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the loading animation when the user tap the button
                signup.startAnimation();

                checkField(fullname);
                checkField(email);
                checkField(password);

                if(!(isStudent.isChecked() || isTeacher.isChecked())){
                    Toast.makeText(RegisterActivity.this, "Select Account Type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(valid){
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Yay!!! Account Created", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("FullName",fullname.getText().toString());
                            userInfo.put("Email",email.getText().toString());

                            if(isTeacher.isChecked()){
                                userInfo.put("isTeacher", "1");
                            }
                            if(isStudent.isChecked()){
                                userInfo.put("isStudent", "1");
                            }

                            df.set(userInfo);

                            if(isTeacher.isChecked()){
                                signup.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                    @Override
                                    public void onAnimationStopEnd() {
                                        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                            }
                            if(isStudent.isChecked()){
                                signup.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                    @Override
                                    public void onAnimationStopEnd() {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            signup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            signup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
            valid = false;
        }else {
            valid = true;
        }
        return valid;
    }
}
package com.example.collegemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText fullname, email, password;
    Button signup;
    Boolean valid=true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(fullname);
                checkField(email);
                checkField(password);

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

                            userInfo.put("isAdmin","0");
                            df.set(userInfo);

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                            e.getMessage();
                        }
                    });
                }
            }
        });
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
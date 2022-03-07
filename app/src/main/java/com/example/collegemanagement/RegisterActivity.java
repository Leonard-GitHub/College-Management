package com.example.collegemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    EditText Username, email, password;
    TransitionButton signup;
    Boolean valid=true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    AnimatedCheckBox isTeacher, isStudent;


    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef;

    private StorageTask mUploadTask;






    MediaPlayer mpfail, mpsuccess, mpcheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        mStorageRef = FirebaseStorage.getInstance().getReference("User Profile");



        mImageView = findViewById(R.id.setting_profile_image);
        Username = findViewById(R.id.edit_text_name);
        email = findViewById(R.id.edit_text_emailaddress);
        password = findViewById(R.id.edit_text_password);
        signup = findViewById(R.id.normal_signup);

        isTeacher = findViewById(R.id.teacher_chk);
        isStudent = findViewById(R.id.student_chk);

        mpfail = MediaPlayer.create(this, R.raw.failed_login);
        mpsuccess = MediaPlayer.create(this, R.raw.success_login_signup);
        mpcheckbox = MediaPlayer.create(this,R.raw.checkbox);


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });



        isTeacher.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked) {
                if(checkBox.isChecked()){
                    isStudent.setChecked(false);
                    mpcheckbox.start();
                }
            }
        });

        isStudent.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(AnimatedCheckBox checkBox, boolean isChecked) {
                if(checkBox.isChecked()){
                    isTeacher.setChecked(false);
                    mpcheckbox.start();
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the loading animation when the user tap the button
                signup.startAnimation();



                checkField(Username);
                checkField(email);
                checkField(password);

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(RegisterActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }




            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    private void uploadFile() {

        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;

                    // We will get the url of our image using uritask
                    final Uri downloadUri = uriTask.getResult();
                    if (uriTask.isSuccessful()) {
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
                                    userInfo.put("Username",Username.getText().toString());
                                    userInfo.put("Email",email.getText().toString());
                                    userInfo.put("UserProfile",downloadUri.toString());

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
                                                mpsuccess.start();
                                                finish();

                                            }
                                        });
                                    }
                                    if(isStudent.isChecked()){
                                        signup.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                            @Override
                                            public void onAnimationStopEnd() {
                                                mpsuccess.start();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivity(intent);
                                                mpsuccess.start();
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
                                    mpfail.start();
                                }
                            });
                        }




                    }else{

                        Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            });


    }}

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            signup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
            mpfail.start();
            valid = false;
        }else {
            valid = true;
        }
        return valid;
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Glide.with(getApplicationContext())
                    .load(mImageUri)
                    .centerCrop()
                    .into(mImageView);
        }
    }


}
package com.example.collegemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    private TransitionButton loginBtn;
    boolean valid = true;

    MediaPlayer mpfail, mpsuccess, mpcheckbox;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    //timmer
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    boolean timerStarted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.edit_text_emailaddress);
        password = findViewById(R.id.edit_text_password);
        loginBtn = findViewById(R.id.loginbutton);

        mpfail = MediaPlayer.create(this, R.raw.failed_login);
        mpsuccess = MediaPlayer.create(this, R.raw.success_login_signup);
        mpcheckbox = MediaPlayer.create(this,R.raw.checkbox);



        timer = new Timer();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Start the loading animation when the user tap the button
                loginBtn.startAnimation();
                checkField(email);
                checkField(password);

                if(valid){
                    fAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            mpsuccess.start();
                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                            checkAccessLevel(authResult.getUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loginBtn.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                            mpfail.start();


                        }
                    });
                }

            }
        });
}

    private void checkAccessLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess: " + documentSnapshot.getData());

                if(documentSnapshot.getString("isTeacher") != null){
                    loginBtn.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                        @Override
                        public void onAnimationStopEnd() {
                            Intent intent = new Intent(getBaseContext(), AdminHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                if(documentSnapshot.getString("isStudent") != null) {
                    loginBtn.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                        @Override
                        public void onAnimationStopEnd() {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();

                        }
                    });
                    finish();
                }
                }
        });
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            loginBtn.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
            mpfail.start();
            valid = false;
        }else {
            valid = true;
        }
        return valid;
    }



    public void startStopTapped(View view)
    {
        if(timerStarted == false)
        {
            timerStarted = true;

            startTimer();
        }
        else
        {
            timerStarted = false;

            timerTask.cancel();
        }
    }


    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }


    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }

}
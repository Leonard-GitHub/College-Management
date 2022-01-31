package com.example.collegemanagement;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{

    private Button btnSignOut;
    private GoogleSignInClient mGoogleSignInClient;

    TextView email, name;
    CircleImageView userProfileImage;



    private static final int POS_HOME = 0;
    private static final int POS_NOTES = 1;
    private static final int POS_VIDEO_LECTURES = 2;
    private static final int POS_QUESTION_PAPER = 3;
    private static final int POS_TO_DO_LIST = 4;
    private static final int POS_ABOUT = 6;
    private static final int POS_LOGOUT = 7;

    Toolbar toolbar;



    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(225)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcons= loadScreenIcons();
        screenTitles = loadScreenTitles();
        



        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_NOTES),
                createItemFor(POS_VIDEO_LECTURES),
                createItemFor(POS_QUESTION_PAPER),
                createItemFor(POS_TO_DO_LIST),
                new SpaceItem(48),
                createItemFor(POS_ABOUT),
                createItemFor(POS_LOGOUT)
        ));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_HOME);



        //for usr profile and name
        userProfileImage = findViewById(R.id.image_usr);
        email = findViewById(R.id.email_address);
        name = findViewById(R.id.name_usr);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personFamilyName = acct.getFamilyName();
            String personGivenName = acct.getGivenName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
            email.setText(personEmail);
            name.setText(personGivenName);
            Glide.with(this).load(String.valueOf(personPhoto)).into(userProfileImage);
        }


    }

    private DrawerItem createItemFor(int position){
        return new SimpleItem(screenIcons[position],screenTitles[position])
                .withIconTint(color(R.color.lightttBlue))
                .withTextTint(color(R.color.black))
                .withSelectedIconTint(color(R.color.lightttBlue))
                .withSelectedTextTint(color(R.color.lightttBlue));
    }


    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }







    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for(int i =0; i<ta.length(); i++){
            int id  = ta.getResourceId(i,0);
            if(id!=0){
                icons[i] = ContextCompat.getDrawable(this,id);
            }
        }

        ta.recycle();
        return icons;
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    public void setSupportActionBar(Toolbar toolbar) {
    }





    @Override
    public void onItemSelected(int position) {

        FragmentTransaction transition  = getSupportFragmentManager().beginTransaction();

        if (position == POS_HOME) {
            toolbar.setTitle("HOME");
            HomeFragment homeFragment = new HomeFragment();
            transition.replace(R.id.container, homeFragment);
        }


        else if (position == POS_NOTES) {
            toolbar.setTitle("NOTES");
            NotesFragment notesFragment =  new NotesFragment();
            transition.replace(R.id.container, notesFragment);
        }


        else if (position == POS_VIDEO_LECTURES) {
            toolbar.setTitle("Video Lectures");
           VideoLecturesFragment videoLecturesFragment = new VideoLecturesFragment();
            transition.replace(R.id.container, videoLecturesFragment);
        }


        else if (position == POS_QUESTION_PAPER) {
            toolbar.setTitle("Question Papers");
            QuestionPaperFragment questionPaperFragment = new QuestionPaperFragment();
            transition.replace(R.id.container, questionPaperFragment);
        }


        else if (position == POS_TO_DO_LIST) {
            toolbar.setTitle("To-Do List");
            TodoListFragment todoListFragment = new TodoListFragment();
            transition.replace(R.id.container, todoListFragment);
        }

        else if (position == POS_ABOUT) {
            toolbar.setTitle("About");
            AboutUsFragment aboutUsFragment = new AboutUsFragment();
            transition.replace(R.id.container, aboutUsFragment);
        }

        else if (position == POS_LOGOUT) {
            LogoutFragment logoutFragment = new LogoutFragment();
            transition.replace(R.id.container, logoutFragment);
            finish();
        }


        slidingRootNav.closeMenu();
        transition.addToBackStack(null);
        transition.commit();


    }
}
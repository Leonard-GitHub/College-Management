package com.example.collegemanagement;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{

    private Button btnSignOut;
    private GoogleSignInClient mGoogleSignInClient;


    private static final int POS_CLOSE=0;
    private static final int POS_HOME = 1;
    private static final int POS_NOTES = 2;
    private static final int POS_VIDEO_LECTURES = 3;
    private static final int POS_QUESTION_PAPER = 4;
    private static final int POS_TO_DO_LIST = 5;
    private static final int POS_ABOUT = 7;
    private static final int POS_LOGOUT = 8;


    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
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
                createItemFor(POS_CLOSE),
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
            HomeFragment homeFragment = new HomeFragment();
            transition.replace(R.id.container, homeFragment);
        }


        else if (position == POS_NOTES) {
            NotesFragment notesFragment =  new NotesFragment();
            transition.replace(R.id.container, notesFragment);
        }


        else if (position == POS_VIDEO_LECTURES) {
           VideoLecturesFragment videoLecturesFragment = new VideoLecturesFragment();
            transition.replace(R.id.container, videoLecturesFragment);
        }


        else if (position == POS_QUESTION_PAPER) {
            QuestionPaperFragment questionPaperFragment = new QuestionPaperFragment();
            transition.replace(R.id.container, questionPaperFragment);
        }


        else if (position == POS_TO_DO_LIST) {
            TodoListFragment todoListFragment = new TodoListFragment();
            transition.replace(R.id.container, todoListFragment);
        }

        else if (position == POS_ABOUT) {
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
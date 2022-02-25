package com.example.collegemanagement;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yashovardhan99.timeit.Stopwatch;

import java.sql.Time;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    Stopwatch stopwatch;
    TextView textTime;
    String time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout=findViewById(R.id.drawer);
        toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView=findViewById(R.id.nav_view);
        loadFragment(new HomeFragment());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                Fragment fragment=null;
                switch (id)
                {
                    case R.id.home:
                        fragment=new HomeFragment();
                        loadFragment(fragment);

                        break;
                    case R.id.notes:
                        fragment=new NotesFragment();
                        loadFragment(fragment);

                        break;
                    case R.id.video_lectures:
                        fragment=new VideoLecturesFragment();
                        loadFragment(fragment);

                        break;
                    case R.id.question_papers:
                        fragment=new QuestionPaperFragment();
                        loadFragment(fragment);

                        break;
                    case R.id.setting:
                        Intent i = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(i);

                        break;
                    case R.id.logout:
                        fragment=new LogoutFragment();
                        loadFragment(fragment);

                        stopwatch.pause();
                        stopwatch.setTextView(textTime);
                        time = textTime.getText().toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("meassage",time);
                        // set Fragmentclass Arguments
                        fragment.setArguments(bundle);

                        break;
                    case R.id.about:
                        fragment=new AboutFragment();
                        loadFragment(fragment);


                        break;
                        

                    default:
                        return true;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //for usr profile and name
        View hView = navigationView.getHeaderView(0);
        TextView email = (TextView) hView.findViewById(R.id.main_email);
        TextView name = (TextView) hView.findViewById(R.id.main_name);
        CircleImageView userProfileImage = (CircleImageView) hView.findViewById(R.id.image_usr);



    }


    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);


    }



    //for toolbar options


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.toolBar_profile_icon);
        View view = MenuItemCompat.getActionView(menuItem);
        CircleImageView toolbarProfile = view.findViewById(R.id.toolBar_profile_pic);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            Uri personPhoto = acct.getPhotoUrl();
            Glide.with(this).load(String.valueOf(personPhoto)).into(toolbarProfile);
        }
        toolbarProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(i);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
    }
}
package com.supreme.supremedevs.schoolnotification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.supreme.supremedevs.schoolnotification.Fragments.Home;
import com.supreme.supremedevs.schoolnotification.Fragments.profile;
import com.supreme.supremedevs.schoolnotification.Login.SetupActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle mActionBarDrawerToggle;
    private AppBarConfiguration mAppBarConfiguration;
    private Uri mainImageURI = null;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final CircleImageView profile_image1=headerView.findViewById(R.id.pimage);
        final TextView navUsername = (TextView) headerView.findViewById(R.id.username);
        final TextView email = (TextView) headerView.findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        if (name==null || image==null ) {
                               startActivity(new Intent(getApplicationContext(), SetupActivity.class));
                        }
                        else {
                        navUsername.setText(name);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String profemail = user.getEmail();
                        email.setText(profemail);
                        profile_image1.setImageResource(R.drawable.background);
                  mainImageURI = Uri.parse(image);

                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.launcher);

                            Glide.with(MainActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profile_image1);
                    }  }
                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(MainActivity.this, "Firestore Load Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }

            }
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mActionBarDrawerToggle.syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mActionBarDrawerToggle.syncState();

            }
        };

        mActionBarDrawerToggle.syncState();
        drawer.addDrawerListener(mActionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_home:
                        Home home = new Home();
                        goToFragment(home);
                        drawer.closeDrawers();

                        break;

                    case R.id.nav_profile:
                        profile profile = new profile();
                        goToFragment(profile);
                        drawer.closeDrawers();
                        break;



                }

                return false;
            }
        });


    }
    private void goToFragment(Fragment selectedFragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, selectedFragment)
                .commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

           

            case R.id.action_logout:



                break;

            case R.id.action_profile:

                  startActivity(new Intent(getApplicationContext(), SetupActivity .class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

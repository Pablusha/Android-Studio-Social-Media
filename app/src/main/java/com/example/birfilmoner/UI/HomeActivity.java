package com.example.birfilmoner.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.birfilmoner.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private String photoUrl;
    private CircleImageView userProfileImage;
    private TextView txtUsername;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initialize Views
        navigationView = findViewById(R.id.ac_home_nav_view);
        View headView = navigationView.getHeaderView(0);
        userProfileImage = headView.findViewById(R.id.ac_home_userImage);
        txtUsername = headView.findViewById(R.id.ac_home_username);

        //Toolbar
        toolbar = findViewById(R.id.ac_home_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        verifyUserInformations();

    }

    //Handle Menu Item Click
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                Toast.makeText(HomeActivity.this, "Profil butonuna tıklandı.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(HomeActivity.this, "Ayarlar butonuna tıklandı.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_cikis:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("Hesabınızdan çıkış yapmak istediğinize emin misiniz?");
                builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("hatirla","false");
                        editor.apply();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });
                builder.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Hesabınızdan çıkış yapmak istediğinize emin misiniz?");
            builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    firebaseAuth.signOut();
                    SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("hatirla","false");
                    editor.apply();
                    finish();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }
            });
            builder.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    //NavigationView Retrieve User Profile Image
    private void retrieveProfileImage() {
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("User").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && snapshot.hasChild("image")) {
                            String retrieveImage = snapshot.child("image").getValue().toString();

                            photoUrl = retrieveImage;
                            Picasso.get().load(retrieveImage).into(userProfileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //NavigationView Retrieve User Name & Surname
    private void retrieveUserName() {
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("User").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && snapshot.hasChild("ad_soyad")) {
                            String username = snapshot.child("ad_soyad").getValue().toString();
                            txtUsername.setText(username);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //If user save his profile informations already, this method is gonna run.
    private void verifyUserInformations() {
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("User").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child("image").exists())) {
                    retrieveProfileImage();
                    retrieveUserName();
                } else {
                    sendUserToProfileInformationsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendUserToProfileInformationsActivity() {
        startActivity(new Intent(getApplicationContext(), ProfileInformationsActivity.class));
    }

}
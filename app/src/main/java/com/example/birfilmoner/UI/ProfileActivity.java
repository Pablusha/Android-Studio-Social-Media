package com.example.birfilmoner.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.birfilmoner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView imgProfilePic;
    private TextView txtUsername;
    private String photoUrl;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        retrieveProfileImageAndUsername();

    }

    private void initializeViews() {
        imgProfilePic = findViewById(R.id.ac_profile_imgProfile);
        txtUsername = findViewById(R.id.ac_profile_username);
    }

    private void retrieveProfileImageAndUsername() {
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("User").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && snapshot.hasChild("image") && snapshot.hasChild("ad_soyad")) {
                            String retrieveImage = snapshot.child("image").getValue().toString();
                            String username = snapshot.child("ad_soyad").getValue().toString();

                            photoUrl = retrieveImage;
                            Picasso.get().load(retrieveImage).into(imgProfilePic);
                            txtUsername.setText(username);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void sendUserToProfiliDuzenle(View view) {
        startActivity(new Intent(ProfileActivity.this,ProfilDuzenleActivity.class));
    }

    public void backToHome(View view) {
        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
    }
}
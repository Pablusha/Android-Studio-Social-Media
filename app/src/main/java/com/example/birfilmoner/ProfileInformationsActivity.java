package com.example.birfilmoner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileInformationsActivity extends AppCompatActivity {

    private EditText edtAdSoyad;
    private Button btnKayit;
    private Spinner spnCinsiyet;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private CircleImageView userProfileImage;
    private static final int galleryPick = 1;
    private StorageReference profileImageRef;
    private String photoURL;
    private String currentUserId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_informations);

        edtAdSoyad = findViewById(R.id.ac_second_register_edt_ad_soyad);
        btnKayit = findViewById(R.id.ac_second_Register_btnKayit);
        userProfileImage = findViewById(R.id.ac_second_register_profile);
        //Spinner
        spnCinsiyet = findViewById(R.id.ac_second_register_cinsiyet);

        ArrayAdapter<String> cinsiyetAdapter = new ArrayAdapter<String>(ProfileInformationsActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.cinsiyet));
        cinsiyetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCinsiyet.setAdapter(cinsiyetAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        firebaseAuth = FirebaseAuth.getInstance();
        profileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,galleryPick);
            }
        });

        btnKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kayitOl();
            }
        });

    }

    private void kayitOl() {
        String ad_soyad = edtAdSoyad.getText().toString();
        String cinsiyet = spnCinsiyet.getSelectedItem().toString();

        if (TextUtils.isEmpty(ad_soyad)) {
            Toast.makeText(ProfileInformationsActivity.this,"Lütfen adınzı ve soyadınızı giriniz.",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(cinsiyet)) {
            Toast.makeText(ProfileInformationsActivity.this,"Lütfen cinsiyetinizi seçiniz.",Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            HashMap<String,Object> profileMap = new HashMap<>();
            profileMap.put("image",photoURL);
            profileMap.put("ad_soyad",ad_soyad);
            databaseReference.child("User").child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileInformationsActivity.this,"Profiliniz güncellendi.",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(ProfileInformationsActivity.this,"Hata:" + message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                StorageReference filePath = profileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();

                                        databaseReference.child("User").child(currentUserId).child("image")
                                                .setValue(downloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ProfileInformationsActivity.this,"Profil fotoğrafı başarıyla yüklendi.",Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            String message = task.getException().toString();
                                                            Toast.makeText(ProfileInformationsActivity.this,"Error:" + message,Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        });
            }
        }

    }


}
package com.example.birfilmoner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private TextView intentKayit;
    private EditText edtEmail,edtSifre;
    private Button btnGiris;
    FirebaseAuth firebaseAuth;
    private CheckBox chkBeniHatirla;
    DatabaseReference databaseReference;
    private FirebaseUser user;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        btnGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {
        String email = edtEmail.getText().toString();
        String sifre = edtSifre.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this,"Bir email adresi giriniz.",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(sifre)) {
            Toast.makeText(LoginActivity.this,"Şifrenizi girmelisiniz.",Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email,sifre)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Başarıyla giriş yaptınız.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        } else {
                            emailCheck();
                        }
                    }
                });


    }

    private void emailCheck() {
        firebaseAuth.fetchSignInMethodsForEmail(edtEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !task.getResult().getSignInMethods().isEmpty();

                        if (check == true) {
                            Toast.makeText(LoginActivity.this,"E-posta adresi veya şifre yanlış.",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this,"E-posta adresi sistemimizde kayıtlı değil lütfen kayıt olunuz.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeViews() {
        edtEmail = findViewById(R.id.ac_login_edtEmail);
        edtSifre = findViewById(R.id.ac_login_edtPassword);
        btnGiris = findViewById(R.id.ac_login_btnGiris);
        chkBeniHatirla = findViewById(R.id.ac_login_chkHatirla);
    }

    public void setIntentKayit(View view) {
        intentKayit = findViewById(R.id.ac_login_txtKayit);
        intentKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }
}
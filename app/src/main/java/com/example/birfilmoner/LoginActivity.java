package com.example.birfilmoner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private TextView intentKayit,txtSifremiUnuttum;
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

        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox = preferences.getString("hatirla","");
        if (checkbox.equals("true")) {
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        } else if (checkbox.equals("false")) {

        }

        btnGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        chkBeniHatirla.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("hatirla","true");
                    editor.apply();
                } else if (!compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("hatirla","false");
                    editor.apply();
                }
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

    public void sifremiUnuttum(View view) {
        txtSifremiUnuttum = findViewById(R.id.ac_login_txtSifremiUnuttum);
        txtSifremiUnuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Şifremi Unuttum");
                passwordResetDialog.setMessage("Lütfen kayıtlı hesabınızın email adresini giriniz.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = resetMail.getText().toString();

                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(LoginActivity.this,"Şifrenizi sıfırlamak için hesabınızın email adresini girmeniz gerekir.",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this,"Şifrenizi sıfırlamak için gereken bağlantı email adresinize gönderilmiştir."
                                            ,Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this,"Error!!" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });

                passwordResetDialog.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                passwordResetDialog.create().show();
            }
        });

    }
}
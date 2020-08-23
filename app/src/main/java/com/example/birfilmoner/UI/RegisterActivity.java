package com.example.birfilmoner.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.birfilmoner.Model.User;
import com.example.birfilmoner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextView intentGiris;
    private EditText edtEmail,edtSifre,edtSifreOnay;
    private Button btnKayit;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeViews();

        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();
        kayitOl();

    }

    private void kayitOl() {
        btnKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = edtEmail.getText().toString();
                final String sifre = edtSifre.getText().toString();
                String sifreOnay = edtSifreOnay.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this,"Email kısmı boş bırakılamaz.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sifre)) {
                    Toast.makeText(RegisterActivity.this,"Bir şifre girmelisiniz.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sifreOnay)) {
                    Toast.makeText(RegisterActivity.this,"Lütfen şifrenizi onaylayınız.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sifre.length() < 6) {
                    Toast.makeText(RegisterActivity.this,"Şifreniz en az 6 karakter olmalıdır.",Toast.LENGTH_LONG).show();
                    return;
                }

                if (!sifre.equals(sifreOnay)) {
                    Toast.makeText(RegisterActivity.this,"Şifreleriniz uyuşmuyor.",Toast.LENGTH_SHORT).show();
                    return;
                }

                emailCheck();
                firebaseAuth.createUserWithEmailAndPassword(email,sifre)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(email,sifre);
                                    FirebaseDatabase.getInstance().getReference("User")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(RegisterActivity.this,"Kayıt olma işlemi başarılı.",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }

                                    });
                                }
                            }
                        });

            }
        });

    }

    private void initializeViews() {
        //EditText
        edtEmail = findViewById(R.id.ac_register_edtEmail);
        edtSifre = findViewById(R.id.ac_register_edtPassword);
        edtSifreOnay = findViewById(R.id.ac_register_edtSifreOnay);
        //Button
        btnKayit = findViewById(R.id.ac_register_btnKayit);
    }

    public void setIntentKayit(View view) {
        intentGiris = findViewById(R.id.ac_register_txtKayit);
        intentGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }

    //İf user has already registered then this method is gonna run
    private boolean emailCheck() {
        firebaseAuth.fetchSignInMethodsForEmail(edtEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !task.getResult().getSignInMethods().isEmpty();

                        if (check == true) {
                            Toast.makeText(RegisterActivity.this,"E-postaya ait bir hesap zaten kayıtlı.",Toast.LENGTH_LONG).show();
                        }

                    }
                });
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

}
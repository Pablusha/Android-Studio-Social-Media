package com.example.birfilmoner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private TextView intentGiris;
    private EditText edtEmail,edtSifre,edtSifreOnay;
    private Button btnKayit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeViews();
        kayitOl();

    }

    private void kayitOl() {
        btnKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String sifre = edtSifre.getText().toString();
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

                if (!sifre.equals(sifreOnay)) {
                    Toast.makeText(RegisterActivity.this,"Şifreleriniz uyuşmuyor.",Toast.LENGTH_SHORT).show();
                    return;
                }

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

}
package com.jojo.metroapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jojo.metroapp.R;

import static com.jojo.metroapp.utils.utils.toast;

public class LoginActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setDialog();

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // UI Component
        final EditText edtEmail = findViewById(R.id.edtEmail);
        final EditText edtPassword = findViewById(R.id.edtPassword);
        FrameLayout btnMasuk = findViewById(R.id.btnMasuk);
        TextView tvToSignUp = findViewById(R.id.tvToSignUp);

        // Set On Click Listener
        tvToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (editTextIsNotEmpty(email, password)) {
                    signIn(email, password);
                } else {
                    if (email.isEmpty()) {
                        toast(getApplicationContext(), "Mohon isi email");
                    } else if (password.isEmpty()) {
                        toast(getApplicationContext(), "Mohon isi password");
                    } else if (password.length() < 6) {
                        toast(getApplicationContext(), "Password harus lebih dari 6 karakter");
                    }
                }
            }
        });
    }

    private boolean editTextIsNotEmpty(String email, String password) {
        return email != null && !email.isEmpty() && password != null && !password.isEmpty() && password.length() >= 6;
    }

    private void setDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon tunggu");
        progressDialog.setMessage("Sedang memasukkan akun");
    }

    private void signIn(String email, String password) {
        progressDialog.show();
        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser() != null) {
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        } else {
                            progressDialog.dismiss();
                            toast(getApplicationContext(), "Login gagal!");
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}

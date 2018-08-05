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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.jojo.metroapp.R;

import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_INFORMATION;
import static com.jojo.metroapp.utils.utils.setUserAccountInformationMap;
import static com.jojo.metroapp.utils.utils.toast;

public class SignUpActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setDialog();

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        firebaseFirestore.setFirestoreSettings(firebaseFirestoreSettings);

        // UI Component
        final EditText edtUsername = findViewById(R.id.edtUsername);
        final EditText edtEmail = findViewById(R.id.edtEmail);
        final EditText edtPassword = findViewById(R.id.edtPassword);
        FrameLayout btnDaftar = findViewById(R.id.btnDaftar);
        TextView tvToLogin = findViewById(R.id.tvToLogin);

        // Set On Click Listener
        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (editTextIsNotEmpty(username, email, password)) {
                    signUp(username, email, password);
                } else {
                    if (username.isEmpty()) {
                        toast(getApplicationContext(), "Mohon isi username");
                    } else if (email.isEmpty()) {
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

    private boolean editTextIsNotEmpty(String username, String email, String password) {
        return username != null && !username.isEmpty() && email != null && !email.isEmpty() && password != null && !password.isEmpty() && password.length() >= 6;
    }

    private void setDialog() {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon tunggu");
        progressDialog.setMessage("Sedang mendaftarkan akun");
    }

    private void signUp(final String username, final String email, final String password) {
        progressDialog.show();
        if (firebaseAuth.getCurrentUser() == null) {
            firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (firebaseAuth.getCurrentUser() != null) {
                                    firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION).document(email).set(setUserAccountInformationMap(username, email, password, firebaseAuth.getCurrentUser().getUid()))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    toast(getApplicationContext(), "Daftar akun gagal!");
                                                }
                                            });
                                }
                            } else {
                                progressDialog.dismiss();
                                toast(getApplicationContext(), "Daftar akun gagal!");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            toast(getApplicationContext(), "Daftar akun gagal!");
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

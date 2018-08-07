package com.jojo.metroapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jojo.metroapp.R;

import java.io.IOException;
import java.util.Objects;

import static com.jojo.metroapp.config.config.BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_URI_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_INFORMATION;
import static com.jojo.metroapp.config.config.RC_PICK_IMAGE;
import static com.jojo.metroapp.utils.utils.setUserAccountInformationMap;
import static com.jojo.metroapp.utils.utils.toast;

public class SignUpActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;
    private Uri imageProfilePath;
    private ImageView imageProfile;

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
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // UI Component
        final EditText edtUsername = findViewById(R.id.edtUsername);
        final EditText edtEmail = findViewById(R.id.edtEmail);
        final EditText edtPassword = findViewById(R.id.edtPassword);
        FrameLayout btnDaftar = findViewById(R.id.btnDaftar);
        TextView tvToLogin = findViewById(R.id.tvToLogin);
        imageProfile = findViewById(R.id.imageProfile_activitySignUp);
        AppCompatButton btnEditImageProfile = findViewById(R.id.btnEditImageProfile_activitySignUp);

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
                    uploadImageToDatabase(username, email, password);
                } else {
                    if (username.isEmpty()) {
                        toast(getApplicationContext(), "Mohon isi nama lengkap");
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

        btnEditImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambilGambar();
            }
        });
    }

    private void ambilGambar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Ambil Gambar"), RC_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageProfilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageProfilePath);
                imageProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                toast(getApplicationContext(), "Terjadi kesalahan saat mengambil gambar");
            }
        }
    }

    private boolean editTextIsNotEmpty(String username, String email, String password) {
        return username != null && !username.isEmpty() && email != null && !email.isEmpty() && password != null && !password.isEmpty() && password.length() >= 6;
    }

    private void setDialog() {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void uploadImageToDatabase(final String username, final String email, final String password) {
        if(imageProfilePath != null && !imageProfilePath.equals(Uri.EMPTY)) {
            progressDialog.setTitle("Mengunggah foto profil");
            progressDialog.setMessage("Sedang mengunggah foto profil");
            progressDialog.show();
            final StorageReference ref = storageReference.child("profileImages/" + email);
            ref.putFile(imageProfilePath)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return ref.getDownloadUrl();
                        }})
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                signUp(username, email, password, task.getResult().toString());
                            } else {
                                progressDialog.dismiss();
                                toast(getApplicationContext(), "Terjadi kesalahan saat mendapatkan url gambar");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            toast(getApplicationContext(), "Terjadi kesalahan saat mengunggah gambar");
                        }
                    });
        } else {
            progressDialog.setTitle("Mohon tunggu");
            progressDialog.setMessage("Sedang mendaftarkan akun");
            progressDialog.show();
            signUp(username, email, password, "");
        }
    }

    private void signUp(final String username, final String email, final String password, final String urlProfileImage) {
        progressDialog.setTitle("Mohon tunggu");
        progressDialog.setMessage("Sedang mendaftarkan akun");
        if (firebaseAuth.getCurrentUser() == null) {
            firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (firebaseAuth.getCurrentUser() != null) {
                                    firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION).document(email).set(setUserAccountInformationMap(username, email, password, firebaseAuth.getCurrentUser().getUid(), urlProfileImage))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    toast(getApplicationContext(), "Berhasil mendaftarkan akun");
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

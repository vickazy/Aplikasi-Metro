package com.jojo.metroapp.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jojo.metroapp.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import static com.jojo.metroapp.config.config.BK_IMAGE_URI_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_STATUS_UNCONFIRMED_FORM;
import static com.jojo.metroapp.config.config.DB_PUBLIC_FORM;
import static com.jojo.metroapp.config.config.DB_PUBLIC_FORM_GENERAL_SETTINGS;
import static com.jojo.metroapp.config.config.DB_PUBLIC_FORM_GENERAL_SETTINGS_TOTAL_FORM;
import static com.jojo.metroapp.config.config.DB_PUBLIC_FORM_SETTINGS;
import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_HISTORY;
import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_INFORMATION;
import static com.jojo.metroapp.config.config.RC_PICK_IMAGE;
import static com.jojo.metroapp.utils.utils.setFormMap;
import static com.jojo.metroapp.utils.utils.setPublicFormGeneralSettings;
import static com.jojo.metroapp.utils.utils.toast;

public class PengajuanIzinActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private String username, title, alasan, dateDari, dateSampai;
    private Uri imageFilePath;
    private ImageView gambarIzin;
    private LinearLayout frameBtnAturGambar;
    private AppCompatButton btnAmbilGambar;
    private int dayDari, monthDari, yearDari, daySampai, monthSampai, yearSampai;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_izin);
        setActivityTitle();

        setDialog();

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        firebaseFirestore.setFirestoreSettings(firebaseFirestoreSettings);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        getUsername();

        // UI Component
        final EditText edtTitle = findViewById(R.id.edtTitle);
        final EditText edtAlasan = findViewById(R.id.edtAlasan);
        final EditText edtDari = findViewById(R.id.edtDari);
        final EditText edtSampai = findViewById(R.id.edtSampai);
        FrameLayout btnDateDari = findViewById(R.id.btnDateDari);
        FrameLayout btnDateSampai = findViewById(R.id.btnDateSampai);
        FrameLayout btnKirimPermohonan = findViewById(R.id.btnKirimPermohonan);
        btnAmbilGambar = findViewById(R.id.btnAmbilGambar);
        AppCompatButton btnEditGambar = findViewById(R.id.btnEditGambar);
        AppCompatButton btnLihatGambar = findViewById(R.id.btnLihatGambar);
        frameBtnAturGambar = findViewById(R.id.frameBtnAturGambar);
        gambarIzin = findViewById(R.id.gambarIzin);

        // Set On Click Listener
        btnDateDari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(PengajuanIzinActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override @SuppressLint("SetTextI18n")
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dayDari = dayOfMonth;
                        monthDari = monthOfYear + 1;
                        yearDari = year;
                        if ((dayDari <= daySampai && monthDari <= monthSampai && yearDari <= yearSampai) || (dateSampai == null || dateSampai.isEmpty())) {
                            dateDari = dayDari + "/" + monthDari + "/" + yearDari;
                            edtDari.setText(dateDari);
                        } else {
                            toast(getApplicationContext(), "Tanggal mulainya izin tidak boleh melebihi tanggal berkahir izin");
                        }
                    }}, year, month, day);
                datePickerDialog.show();
            }
        });

        btnDateSampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(PengajuanIzinActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        daySampai = dayOfMonth;
                        monthSampai = monthOfYear + 1;
                        yearSampai = year;
                        if ((daySampai >= dayDari && monthSampai >= monthDari && yearSampai >= yearDari) || (dateDari == null || dateDari.isEmpty())) {
                            dateSampai = daySampai + "/" + monthSampai + "/" + yearSampai;
                            edtSampai.setText(dateSampai);
                        } else {
                            toast(getApplicationContext(), "Tanggal berakhirnya izin tidak boleh lebih awal dari tanggal mulai izin");
                        }
                    }}, year, month, day);
                datePickerDialog.show();
            }
        });

        btnAmbilGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambilGambar();
            }
        });

        btnEditGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambilGambar();
            }
        });

        btnLihatGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lihatGambar();
            }
        });

        btnKirimPermohonan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = edtTitle.getText().toString();
                alasan = edtAlasan.getText().toString();
                idFactory();
                if (formIsNotEmpty(title, alasan, dateDari, dateSampai, imageFilePath)) {
                    uploadImageToDatabase();
                } else {
                    if (title.isEmpty()) {
                        toast(getApplicationContext(), "Mohon isi judul absensi");
                    } else if (alasan.isEmpty()) {
                        toast(getApplicationContext(), "Mohon isi alasan absensi");
                    } else if (dateDari.isEmpty()) {
                        toast(getApplicationContext(), "Mohon isi tanggal mulainya izin");
                    } else if (dateSampai.isEmpty()) {
                        toast(getApplicationContext(), "Mohon isi tanggal berakhirnya izin");
                    } else if (imageFilePath == null || imageFilePath.equals(Uri.EMPTY)) {
                        toast(getApplicationContext(), "Mohon sisipkan gambar surat izin");
                    }
                }
            }
        });
    }

    private String idFactory() {
        return UUID.randomUUID().toString();
    }

    private void getUsername() {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION).document(firebaseAuth.getCurrentUser().getUid()).
            get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            username = documentSnapshot.getString("username");
                        }
                    }
                }
            });
        }
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);
    }

    private boolean formIsNotEmpty(String title, String alasan, String dateDari, String dateSampai, Uri imageUri) {
        return title != null && !title.isEmpty() && alasan != null && !alasan.isEmpty() && dateDari != null && !dateDari.isEmpty() && dateSampai != null && !dateSampai.isEmpty() && imageUri != null;
    }

    private void setDialog() {
        progressDialog = new ProgressDialog(PengajuanIzinActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }
    private void uploadImageToDatabase() {
        if(imageFilePath != null && !imageFilePath.equals(Uri.EMPTY)) {
            progressDialog.setTitle("Mengunggah gambar");
            progressDialog.setMessage("Sedang mengunggah gambar pengajuan absensi");
            progressDialog.show();
            final StorageReference ref = storageReference.child("formImages/" + idFactory());
            ref.putFile(imageFilePath)
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
                                sendFormToDatabase(title, alasan, dateDari, dateSampai, task.getResult().toString());
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
        }
    }

    private void sendFormToDatabase(final String title, final String alasan, final String dateDari, final String dateSampai, final String imageUrl) {
        progressDialog.setTitle("Menunggah formulir");
        progressDialog.setMessage("Sedang mengunggah formulir pengajuan absensi");
        final String currentDate = getCurrentDate();
        if (firebaseAuth.getCurrentUser() != null) {
            // Get public number
            firebaseFirestore.collection(DB_PUBLIC_FORM_SETTINGS)
                    .document(DB_PUBLIC_FORM_GENERAL_SETTINGS)
                    .get()
                    // Get public number success
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        String totalFormCount = "0";
                        if (documentSnapshot.exists()) { totalFormCount = documentSnapshot.getString(DB_PUBLIC_FORM_GENERAL_SETTINGS_TOTAL_FORM); }
                        int formNumber = Integer.valueOf(totalFormCount);
                        final int newFormNumber = formNumber + 1;
                        // Send to public
                        firebaseFirestore.collection(DB_PUBLIC_FORM)
                                .document(String.valueOf(newFormNumber) + "-" + firebaseAuth.getCurrentUser().getUid())
                                .set(setFormMap(
                                        String.valueOf(newFormNumber),
                                        firebaseAuth.getCurrentUser().getEmail(),
                                        username,
                                        firebaseAuth.getCurrentUser().getUid(),
                                        currentDate,
                                        title,
                                        alasan,
                                        dateDari,
                                        dateSampai,
                                        imageUrl,
                                        BK_STATUS_UNCONFIRMED_FORM))
                                // Upload to public success
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // Send to private history
                                        firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION)
                                                .document(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()))
                                                .collection(DB_USER_ACCOUNT_HISTORY)
                                                .document(String.valueOf(newFormNumber)).set(setFormMap(
                                                        String.valueOf(newFormNumber),
                                                        firebaseAuth.getCurrentUser().getEmail(),
                                                        username,
                                                        firebaseAuth.getCurrentUser().getUid(),
                                                        currentDate,
                                                        title,
                                                        alasan,
                                                        dateDari,
                                                        dateSampai,
                                                        imageUrl,
                                                        BK_STATUS_UNCONFIRMED_FORM))
                                                // Upload to private history success
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        // Incrementing public number
                                                        progressDialog.setTitle("Menyelesaikan unggahan");
                                                        progressDialog.setMessage("Sedang memeriksa dan menyelesaikan unggahan anda");
                                                        firebaseFirestore.collection(DB_PUBLIC_FORM_SETTINGS)
                                                                .document(DB_PUBLIC_FORM_GENERAL_SETTINGS)
                                                                .set(setPublicFormGeneralSettings(String.valueOf(newFormNumber)))
                                                        // Increment success
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                progressDialog.dismiss();
                                                                toast(getApplicationContext(), "Pengajuan izin berhasil diunggah");
                                                                finish();
                                                            }
                                                        })
                                                        // Increment fail
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.dismiss();
                                                                toast(getApplicationContext(), "Terjadi kesalahan saat memeriksa formulir pengajuan absensi");
                                                            }
                                                        });
                                                    }
                                                })
                                                // Upload to private history fail
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        toast(getApplicationContext(), "Terjadi kesalahan saat mengunggah formulir pengajuan absensi");
                                                    }
                                                });
                                    }
                                })
                                // Upload to public fail
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        toast(getApplicationContext(), "Terjadi kesalahan saat mengunggah gambar pengajuan absensi");
                                    }
                                });
                    }
                }
            })
            // Get public number fail
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    toast(getApplicationContext(), "Terjadi kesalahan saat mengambil id formulir");
                }
            });
        } else {
            progressDialog.dismiss();
            toast(getApplicationContext(), "Telah terjadi kesalahan tak terduga!");
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finishAffinity();
        }
    }

    private void ambilGambar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Ambil Gambar"), RC_PICK_IMAGE);
    }

    private void lihatGambar() {
        startActivity(new Intent(getApplicationContext(), LihatGambarActivity.class). putExtra(BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR, BK_IMAGE_URI_TYPE_ACTIVITY_LIHAT_GAMBAR).putExtra(BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR, imageFilePath.toString()));
    }

    private void setActivityTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pengajuan Izin");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePath);
                gambarIzin.setImageBitmap(bitmap);
                frameBtnAturGambar.setVisibility(View.VISIBLE);
                btnAmbilGambar.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
                toast(getApplicationContext(), "Terjadi kesalahan saat mengambil gambar");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

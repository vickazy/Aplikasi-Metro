package com.jojo.metroapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.jojo.metroapp.R;

import java.util.Objects;

import static com.jojo.metroapp.config.config.BK_DATE_DARI_FORM;
import static com.jojo.metroapp.config.config.BK_DATE_PUBLISHED_FORM;
import static com.jojo.metroapp.config.config.BK_DATE_SAMPAI_FORM;
import static com.jojo.metroapp.config.config.BK_DESKRIPSI_FORM;
import static com.jojo.metroapp.config.config.BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_URL_FORM;
import static com.jojo.metroapp.config.config.BK_IMAGE_URL_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_NUMBER_FORM;
import static com.jojo.metroapp.config.config.BK_STATUS_CANCELED_FORM;
import static com.jojo.metroapp.config.config.BK_STATUS_CONFIRMED_FORM;
import static com.jojo.metroapp.config.config.BK_STATUS_FORM;
import static com.jojo.metroapp.config.config.BK_TITLE_FORM;
import static com.jojo.metroapp.config.config.DB_PUBLIC_FORM;
import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_HISTORY;
import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_INFORMATION;
import static com.jojo.metroapp.utils.utils.setCancelForm;
import static com.jojo.metroapp.utils.utils.setImageWithGlide;
import static com.jojo.metroapp.utils.utils.toast;

public class DetailFormUserActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView title, deskripsi, statusConfirmed, statusUnconfirmed, statusCanceled, statusConfirmedDescription, statusUnconfirmedDescription, statusCanceledDescription, datePublished, dateDari, dateSampai;
    private String getTitle, getDeskripsi, getImageUrl, status, getDateDari, getDateSampai, getDatePublished, formNumber;
    private ImageView imageView;
    private CardView btnBatalAbsensiFrame, btnUbahAbsensiFrame;
    private ProgressDialog progressDialog;
    private FrameLayout btnBatalAbsensi, btnUbahAbsensi;
    private int exitStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form_user);

        setDialog();

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        firebaseFirestore.setFirestoreSettings(firebaseFirestoreSettings);

        // UI Component
        title = findViewById(R.id.title_detailFormUserActivity);
        deskripsi = findViewById(R.id.alasan_detailFormUserActivity);
        statusConfirmed = findViewById(R.id.statusConfirmed_detailFormUserActivity);
        statusUnconfirmed = findViewById(R.id.statusUnconfirmed_detailFormUserActivity);
        statusCanceled = findViewById(R.id.statusCanceled_detailFormUserActivity);
        statusConfirmedDescription = findViewById(R.id.statusConfirmedDescription_detailFormUserActivity);
        statusUnconfirmedDescription = findViewById(R.id.statusUnconfirmedDescription_detailFormUserActivity);
        statusCanceledDescription = findViewById(R.id.statusCanceledDescription_detailFormUserActivity);
        imageView = findViewById(R.id.image_detailFormUserActivity);
        datePublished = findViewById(R.id.datePublished_detailFormUserActivity);
        dateDari = findViewById(R.id.dateDari_detailFormUserActivity);
        dateSampai = findViewById(R.id.dateSampai_detailFormUserActivity);
        btnBatalAbsensiFrame = findViewById(R.id.btnBatalAbsensiFrame_detailFormUserActivity);
        btnBatalAbsensi = findViewById(R.id.btnBatalAbsensi_detailFormUserActivity);
        btnUbahAbsensiFrame = findViewById(R.id.btnUbahAbsensiFrame_detailFormUserActivity);
        btnUbahAbsensi = findViewById(R.id.btnUbahAbsensi_detailFormUserActivity);

        // set View
        setToolbar();
        getData();
        setData();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_detailFormUserActivity);
        CollapsingToolbarLayout ctl = findViewById(R.id.ctl_detailFormUserActivity);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ctl.setTitle("Detail Absensi");
    }

    private void getData() {
        getImageUrl = Objects.requireNonNull(getIntent().getExtras()).getString(BK_IMAGE_URL_FORM);
        getTitle = Objects.requireNonNull(getIntent().getExtras()).getString(BK_TITLE_FORM);
        getDeskripsi = Objects.requireNonNull(getIntent().getExtras()).getString(BK_DESKRIPSI_FORM);
        getDatePublished = Objects.requireNonNull(getIntent().getExtras()).getString(BK_DATE_PUBLISHED_FORM);
        getDateDari = Objects.requireNonNull(getIntent().getExtras()).getString(BK_DATE_DARI_FORM);
        getDateSampai = Objects.requireNonNull(getIntent().getExtras()).getString(BK_DATE_SAMPAI_FORM);
        status = Objects.requireNonNull(getIntent().getExtras()).getString(BK_STATUS_FORM);
        formNumber = Objects.requireNonNull(getIntent().getExtras()).getString(BK_NUMBER_FORM);
    }

    private void setData() {
        setImageWithGlide(getApplicationContext(), imageView, getImageUrl, 1);
        title.setText(getTitle);
        deskripsi.setText(getDeskripsi);
        datePublished.setText(getDatePublished);
        dateDari.setText(getDateDari);
        dateSampai.setText(getDateSampai);
        setStatus(status);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LihatGambarActivity.class).putExtra(BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR, BK_IMAGE_URL_TYPE_ACTIVITY_LIHAT_GAMBAR).putExtra(BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR, getImageUrl));
            }
        });

        btnBatalAbsensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelForm();
            }
        });
    }

    private void setStatus(String type) {
        switch (type) {
            case BK_STATUS_CONFIRMED_FORM:
                statusConfirmed.setVisibility(View.VISIBLE);
                statusConfirmedDescription.setVisibility(View.VISIBLE);
                statusCanceled.setVisibility(View.GONE);
                statusCanceledDescription.setVisibility(View.GONE);
                statusUnconfirmed.setVisibility(View.GONE);
                statusUnconfirmedDescription.setVisibility(View.GONE);

                btnBatalAbsensiFrame.setVisibility(View.GONE);
                btnUbahAbsensiFrame.setVisibility(View.GONE);
                break;
            case BK_STATUS_CANCELED_FORM:
                statusConfirmed.setVisibility(View.GONE);
                statusConfirmedDescription.setVisibility(View.GONE);
                statusCanceled.setVisibility(View.VISIBLE);
                statusCanceledDescription.setVisibility(View.VISIBLE);
                statusUnconfirmed.setVisibility(View.GONE);
                statusUnconfirmedDescription.setVisibility(View.GONE);

                btnBatalAbsensiFrame.setVisibility(View.GONE);
                btnUbahAbsensiFrame.setVisibility(View.GONE);
                break;
            default:
                statusConfirmed.setVisibility(View.GONE);
                statusConfirmedDescription.setVisibility(View.GONE);
                statusCanceled.setVisibility(View.GONE);
                statusCanceledDescription.setVisibility(View.GONE);
                statusUnconfirmed.setVisibility(View.VISIBLE);
                statusUnconfirmedDescription.setVisibility(View.VISIBLE);

                // btn ubah absensi is not ready yet
                btnUbahAbsensiFrame.setVisibility(View.GONE);
                break;
        }
    }

    private void setDialog() {
        progressDialog = new ProgressDialog(DetailFormUserActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void cancelForm() {
        progressDialog.setTitle("Membatalkan absensi");
        progressDialog.setMessage("Sedang membatalkan formulir absensi anda");
        progressDialog.show();
        if (firebaseAuth.getCurrentUser() != null) {
            // Update status to public
            firebaseFirestore
                    .collection(DB_PUBLIC_FORM)
                    .document(formNumber + "-" + firebaseAuth.getCurrentUser().getUid())
                    .update("status", BK_STATUS_CANCELED_FORM)
                    // Update status to public success
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Upload status to private history
                            firebaseFirestore
                                    .collection(DB_USER_ACCOUNT_INFORMATION)
                                    .document(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()))
                                    .collection(DB_USER_ACCOUNT_HISTORY)
                                    .document(formNumber)
                                    .update("status", BK_STATUS_CANCELED_FORM)
                                    // Upload to private history success
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            exitStatus = 1;
                                            setStatus(BK_STATUS_CANCELED_FORM);
                                            progressDialog.dismiss();
                                            toast(getApplicationContext(), "Berhasil membatalkan absensi");
                                        }
                                    })
                                    // Upload to private history fail
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            toast(getApplicationContext(), "Terjadi kesalahan saat mencoba membatalkan absensi");
                                        }
                                    });
                        }
                    })
                    // Upload to public fail
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            toast(getApplicationContext(), "Terjadi kesalahan saat mencoba membatalkan absensi");
                        }
                    });
        } else {
            progressDialog.dismiss();
            toast(getApplicationContext(), "Telah terjadi kesalahan saat menghubungi server");
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finishAffinity();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        switch (exitStatus) {
            case 0:
                setResult(Activity.RESULT_CANCELED, new Intent());
                break;
            case 1:
                setResult(Activity.RESULT_OK, new Intent().putExtra(BK_STATUS_FORM, BK_STATUS_CANCELED_FORM));
                break;
        }
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        switch (exitStatus) {
            case 0:
                setResult(Activity.RESULT_CANCELED, new Intent());
                break;
            case 1:
                setResult(Activity.RESULT_OK, new Intent().putExtra(BK_STATUS_FORM, BK_STATUS_CANCELED_FORM));
                break;
        }
        finish();
    }
}

package com.jojo.metroapp.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jojo.metroapp.R;

import java.util.Objects;

import static com.jojo.metroapp.config.config.BK_ALASAN_FORM;
import static com.jojo.metroapp.config.config.BK_DATE_DARI_FORM;
import static com.jojo.metroapp.config.config.BK_DATE_PUBLISHED_FORM;
import static com.jojo.metroapp.config.config.BK_DATE_SAMPAI_FORM;
import static com.jojo.metroapp.config.config.BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_URL_FORM;
import static com.jojo.metroapp.config.config.BK_IMAGE_URL_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_STATUS_CONFIRMED_FORM;
import static com.jojo.metroapp.config.config.BK_STATUS_FORM;
import static com.jojo.metroapp.config.config.BK_TITLE_FORM;
import static com.jojo.metroapp.utils.utils.setImageWithGlide;

public class DetailFormUserActivity extends AppCompatActivity {

    private TextView title, alasan, statusConfirmed, statusConfirmed2, statusUnconfirmed, statusUnconfirmed2, datePublished, dateDari, dateSampai;
    private String getTitle, getAlasan, getImageUrl, status, getDateDari, getDateSampai, getDatePublished;
    private ImageView imageView;
    private CardView btnBatalAbsensiFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form_user);

        // UI Component
        title = findViewById(R.id.title_detailFormUserActivity);
        alasan = findViewById(R.id.alasan_detailFormUserActivity);
        statusConfirmed = findViewById(R.id.statusConfirmed_detailFormUserActivity);
        statusConfirmed2 = findViewById(R.id.statusConfirmed2_detailFormUserActivity);
        statusUnconfirmed = findViewById(R.id.statusUnconfirmed_detailFormUserActivity);
        statusUnconfirmed2 = findViewById(R.id.statusUnconfirmed2_detailFormUserActivity);
        imageView = findViewById(R.id.image_detailFormUserActivity);
        datePublished = findViewById(R.id.datePublished_detailFormUserActivity);
        dateDari = findViewById(R.id.dateDari_detailFormUserActivity);
        dateSampai = findViewById(R.id.dateSampai_detailFormUserActivity);
        btnBatalAbsensiFrame = findViewById(R.id.btnBatalAbsensiFrame_detailFormUserActivity);

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
        getAlasan = Objects.requireNonNull(getIntent().getExtras()).getString(BK_ALASAN_FORM);
        getDatePublished = Objects.requireNonNull(getIntent().getExtras()).getString(BK_DATE_PUBLISHED_FORM);
        getDateDari = Objects.requireNonNull(getIntent().getExtras()).getString(BK_DATE_DARI_FORM);
        getDateSampai = Objects.requireNonNull(getIntent().getExtras()).getString(BK_DATE_SAMPAI_FORM);
        status = Objects.requireNonNull(getIntent().getExtras()).getString(BK_STATUS_FORM);
    }

    private void setData() {
        setImageWithGlide(getApplicationContext(), imageView, getImageUrl, 1);
        title.setText(getTitle);
        alasan.setText(getAlasan);
        datePublished.setText(getDatePublished);
        dateDari.setText(getDateDari);
        dateSampai.setText(getDateSampai);
        if (status.equals(BK_STATUS_CONFIRMED_FORM)) {
            statusConfirmed.setVisibility(View.VISIBLE);
            statusConfirmed2.setVisibility(View.VISIBLE);
            btnBatalAbsensiFrame.setVisibility(View.GONE);
        } else {
            statusUnconfirmed.setVisibility(View.VISIBLE);
            statusUnconfirmed2.setVisibility(View.VISIBLE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LihatGambarActivity.class).putExtra(BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR, BK_IMAGE_URL_TYPE_ACTIVITY_LIHAT_GAMBAR).putExtra(BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR, getImageUrl));
            }
        });
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

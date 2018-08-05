package com.jojo.metroapp.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.jojo.metroapp.R;

import java.io.IOException;
import java.util.Objects;

import static com.jojo.metroapp.config.config.BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_URI_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.config.config.BK_IMAGE_URL_TYPE_ACTIVITY_LIHAT_GAMBAR;
import static com.jojo.metroapp.utils.utils.setImageWithGlide;
import static com.jojo.metroapp.utils.utils.toast;

public class LihatGambarActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_gambar);
        setToolbar();

        // UI Component
        imageView = findViewById(R.id.image_activityLihatGambar);

        // Set View
        setImage();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_activityLihatGambar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void setImage() {
        String imagePath = Objects.requireNonNull(getIntent().getExtras()).getString(BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR);
        int type = Objects.requireNonNull(getIntent().getExtras()).getInt(BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR);
        switch (type) {
            case BK_IMAGE_URI_TYPE_ACTIVITY_LIHAT_GAMBAR:
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imagePath));
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    toast(getApplicationContext(), "Telah terjadi kesalahan");
                    finish();
                }
                break;
            case BK_IMAGE_URL_TYPE_ACTIVITY_LIHAT_GAMBAR:
                setImageWithGlide(getApplicationContext(), imageView, imagePath, 0);
                break;
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

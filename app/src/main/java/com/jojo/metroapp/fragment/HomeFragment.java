package com.jojo.metroapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jojo.metroapp.R;
import com.jojo.metroapp.activity.PengajuanIzinActivity;
import com.jojo.metroapp.activity.RiwayatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.jojo.metroapp.utils.utils.removeImageWithGlide;
import static com.jojo.metroapp.utils.utils.setImageWithGlideFromLocal;

public class HomeFragment extends Fragment {

    private ImageView clockThemeWidget;
    private TextView textViewClockAmPm, textViewDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // UI Component
        FrameLayout btnPengajuanIzin = v.findViewById(R.id.btnPengajuanIzin);
        FrameLayout btnRiwayat = v.findViewById(R.id.btnRiwayat);
        textViewClockAmPm = v.findViewById(R.id.textViewClockAmPmFragmentHome);
        textViewDate = v.findViewById(R.id.textViewDateFragmentHome);
        clockThemeWidget = v.findViewById(R.id.clockWidgetFragmentHome);

        // Set On Click Listener
        btnPengajuanIzin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getContext()).startActivity(new Intent(getContext(), PengajuanIzinActivity.class));
            }
        });

        btnRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getContext()).startActivity(new Intent(getContext(), RiwayatActivity.class));
            }
        });
        return v;
    }

    private void setClockWidget() {
        textViewClockAmPm.setText(new SimpleDateFormat("a", Locale.getDefault()).format(new Date()));
        textViewDate.setText(new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(new Date()));
        int hour = Integer.valueOf(new SimpleDateFormat("H", Locale.getDefault()).format(new Date()));
        if (hour >= 0 && hour <= 4) {
            setImageWithGlideFromLocal(getContext(), clockThemeWidget, R.drawable.dark_theme, 1);
        } else if (hour >= 5 && hour <= 12) {
            setImageWithGlideFromLocal(getContext(), clockThemeWidget, R.drawable.light_theme, 1);
        } else if (hour >= 13 && hour <= 14) {
            setImageWithGlideFromLocal(getContext(), clockThemeWidget, R.drawable.light_theme, 1);
        } else if (hour >= 15 && hour <= 17) {
            setImageWithGlideFromLocal(getContext(), clockThemeWidget, R.drawable.light_theme, 1);
        } else if (hour >= 18 && hour <= 23) {
            setImageWithGlideFromLocal(getContext(), clockThemeWidget, R.drawable.dark_theme, 1);
        }
    }

    private void removeClockWidget() {
        textViewClockAmPm.destroyDrawingCache();
        textViewClockAmPm.setText(null);
        textViewDate.destroyDrawingCache();
        textViewDate.setText(null);
        removeImageWithGlide(getContext(), clockThemeWidget);
    }

    @Override
    public void onStart() {
        super.onStart();
        setClockWidget();
    }

    @Override
    public void onStop() {
        super.onStop();
        removeClockWidget();
    }
}
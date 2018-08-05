package com.jojo.metroapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jojo.metroapp.R;
import com.jojo.metroapp.activity.PengajuanIzinActivity;
import com.jojo.metroapp.activity.RiwayatActivity;

import java.util.Objects;

public class HomeFragment extends Fragment {

    public HomeFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // UI Component
        FrameLayout btnPengajuanIzin = v.findViewById(R.id.btnPengajuanIzin);
        FrameLayout btnRiwayat = v.findViewById(R.id.btnRiwayat);

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
}

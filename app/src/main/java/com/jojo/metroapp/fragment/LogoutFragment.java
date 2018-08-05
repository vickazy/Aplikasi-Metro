package com.jojo.metroapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.jojo.metroapp.R;
import com.jojo.metroapp.activity.LoginActivity;

import java.util.Objects;

public class LogoutFragment extends Fragment {

    // Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logout, container, false);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // UI Component
        FrameLayout btnLogOut = v.findViewById(R.id.btnLogOut);

        // Set On Click Listener
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                Objects.requireNonNull(getActivity()).finishAffinity();
            }
        });

        return v;
    }
}

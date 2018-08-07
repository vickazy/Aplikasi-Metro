package com.jojo.metroapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jojo.metroapp.R;
import com.jojo.metroapp.model.FormModel;
import com.jojo.metroapp.utils.recyclerview.HistoryRecyclerViewAdapter;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Objects;

import static com.jojo.metroapp.config.config.BK_STATUS_CANCELED_FORM;
import static com.jojo.metroapp.config.config.BK_STATUS_CONFIRMED_FORM;
import static com.jojo.metroapp.config.config.BK_STATUS_FORM;
import static com.jojo.metroapp.config.config.DB_PUBLIC_FORM;
import static com.jojo.metroapp.config.config.DB_PUBLIC_FORM_NUMBER;
import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_HISTORY;
import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_INFORMATION;
import static com.jojo.metroapp.config.config.RC_ACTIVITYFORM_STATUS;
import static com.jojo.metroapp.utils.utils.toast;

public class RiwayatActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout placeholderNoRiwayat, placeholderLoadRiwayat, placeholderErrorRiwayat;
    private HistoryRecyclerViewAdapter adapter;
    private ArrayList<FormModel> arrayListForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);
        setActivityTitle();

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        firebaseFirestore.setFirestoreSettings(firebaseFirestoreSettings);

        // UI Component
        recyclerView = findViewById(R.id.recyclerViewRiwayat);
        progressBar = findViewById(R.id.progressBarRiwayat);
        placeholderNoRiwayat = findViewById(R.id.placeholderNoRiwayat);
        placeholderLoadRiwayat = findViewById(R.id.placeholderLoadRiwayat);
        placeholderErrorRiwayat = findViewById(R.id.placeholderLoadRiwayatError);

        // Set View
        setRecyclerView();
        getHistoryList();
    }

    private void setRecyclerView() {
        progressBar.setIndeterminate(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        /*recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));*/
    }

    private void getHistoryList() {
        progressBar.setVisibility(View.VISIBLE);
        placeholderLoadRiwayat.setVisibility(View.VISIBLE);
        if (firebaseAuth.getCurrentUser() != null) {
            // Query private history
            firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION).document(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail())).collection(DB_USER_ACCOUNT_HISTORY).orderBy(DB_PUBLIC_FORM_NUMBER, Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (!task.getResult().isEmpty()) {
                                arrayListForm = new ArrayList<>();
                                for (DocumentSnapshot querySnapshot : task.getResult()) {
                                    FormModel formModel = new FormModel(querySnapshot.getString("formNumber"), querySnapshot.getString("username"), querySnapshot.getString("titleForm"), querySnapshot.getString("deskripsi"), querySnapshot.getString("dateDari"), querySnapshot.getString("dateSampai"), querySnapshot.getString("imageUrl"), querySnapshot.getString("datePublished"), querySnapshot.getString("status"));
                                    arrayListForm.add(formModel);
                                }
                                adapter = new HistoryRecyclerViewAdapter(RiwayatActivity.this, arrayListForm);
                                recyclerView.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);
                                placeholderLoadRiwayat.setVisibility(View.GONE);
                                placeholderNoRiwayat.setVisibility(View.GONE);
                                placeholderErrorRiwayat.setVisibility(View.GONE);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                placeholderLoadRiwayat.setVisibility(View.GONE);
                                placeholderNoRiwayat.setVisibility(View.VISIBLE);
                                placeholderErrorRiwayat.setVisibility(View.GONE);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            placeholderLoadRiwayat.setVisibility(View.GONE);
                            placeholderNoRiwayat.setVisibility(View.GONE);
                            placeholderErrorRiwayat.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private void setActivityTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Riwayat");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_ACTIVITYFORM_STATUS && resultCode == Activity.RESULT_OK && data != null && data.getStringExtra(BK_STATUS_FORM) != null) {
            arrayListForm = null;
            adapter = null;
            recyclerView.setAdapter(null);
            getHistoryList();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.destroyDrawingCache();
        recyclerView.setAdapter(null);
    }
}

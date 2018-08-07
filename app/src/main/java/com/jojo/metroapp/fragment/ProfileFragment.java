package com.jojo.metroapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.jojo.metroapp.R;
import com.jojo.metroapp.activity.LoginActivity;

import java.util.Objects;

import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_INFORMATION;
import static com.jojo.metroapp.utils.utils.setImageWithGlide;
import static com.jojo.metroapp.utils.utils.toast;

public class ProfileFragment extends Fragment {

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ImageView profileImage;
    private TextView username, email, placeholderNoDescriptionText;
    private EditText description;
    private String updateDescription = "", cachedDescription = "", cachedDescriptionText = "";
    private CardView  btnUpdateDescriptionFrame, btnCancelUpdateDescriptionFrame;
    private FrameLayout placeholderNoDescription;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        setDialog();

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        firebaseFirestore.setFirestoreSettings(firebaseFirestoreSettings);

        // UI Component
        profileImage = v.findViewById(R.id.imageProfile_fragmentProfile);
        username = v.findViewById(R.id.username_fragmentProfile);
        email = v.findViewById(R.id.email_fragmentProfile);
        description = v.findViewById(R.id.edtDeskrisiProfile_fragmentProfile);
        FrameLayout btnUpdateDescription = v.findViewById(R.id.btnUpdateDeskripsiProfil);
        btnUpdateDescriptionFrame = v.findViewById(R.id.btnUpdateDeskripsiProfilFrame);
        placeholderNoDescription = v.findViewById(R.id.placeholderNoUserDescription);
        placeholderNoDescriptionText = v.findViewById(R.id.placeholderNoUserDescriptionText);
        FrameLayout btnCancelUpdateDescription = v.findViewById(R.id.btnCancelUpdateDeskripsiProfil);
        btnCancelUpdateDescriptionFrame = v.findViewById(R.id.btnCancelUpdateDeskripsiProfilFrame);

        // Set View
        setProfileInformation();

        // Loading
        updateDescriptionPanelText("Sedang memuat informasi profil anda...");

        // Set On Click Listener
        btnUpdateDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDescription();
            }
        });
        btnCancelUpdateDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cancelUpdateDescription(); }
        });
        return v;
    }

    private void setProfileInformation() {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore
                    .collection(DB_USER_ACCOUNT_INFORMATION)
                    .document(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    String urlProfileImage = documentSnapshot.getString("profileImage");
                                    String getUsername = documentSnapshot.getString("username");
                                    String getDescription = documentSnapshot.getString("description");
                                    setImageWithGlide(Objects.requireNonNull(getActivity()).getApplicationContext(), profileImage, urlProfileImage, 4);
                                    username.setText(getUsername);
                                    email.setText(firebaseAuth.getCurrentUser().getEmail());
                                    initDescriptionUpdater();
                                    if (getDescription != null && !getDescription.isEmpty()) {
                                        updateDescriptionPanelText(getDescription);
                                        updateDescriptionText(getDescription);
                                        cachedDescription = getDescription;
                                        cachedDescriptionText = getDescription;
                                    } else {
                                        updateDescriptionPanelText("Belum ada deskripsi pengguna.\nKetuk disini untuk menambahkan.");
                                    }
                                    placeholderNoDescription.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            hideDescriptionPanel();
                                            showAllDescriptionBtn();
                                        }
                                    });
                                }
                            }
                        }});
        } else {
            toast(getContext(), "Telah terjadi kesalahan saat menghubungi server");
            startActivity(new Intent(getContext(), LoginActivity.class));
            Objects.requireNonNull(getActivity()).finishAffinity();
        }
    }

    private void setDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Memperbarui Deskripsi");
        progressDialog.setMessage("Sedang memperbarui deskripsi profil anda");
    }

    private void showAllDescriptionBtn() {
        btnUpdateDescriptionFrame.setVisibility(View.VISIBLE);
        btnCancelUpdateDescriptionFrame.setVisibility(View.VISIBLE);
    }

    private void hideAllDescriptionBtn() {
        btnUpdateDescriptionFrame.setVisibility(View.GONE);
        btnCancelUpdateDescriptionFrame.setVisibility(View.GONE);
    }

    private void showDescriptionPanel() {
        placeholderNoDescription.setVisibility(View.VISIBLE);
        description.setVisibility(View.GONE);
    }

    private void hideDescriptionPanel() {
        placeholderNoDescription.setVisibility(View.GONE);
        description.setVisibility(View.VISIBLE);
    }

    private void updateDescriptionPanelText(String description) {
        placeholderNoDescriptionText.setText(description);
    }

    private void updateDescriptionText(String getDescription) {
        description.setText(getDescription);
    }

    private void initDescriptionUpdater() {
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                updateDescription = s.toString();
            }
        });
    }

    private void updateDescription() {
        progressDialog.show();
        if (!updateDescription.equals(cachedDescription) && !updateDescription.equals("")) {
            updateDescriptionPanelText(updateDescription);
            cachedDescription = updateDescription;
            cachedDescriptionText = updateDescription;
            // NEED UPDATE NEW VALUE
            updateDescriptionDatabase(updateDescription);
        } else if (updateDescription.equals(cachedDescription)) {
            updateDescriptionPanelText(cachedDescription);
            cachedDescriptionText = cachedDescription;
            // NO NEED TO UPDATE
        } else {
            updateDescriptionPanelText("Belum ada deskripsi pengguna.\nKetuk disini untuk menambahkan.");
            cachedDescriptionText = "";
            // NEED UPDATE TO NULL
            updateDescriptionDatabase("");
        }
        showDescriptionPanel();
        hideAllDescriptionBtn();
    }

    private void updateDescriptionDatabase(String description) {
        firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION)
                .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                .update("description", description)
                // Update description success
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        toast(getContext(), "Berhasil memperbarui deskripsi profil");
                    }
                })
                // Update description fail
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        toast(getContext(), "Terjadi kesalahan saat mencoba memperbarui deskripsi profil");
                    }
                });
    }

    private void cancelUpdateDescription() {
        if (!cachedDescriptionText.equals("")) {
            updateDescriptionText(cachedDescriptionText);
            updateDescriptionPanelText(cachedDescriptionText);
        } else {
            updateDescriptionText("");
            updateDescriptionPanelText("Belum ada deskripsi pengguna.\nKetuk disini untuk menambahkan.");
        }
        showDescriptionPanel();
        hideAllDescriptionBtn();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        description.addTextChangedListener(null);
    }
}

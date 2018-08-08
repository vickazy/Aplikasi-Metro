package com.jojo.metroapp.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jojo.metroapp.R;
import com.jojo.metroapp.activity.LoginActivity;

import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.jojo.metroapp.config.config.DB_USER_ACCOUNT_INFORMATION;
import static com.jojo.metroapp.config.config.RC_PICK_IMAGE_PROFILE;
import static com.jojo.metroapp.utils.utils.idFactory;
import static com.jojo.metroapp.utils.utils.setImageWithGlide;
import static com.jojo.metroapp.utils.utils.toast;

public class ProfileFragment extends Fragment {

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ImageView profileImage;
    private TextView username, email, placeholderNoDescriptionText;
    private EditText description, edtChangeUsername, edtChangeEmail, edtChangePasswordOld, edtChangePasswordNew, edtChangePasswordNewConfirm;
    private String updateDescription = "", cachedDescription = "", cachedDescriptionText = "";
    private CardView  btnUpdateDescriptionFrame, btnCancelUpdateDescriptionFrame;
    private FrameLayout placeholderNoDescription;
    private ProgressDialog progressDialog;
    private Uri imageProfilePath;
    private View changeUsernameDialog, changeEmailDialog, changePasswordDialog;

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
        FrameLayout btnUbahGambar = v.findViewById(R.id.btnUbahFotoProfil);
        FrameLayout btnUbahUsername = v.findViewById(R.id.btnUbahUsername);
        FrameLayout btnUbahEmail = v.findViewById(R.id.btnUbahAlamatSurel);
        FrameLayout btnUbahPassword = v.findViewById(R.id.btnUbahKataSandi);

        // Set View
        setProfileInformation();
        initAlertDialog();

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

        btnUbahGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambilGambar();
            }
        });

        btnUbahUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername();
            }
        });

        btnUbahEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });

        btnUbahPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
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

    private void ambilGambar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Ambil Gambar"), RC_PICK_IMAGE_PROFILE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        description.addTextChangedListener(null);
        progressDialog.cancel();
        progressDialog = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_IMAGE_PROFILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageProfilePath = data.getData();
            new AlertDialog
                    .Builder(Objects.requireNonNull(getContext()))
                    .setIcon(null)
                    .setTitle("Ubah foto profil")
                    .setMessage("Anda yakin ingin mengubah foto profil anda?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            updateImageToDatabase(imageProfilePath);
                        }})
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }

    private void updateImageToDatabase(final Uri imagePath) {
        progressDialog.setTitle("Mengubah Foto Profil");
        progressDialog.setMessage("Sedang mengubah foto profil anda");
        progressDialog.show();
        // get image url
        firebaseFirestore
                .collection(DB_USER_ACCOUNT_INFORMATION)
                .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                String imageUrl = documentSnapshot.getString("profileImage");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    // revoking current image
                                    revokeImageInDatabase(imageUrl, imagePath);
                                } else {
                                    // send new image
                                    setImageInDatabase(imagePath);
                                }
                            }
                        } else {
                            progressDialog.dismiss();
                            toast(getContext(), "Terjadi kesalahan saat mengubah foto profil anda");
                        }
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        toast(getContext(), "Terjadi kesalahan saat mengubah foto profil anda");
                    }
                });
    }

    private void revokeImageInDatabase(final String imageUrl, final Uri imagePath) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        // deleting cached image
        storageReference
                .delete()
                // delete cached image success
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            // success deleting cached image
            @Override
            public void onSuccess(Void aVoid) {
                // uploading new image
                if(imagePath != null && !imagePath.equals(Uri.EMPTY)) {
                    final StorageReference ref = storageReference.child("profileImages/" + idFactory());
                    ref.putFile(imagePath)
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
                                    // update image url
                                    firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION)
                                            .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                                            .update("profileImage", task.getResult().toString())
                                            // update image url success
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    if (task.isSuccessful()) {
                                                        try {
                                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), imagePath);
                                                            profileImage.setImageBitmap(bitmap);
                                                            toast(getContext(), "Berhasil mengubah foto profil anda");
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                            toast(getContext(), "Terjadi kesalahan saat memproses foto profil");
                                                        }
                                                    } else {
                                                        toast(getContext(), "Terjadi kesalahan saat mengubah foto profil anda");
                                                    }
                                                }
                                            })
                                            // update image url fail
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    toast(getContext(), "Terjadi kesalahan saat mengubah foto profil anda");
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    toast(getContext(), "Terjadi kesalahan saat mengubah foto profil anda");
                                }
                            });
                }
            }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        toast(getContext(), "Terjadi kesalahan saat mengubah foto profil anda");
                    }
                });
    }

    private void setImageInDatabase(final Uri imagePath) {
        // uploading new image
        if (imagePath != null && !imagePath.equals(Uri.EMPTY)) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            final StorageReference ref = storageReference.child("profileImages/" + idFactory());
            ref.putFile(imagePath)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return ref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            // update image url
                            firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION)
                                    .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                                    .update("profileImage", task.getResult().toString())
                                    // update image url success
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                try {
                                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), imagePath);
                                                    profileImage.setImageBitmap(bitmap);
                                                    toast(getContext(), "Berhasil mengubah foto profil anda");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    toast(getContext(), "Terjadi kesalahan saat memproses foto profil");
                                                }
                                            } else {
                                                toast(getContext(), "Terjadi kesalahan saat mengubah foto profil anda");
                                            }
                                        }
                                    })
                                    // update image url fail
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            toast(getContext(), "Terjadi kesalahan saat mengubah foto profil anda");
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            toast(getContext(), "Terjadi kesalahan saat mengubah foto profil anda");
                        }
                    });
        }
    }

    @SuppressLint("InflateParams")
    private void initAlertDialog() {
        changeUsernameDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_username, null);
        edtChangeUsername = changeUsernameDialog.findViewById(R.id.edtChangeUsername);
        changeEmailDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_email, null);
        edtChangeEmail = changeEmailDialog.findViewById(R.id.edtChangeEmail);
        changePasswordDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        edtChangePasswordOld = changePasswordDialog.findViewById(R.id.edtChangePasswordOld);
        edtChangePasswordNew = changePasswordDialog.findViewById(R.id.edtChangePasswordNew);
        edtChangePasswordNewConfirm = changePasswordDialog.findViewById(R.id.edtChangePasswordNewConfirm);
    }

    private void changeUsername() {
        new AlertDialog
                .Builder(Objects.requireNonNull(getContext()))
                .setIcon(null)
                .setView(null)
                .setTitle("Ubah nama pengguna")
                .setView(changeUsernameDialog)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String newUsername = edtChangeUsername.getText().toString();
                        if (!newUsername.isEmpty()) {
                            new AlertDialog
                                    .Builder(Objects.requireNonNull(getContext()))
                                    .setIcon(null)
                                    .setTitle("Kanfirmasi nama pengguna")
                                    .setMessage("Anda yakin ingin mengganti nama pengguna anda menjadi \"" + newUsername + "\" ?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            updateUsername(newUsername);
                                        }})
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                        } else {
                            toast(getContext(), "Mohon masukkan nama pengguna baru");
                        }
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void updateUsername(final String newUsername) {
        progressDialog.setTitle("Mengubah nama pengguna");
        progressDialog.setMessage("Sedang mengubah nama pengguna anda");
        progressDialog.show();
        firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION)
                .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                .update("username", newUsername)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        toast(getContext(), "Berhasil mengubah nama pengguna");
                        username.setText(newUsername);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        toast(getContext(), "Terjadi kesalahan saat mengganti nama pengguna");
                    }
                });
    }

    private void changeEmail() {
        new AlertDialog
                .Builder(Objects.requireNonNull(getContext()))
                .setIcon(null)
                .setTitle("Ubah alamat surel")
                .setView(changeEmailDialog)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String newEmail = edtChangeEmail.getText().toString();
                        if (!newEmail.isEmpty()) {
                            new AlertDialog
                                    .Builder(Objects.requireNonNull(getContext()))
                                    .setIcon(null)
                                    .setTitle("Kanfirmasi alamat surel")
                                    .setMessage("Anda yakin ingin mengganti alamat surel anda menjadi \"" + newEmail + "\" ?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            updateEmail(newEmail);
                                        }})
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                        } else {
                            toast(getContext(), "Mohon masukkan alamat surel baru");
                        }
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void updateEmail(final String newEmail) {
        progressDialog.setTitle("Memeriksa validitas akun");
        progressDialog.setMessage("Sedang memverifikasi & memeriksa validitas akun anda");
        progressDialog.show();
        // temp old email
        final String oldEmail = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
        // get current password
        firebaseFirestore
                .collection(DB_USER_ACCOUNT_INFORMATION)
                .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                final String password = documentSnapshot.getString("password");
                                if (password != null && !password.isEmpty()) {
                                    // reauthenticate
                                    AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()), password);
                                    firebaseAuth.getCurrentUser().reauthenticate(credential)
                                            // reauth success
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // update email
                                                    firebaseAuth.getCurrentUser().updateEmail(newEmail)
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.dismiss();
                                                                    toast(getContext(), "Alamat surel ini telah digunakan");
                                                                }
                                                            })
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    // update email in database
                                                                    progressDialog.setTitle("Mengubah alamat surel");
                                                                    progressDialog.setMessage("Sedang mengubah alamat surel anda");
                                                                    firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION)
                                                                            .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                                                                            .update("email", newEmail)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    progressDialog.dismiss();
                                                                                    toast(getContext(), "Berhasil mengubah alamat surel");
                                                                                    email.setText(newEmail);
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    if (oldEmail != null) firebaseAuth.getCurrentUser().updateEmail(oldEmail);
                                                                                    progressDialog.dismiss();
                                                                                    toast(getContext(), "Terjadi kesalahan saat mengganti alamat surel");
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    toast(getContext(), "Terjadi kesalahan saat mangautentikasi akun");
                                                }
                                            });
                                } else {
                                    progressDialog.dismiss();
                                    toast(getContext(), "Terjadi kesalahan saat memeriksa validitas akun");
                                }
                            } else {
                                progressDialog.dismiss();
                                toast(getContext(), "Terjadi kesalahan saat memeriksa validitas akun");
                            }
                        } else {
                            progressDialog.dismiss();
                            toast(getContext(), "Terjadi kesalahan saat memeriksa validitas akun");
                        }
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        toast(getContext(), "Terjadi kesalahan saat memeriksa validitas akun");
                    }
                });
    }

    private void changePassword() {
        progressDialog.setTitle("Memeriksa validitas akun");
        progressDialog.setMessage("Sedang memverifikasi & memeriksa validitas akun");
        progressDialog.show();
        // get current password
        firebaseFirestore
                .collection(DB_USER_ACCOUNT_INFORMATION)
                .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                final String currentPassword = documentSnapshot.getString("password");
                                progressDialog.dismiss();
                                if (currentPassword != null && !currentPassword.isEmpty()) {
                                    new AlertDialog
                                            .Builder(Objects.requireNonNull(getContext()))
                                            .setIcon(null)
                                            .setTitle("Ubah kata sandi")
                                            .setView(changePasswordDialog)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    final String getOldPassword = edtChangePasswordOld.getText().toString();
                                                    final String getNewPassword = edtChangePasswordNew.getText().toString();
                                                    String getNewPasswordConfirm = edtChangePasswordNewConfirm.getText().toString();
                                                    if (getOldPassword.equals(currentPassword)) {
                                                        if (getNewPassword.equals(getNewPasswordConfirm) && getNewPassword.length() >= 6 ) {
                                                            new AlertDialog
                                                                    .Builder(Objects.requireNonNull(getContext()))
                                                                    .setIcon(null)
                                                                    .setTitle("Konfirmasi kata sandi")
                                                                    .setMessage("Anda yakin ingin mengganti kata sandi akun anda?")
                                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                                            updatePassword(getNewPassword, getOldPassword);
                                                                        }})
                                                                    .setNegativeButton(android.R.string.no, null)
                                                                    .show();
                                                        } else if (getNewPassword.equals(getNewPasswordConfirm) && getNewPassword.length() <= 6) {
                                                            toast(getContext(), "Kata sandi baru harus lebih dari 6 karakter");
                                                        } else {
                                                            toast(getContext(), "Kata sandi baru tidak sama, silahkan periksa kembali");
                                                        }
                                                    } else {
                                                        toast(getContext(), "Kata sandi lama tidak cocok dengan akun anda");
                                                    }
                                                }})
                                            .setNegativeButton(android.R.string.no, null)
                                            .show();
                                } else {
                                    toast(getContext(), "Terjadi kesalahan saat memeriksa validitas akun");
                                }
                            } else {
                                progressDialog.dismiss();
                                toast(getContext(), "Terjadi kesalahan saat memeriksa validitas akun");
                            }
                        }
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        toast(getContext(), "Terjadi kesalahan saat memeriksa validitas akun");
                    }
                });
    }

    private void updatePassword(final String newPassword, final String oldPassword) {
        progressDialog.setTitle("Mengubah kata sandi");
        progressDialog.setMessage("Sedang mengubah kata sandi akun anda");
        progressDialog.show();
        // reauthenticate
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()), oldPassword);
        firebaseAuth.getCurrentUser().reauthenticate(credential)
                // reauth success
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // update password
                        firebaseAuth.getCurrentUser().updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // update password in database
                                        firebaseFirestore.collection(DB_USER_ACCOUNT_INFORMATION)
                                                .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                                                .update("password", newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.dismiss();
                                                        toast(getContext(), "Berhasil mengubah kata sandi");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        firebaseAuth.getCurrentUser().updatePassword(oldPassword);
                                                        progressDialog.dismiss();
                                                        toast(getContext(), "Terjadi kesalahan saat mengubah kata sandi");
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        toast(getContext(), "Terjadi kesalahan saat mengubah kata sandi");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        toast(getContext(), "Terjadi kesalahan saat mengubah kata sandi");
                    }
                });
    }
}

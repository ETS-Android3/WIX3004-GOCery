package com.example.gocery.manageprofiles;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gocery.R;
import com.example.gocery.manageprofiles.dao.DAOProfile;
import com.example.gocery.manageprofiles.model.UserProfile;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.util.HashMap;
import com.bumptech.glide.Glide;

public class EditProfileFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DAOProfile daoProfile = new DAOProfile();
    UserProfile userProfile;

    TextInputEditText ETUsername;
    CheckBox CBAdmin;
    CheckBox CBRep;
    SwitchMaterial SWLock;
    TextInputLayout TILPassword;
    TextInputEditText ETPassword;

    final int REQUEST_IMAGE = 999;
    Uri imageUri;
    ImageView IVProfileEdit;
    String imageURL;

    Button BTNSave;
    Button BTNDelete;
    Boolean isAdmin = false;
    Boolean isRep = false;
    Boolean isChanged;

    String profileKey;

    ProgressDialog progressDialog;
    StorageReference storageReference;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ETUsername = view.findViewById(R.id.ETAddress);
        CBAdmin = view.findViewById(R.id.CBAdmin);
        CBRep = view.findViewById(R.id.CBRep);
        SWLock = view.findViewById(R.id.SWLock);
        ETPassword = view.findViewById(R.id.ETPassword);
        TILPassword = view.findViewById(R.id.TILPassword);
        BTNSave = view.findViewById(R.id.BTNSave);
        BTNDelete = view.findViewById(R.id.BTNDelete);
        IVProfileEdit = view.findViewById(R.id.IVProfileEdit);
        storageReference = FirebaseStorage.getInstance().getReference();
        isChanged = false;


        getParentFragmentManager().setFragmentResultListener("editProfile", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                setProfileData(""+result.get("PROFILE_KEY"));
            }
        });

        CBAdmin.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            isAdmin = isChecked;
        });

        CBRep.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            isRep = isChecked;
        });

        SWLock.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                TILPassword.setVisibility(view.VISIBLE);
                ETPassword.setVisibility(view.VISIBLE);
            }else{
                TILPassword.setVisibility(view.GONE);
                ETPassword.setVisibility(view.GONE);
            }
        });

        BTNSave.setOnClickListener(v-> {
            updateProfileData(v);
        });

        BTNDelete.setOnClickListener(v -> {
            deleteProfile(v);
        });

        IVProfileEdit.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_IMAGE);
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    private void setProfileData(String profileKey){
        this.profileKey = profileKey;
        if(profileKey != null){
            daoProfile.getSingle(profileKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userProfile = snapshot.getValue(UserProfile.class);
                    ETUsername.setText(userProfile.getUsername());
                    CBAdmin.setChecked(userProfile.isAdmin());
                    CBRep.setChecked(userProfile.isRepresentative());

                    storageReference.child(userProfile.getPath()).getDownloadUrl().addOnSuccessListener(suc-> {
                        Glide.with(getContext())
                                .load(suc)
                                .placeholder(R.drawable.spinning_loading)
                                .error(R.drawable.gocery_logo_only)
                                .into(IVProfileEdit);
                    }).addOnFailureListener(err -> {
                        System.out.println(err);
                    });
                    if(!userProfile.getPassword().isEmpty()){
                        SWLock.setChecked(true);
                        ETPassword.setVisibility(getView().VISIBLE);
                        TILPassword.setVisibility(getView().VISIBLE);
                        ETPassword.setText(userProfile.getPassword());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void updateProfileData(View view){
        String username = ETUsername.getText().toString();
        String password = ETPassword.getText().toString();
        if(TextUtils.isEmpty(username)){
            ETUsername.setError("Username cannot be empty");
        }else if(SWLock.isChecked() && TextUtils.isEmpty(password)) {
            ETPassword.setError("Pin must not be empty");
        }else{
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Updating profile...");
            progressDialog.show();
            String userID = mAuth.getCurrentUser().getUid();
            String currentUser = "user_"+userID;
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String fileName = "member_profile/"+currentUser+"/CREATOR"+ts.getTime();

            HashMap<String, Object> hashMap = new HashMap<>();

            DAOProfile daoProfile = new DAOProfile();
            if(isChanged){
                storageReference = FirebaseStorage.getInstance().getReference(fileName);
                storageReference.putFile(imageUri).addOnSuccessListener(suc -> {
                    hashMap.put("username", username);
                    hashMap.put("password", password);
                    hashMap.put("representative", isRep);
                    hashMap.put("admin", isAdmin);
                    hashMap.put("path", fileName);

                    daoProfile.update(profileKey, hashMap).addOnSuccessListener(v -> {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
//                Navigation.findNavController(view).navigate(R.id.DestHousehold);
                        Navigation.findNavController(view).navigate(R.id.DestManageHousehold2);
                    }).addOnFailureListener(err -> {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(er -> {
                    Toast.makeText(getActivity(), "Upload failed image", Toast.LENGTH_SHORT).show();
                });
            }else{
                hashMap.put("username", username);
                hashMap.put("password", password);
                hashMap.put("representative", isRep);
                hashMap.put("admin", isAdmin);
                daoProfile.update(profileKey, hashMap).addOnSuccessListener(v -> {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
//                Navigation.findNavController(view).navigate(R.id.DestHousehold);
                    Navigation.findNavController(view).navigate(R.id.DestManageHousehold2);
                }).addOnFailureListener(err -> {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getActivity(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

        }
    }

    private void deleteProfile(View view){
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Remove member")
                .setMessage("Are you sure you want to remove "+ETUsername.getText().toString()+"?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    int totalMember = 0;
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        daoProfile.get().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                totalMember = (int) snapshot.getChildrenCount();
                                if(totalMember <= 1){
                                    Toast.makeText(getActivity(), totalMember+" Unable to remove member, households cannot be empty", Toast.LENGTH_LONG).show();
                                }else{
                                    daoProfile.remove(profileKey).addOnSuccessListener(suc -> {
                                        Toast.makeText(getActivity(), "Member removed successfully", Toast.LENGTH_SHORT).show();
//                                        Navigation.findNavController(view).navigate(R.id.DestHousehold);
                                        Navigation.findNavController(view).navigate(R.id.DestManageHousehold2);
                                    }).addOnFailureListener(err -> {
                                        Toast.makeText(getActivity(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE && data != null && data.getData() != null){
            imageUri = data.getData();
            IVProfileEdit.setImageURI(imageUri);
            isChanged = true;
        }
//        else{
//            imageUri = null;
//            IVProfileEdit.setImageURI(null);
//        }
    }
}
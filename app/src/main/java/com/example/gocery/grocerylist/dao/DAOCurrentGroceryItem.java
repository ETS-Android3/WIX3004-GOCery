package com.example.gocery.grocerylist.dao;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class DAOCurrentGroceryItem {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String currentUser;

    public DAOCurrentGroceryItem() {
        currentUser = "user_"+FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        databaseReference = db.getReference(GroceryItem.class.getSimpleName());

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }


    public Task<Void> add(GroceryItem groceryItem, @Nullable Uri imageURI)
    {
        String creatorName = "CREATOR_NAME_HERE";
        groceryItem.setCreatedBy(creatorName);

        if(imageURI != null){
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String fileName = "grocery_image/"+currentUser+"/"+creatorName+"-"+ts.getTime();

            storageReference = FirebaseStorage.getInstance().getReference(fileName);
            storageReference.putFile(imageURI);
            groceryItem.setImage(fileName);
            return databaseReference.child(currentUser).push().setValue(groceryItem);
        }else{
            return databaseReference.child(currentUser).push().setValue(groceryItem);
        }

    }

    public Task<Void> add(GroceryItem groceryItem)
    {
        return databaseReference.child(currentUser).push().setValue(groceryItem);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap, @Nullable String imagePath){

        //update image
        if(imagePath != null){
            storageReference.child(hashMap.get("image").toString()).delete();
            hashMap.put("image", imagePath);
        }

        return databaseReference.child(currentUser).child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key, @Nullable String imagePath) {

        // remove image
        if(imagePath != null){
            storageReference.child(imagePath).delete();
        }

        return databaseReference.child(currentUser).child(key).removeValue();
    }

    public Query get() {
        return databaseReference.child(currentUser);
    }

    public Query getSingle(String item_key){
        return databaseReference.child(currentUser).child(item_key);
    }






}

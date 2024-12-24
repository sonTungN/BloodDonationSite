package com.sontung.blood.repo;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sontung.blood.callback.FirebaseCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ImageRepository {
    private Context context;
    
    private final FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    
    private MutableLiveData<List<String>> imageUrlsFromCallback;
    
    private final List<String> imageUrls;
    private String profileImgUrl;
    
    public ImageRepository(Context context) {
        this.context = context;
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();
        
        this.imageUrlsFromCallback = new MutableLiveData<>();
        this.imageUrls = new ArrayList<>();
    }
    
    public void uploadSiteImageToStorage(
            List<Uri> images,
            String parent,
            FirebaseCallback<String> callback
    ) {
        if (images.isEmpty()) {
            return;
        }
        
        StorageReference childRef = storageReference.child(parent);
        
        imageUrls.clear();
        images.forEach(image -> {
            long index = new Date().getTime();
            
            StorageReference imageRef = childRef.child(String.valueOf(index));
            UploadTask uploadTask = imageRef.putFile(image);
            
            uploadTask
                    .continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                return imageRef.getDownloadUrl();
                            })
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.d("LINK", String.valueOf(downloadUri));
                            
                            imageUrls.add(String.valueOf(downloadUri));
                            
                            if (imageUrls.size() == images.size()){
                                callback.onSuccess(imageUrls);
                            }
                        } else {
                            Log.d(TAG, "The bug is that " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        });
    }
    
    public void uploadUserAvatarToStorage (
            Uri imageUri,
            String parent,
            FirebaseCallback<String> callback
    ) {
        long index = new Date().getTime();
        StorageReference imageRef =
                storageReference
                        .child(parent)
                        .child(String.valueOf(index));
        
        UploadTask uploadTask = imageRef.putFile(imageUri);
        
        uploadTask
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return imageRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d("LINK", String.valueOf(downloadUri));
                        
                        profileImgUrl = String.valueOf(downloadUri);
                        callback.onSuccess(profileImgUrl);
                        
                    } else {
                        Log.d(TAG, "The bug is that " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }
    
    
}

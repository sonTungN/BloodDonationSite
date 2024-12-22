package com.sontung.blood.repo;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ImageRepository {
    private Context context;
    
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    private MutableLiveData<List<String>> imageUrlsFromCallback;
    
    private final List<String> imageUrls = new ArrayList<>();
    
    public ImageRepository(Context context) {
        this.context = context;
        imageUrlsFromCallback = new MutableLiveData<>();
    }
    
    public MutableLiveData<List<String>> uploadImageToStorage(List<Uri> images, String parent) {
        if (images.isEmpty()) {
            return new MutableLiveData<>();
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
                            imageUrlsFromCallback.setValue(imageUrls);
                        } else {
                            Log.d(TAG, "The bug is that " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        });
        
        return imageUrlsFromCallback;
    }
}

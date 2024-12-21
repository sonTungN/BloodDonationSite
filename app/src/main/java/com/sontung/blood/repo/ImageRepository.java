package com.sontung.blood.repo;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ImageRepository {
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference;
    
    private final List<String> imageUrls = new ArrayList<>();
    
    public void uploadImageToStorage(List<Uri> images, String parent) {
        if (images.isEmpty()) {
            return;
        }
        
        storageReference = firebaseStorage.getReference().child(parent);
        images.forEach(image -> {
            UUID uuid = UUID.randomUUID();
            StorageReference imageRef = storageReference.child(uuid.toString());
            
            UploadTask uploadTask = imageRef.putFile(image);
            uploadTask
                    .continueWithTask(
                            new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                                        throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw Objects.requireNonNull(task.getException());
                                    }
                                    return imageRef.getDownloadUrl();
                                }
                            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.d("link", String.valueOf(downloadUri));
                                
                                imageUrls.add(String.valueOf(downloadUri));
                            } else {
                                Log.d(TAG, "The bug is that " + Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });
        });
    }
}

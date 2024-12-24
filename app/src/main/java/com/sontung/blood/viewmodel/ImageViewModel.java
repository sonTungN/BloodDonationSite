package com.sontung.blood.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.repo.ImageRepository;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    private final ImageRepository imageRepository;
    
    public ImageViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(getApplication());
    }
    
    public void uploadSiteImageToStorage(List<Uri> images, String parent, FirebaseCallback<String> callback) {
        imageRepository.uploadSiteImageToStorage(images, parent, callback);
    }
    
    public void uploadUserAvatarToStorage(Uri imageUri, String parent, FirebaseCallback<String> callback) {
        imageRepository.uploadUserAvatarToStorage(imageUri, parent, callback);
    }
}

package com.sontung.blood.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sontung.blood.repo.ImageRepository;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    private final ImageRepository imageRepository;
    
    public ImageViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(getApplication());
    }
    
    public MutableLiveData<List<String>> uploadImageToStorage(List<Uri> images, String parent) {
        return imageRepository.uploadImageToStorage(images, parent);
    }
}

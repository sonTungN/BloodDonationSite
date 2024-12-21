package com.sontung.blood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sontung.blood.model.User;
import com.sontung.blood.repo.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    
    public UserViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application.getApplicationContext());
    }
    
    public void signUpUserWithEmailAndPassword(
            final String email,
            final String password,
            final String displayName,
            final String bloodType
    ) {
        userRepository.signUpUserWithEmailAndPassword(email, password, displayName, bloodType);
    }

    public void signInUserWithEmailAndPassword(
            final String email,
            final String password
    ) {
        userRepository.signInUserWithEmailAndPassword(email, password);
    }
    
    public MutableLiveData<User> getUserDataById(String userId) {
        return userRepository.getUserDataById(userId);
    }
    
    public User convertUserMutableLiveDataToModelClass(String userId) {
        return userRepository.convertUserMutableLiveDataToModelClass(userId);
    }
    
    public String getCurrentUserId() {
        return userRepository.getCurrentUserId();
    }
    
    public void signOut() {
        userRepository.signOut();
    }
}

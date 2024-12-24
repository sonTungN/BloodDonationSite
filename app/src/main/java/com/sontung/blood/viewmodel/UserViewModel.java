package com.sontung.blood.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FieldValue;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.model.User;
import com.sontung.blood.repo.UserRepository;

import java.util.Objects;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    
    public UserViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application.getApplicationContext());
    }
    
    /*
    public void signUpUserWithEmailAndPassword(
            final String email,
            final String password,
            final String displayName,
            final String bloodType
    ) {
        userRepository.signUpUserWithEmailAndPassword(email, password, displayName, bloodType);
    }
     */
    public void signUpUser(User user, FirebaseCallback<User> callback) {
        userRepository.signUpUser(user, callback);
    }

    public void signInUserWithEmailAndPassword(
            final String email,
            final String password
    ) {
        userRepository.signInUserWithEmailAndPassword(email, password);
    }
    
    public void updateUserId(String userId, User updateduser) {
        userRepository.updateUserId(userId, updateduser);
    }
    
    public void updateUserProfileAvatar(String userId, User updatedUser) {
        userRepository.updateUserProfileAvatar(userId, updatedUser);
    }
    
    public MutableLiveData<User> getCurrentUser() {
        return userRepository.getCurrentUser();
    }
    
    public String getCurrentUserId() {
        return userRepository.getCurrentUserId();
    }
    
    public MutableLiveData<User> getUserDataById(String userId) {
        return userRepository.getUserDataById(userId);
    }
    
    public void addCurrentUserRegisteredSite(String siteId, FirebaseCallback<Boolean> callback) {
        userRepository.addCurrentUserRegisteredSite(siteId, callback);
    }
    
    public void addCurrentUserVolunteerSite(String siteId, FirebaseCallback<Boolean> callback) {
        userRepository.addCurrentUserVolunteerSite(siteId, callback);
    }
    
    public void signOut() {
        userRepository.signOut();
    }
}

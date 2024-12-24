package com.sontung.blood.repo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.model.Site;
import com.sontung.blood.model.User;
import com.sontung.blood.preference.LocalStorageManager;
import com.sontung.blood.shared.Paths;
import com.sontung.blood.views.HomeActivity;
import com.sontung.blood.views.SignInActivity;

import java.util.Objects;

import okhttp3.MediaType;

public class UserRepository {
    private final Context context;
    private final FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    
    private final FirebaseFirestore db;
    private final CollectionReference userCollection;
    
    private final LocalStorageManager manager;
    
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<User> currentUserData = new MutableLiveData<>();
    
    public static final MediaType JSON = MediaType.get("application/json");

    public UserRepository(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        
        this.db = FirebaseFirestore.getInstance();
        this.userCollection = db.collection(Paths.USER_COLLECTION_PATH);
        
        this.manager = new LocalStorageManager(context);
    }
    
    public void signUpUser(User user, FirebaseCallback<User> callback) {
        firebaseAuth
                .createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    currentUser = firebaseAuth.getCurrentUser();
                    
                    if (task.isSuccessful()) {
                        String userId = currentUser.getUid();
                        user.setUserId(userId);
                        callback.onSuccess(user);
                        
                        userCollection
                                .document(userId)
                                .set(user);
//                                .addOnSuccessListener(documentReference -> {
//                                    Toast.makeText(context, "Adding user to FireStore successfully", Toast.LENGTH_SHORT).show();
//                                })
//                                .addOnFailureListener(e -> {
//                                    Log.d("CREATE", "Create User failed!");
//                                    Toast.makeText(context, "Failed to store new user to FireStore", Toast.LENGTH_SHORT).show();
//                                });
                    }
                })
                .addOnFailureListener(exception -> {
                    Log.d("REGISTER", exception.getMessage() != null ? exception.getMessage() : "Error");
                    Toast.makeText(context, "Register Status: FAILED", Toast.LENGTH_SHORT).show();
                });
    }

    /*
    public void signUpUserWithEmailAndPassword(
            final String email,
            final String password,
            final String displayName,
            final String bloodType
    ) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        currentUser = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();

                        if (currentUser != null) {
                            currentUser.updateProfile(profileUpdate)
                                    .addOnCompleteListener(updateTask -> {
                                        if (!updateTask.isSuccessful()) {
                                            Log.d("REGISTER", "Register with display name failed");
                                        }
                                    });

                            if (task.isSuccessful()) {
                                String currentUserId = currentUser.getUid();
                                User userObject =
                                        User.builder()
                                            .userId(currentUserId)
                                            .username(displayName)
                                            .email(email)
                                            .password(password)
                                            .bloodType(bloodType)
                                            .build();

                                userCollection
                                        .document(currentUserId)
                                        .set(userObject);

                                Intent intent = new Intent(context, SignInActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);

                                Toast.makeText(context, "Register Status: SUCCESS", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(exception -> {
                        Log.d("REGISTER", exception.getMessage() != null ? exception.getMessage() : "Error");
                        Toast.makeText(context, "Register Status: FAILED", Toast.LENGTH_SHORT).show();
                    });
        }
     */
        
    public void signInUserWithEmailAndPassword(String email, String password) {
        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser = firebaseAuth.getCurrentUser();
                        
                        userCollection
                                .document(currentUser.getUid())
                                .get()
                                        .addOnCompleteListener(task1 -> {
                                            DocumentSnapshot snapshot = task1.getResult();
                                            User user = snapshot.toObject(User.class);
                                            user.setUserId(currentUser.getUid());
                                            
                                            manager.setCurrentUser(user);
                                        });
                        
                        Toast.makeText(context, "Login Status: SUCCESS", Toast.LENGTH_SHORT).show();
                        
                        Intent intent = new Intent(context, HomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(context, "Login Status: FAILED", Toast.LENGTH_SHORT).show();
                    Log.d("LOGIN", exception.getMessage() != null ? exception.getMessage() : "Error");
                });
    }
    
    public void updateUserId(String userId, User updatedUser) {
        userCollection
                .document(userId)
                .update("userId", currentUser.getUid())
                .addOnSuccessListener(e -> {
                    Toast.makeText(context, "User ID updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("SITE: UPDATE ID ERROR", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(context, "Failed to update user id", Toast.LENGTH_SHORT).show();
                });
    }
    
    public void updateUserProfileAvatar(String userId, User updatedUser) {
        userCollection
                .document(userId)
                .update("profileUrl", updatedUser.getProfileUrl())
                .addOnSuccessListener(e -> {
                    Toast.makeText(context, "User profile image updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("USER: UPDATE ERROR", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(context, "Failed to update user profile image", Toast.LENGTH_SHORT).show();
                });
    }
    
    public MutableLiveData<User> getCurrentUser() {
        return getUserDataById(getCurrentUserId());
    }
    
    public String getCurrentUserId() {
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }
    
    public MutableLiveData<User> getUserDataById(String userId) {
        userCollection
            .document(userId)
            .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User targetUser = null;
                    
                    if (documentSnapshot.exists()) {
                        targetUser = documentSnapshot.toObject(User.class);
                        
                    } else {
                        Log.d("USER: FETCH ERROR", "Document not found!");
                    }
                    userData.postValue(targetUser);
                })
                
                .addOnFailureListener(e -> {
                    Log.d("USER: FETCH ERROR", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(context, "USERS DOCUMENT: ERROR", Toast.LENGTH_SHORT).show();
                });
        
       return userData;
    }
    
    public void addCurrentUserRegisteredSite(String siteId, FirebaseCallback<Boolean> callback) {
        userCollection
                .document(getCurrentUserId())
                .update("listOfRegisteredSites", FieldValue.arrayUnion(siteId))
                .addOnSuccessListener(unused -> callback.onSuccess(true))
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "SiteId can't add into User registered list!", Toast.LENGTH_SHORT).show();
                    callback.onFailure(true);
                });
    }
    
    public void addCurrentUserVolunteerSite(String siteId, FirebaseCallback<Boolean> callback) {
        userCollection
                .document(getCurrentUserId())
                .update("listOfVolunteerSites", FieldValue.arrayUnion(siteId))
                .addOnSuccessListener(unused -> callback.onSuccess(true))
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "SiteId can't add into User volunteer list!", Toast.LENGTH_SHORT).show();
                    callback.onFailure(true);
                });
    }
    
    public void signOut() {
        Toast.makeText(context, "LOG OUT", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        manager.clearEditor();
    }
}

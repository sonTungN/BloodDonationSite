package com.sontung.blood.repo;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.model.User;
import com.sontung.blood.preference.LocalStorageManager;
import com.sontung.blood.shared.Paths;
import com.sontung.blood.views.HomeActivity;
import com.sontung.blood.views.SignInActivity;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;

public class UserRepository {
    private final Context context;
    private final FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    
    private final FirebaseFirestore db;
    private final CollectionReference collection;
    
    private final LocalStorageManager manager;
    
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<User> currentUserData = new MutableLiveData<>();
    
    public static final MediaType JSON = MediaType.get("application/json");

    public UserRepository(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        
        this.db = FirebaseFirestore.getInstance();
        this.collection = db.collection(Paths.USER_COLLECTION_PATH);
        
        this.manager = new LocalStorageManager(context);
    }

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

                                collection
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
        
    public void signInUserWithEmailAndPassword(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser = firebaseAuth.getCurrentUser();
                        
                        collection
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
    
    public MutableLiveData<User> getCurrentUser() {
        return getUserDataById(getCurrentUserId());
    }
    
    public String getCurrentUserId() {
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }
    
    public MutableLiveData<User> getUserDataById(String userId) {
        collection
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
    
    public void updateUserHostSiteId(String siteId) {
        collection
                .document(getCurrentUserId())
                .update("hostedSite", siteId)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Can not update user ID when create site", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Can not update user ID when create site", Toast.LENGTH_SHORT).show();
                    Log.d("CREATE: Cant update user ID, Error: " + e.getMessage(), e.getMessage() != null ? e.getMessage() : "Error");
                });
    }
    
    public void signOut() {
        Toast.makeText(context, "LOG OUT", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        manager.clearEditor();
    }
}

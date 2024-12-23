package com.sontung.blood.repo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.model.Site;
import com.sontung.blood.model.User;
import com.sontung.blood.preference.LocalStorageManager;
import com.sontung.blood.shared.Paths;
import com.sontung.blood.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SiteRepository {
    private final Context context;
    
    private final FirebaseFirestore db;
    private final CollectionReference siteCollection;
    private final CollectionReference userCollection;
    
    private LocalStorageManager manager;
    
    private final MutableLiveData<Site> siteData = new MutableLiveData<>();
    private final MutableLiveData<List<Site>> allSiteListData = new MutableLiveData<>();
    
    private final MutableLiveData<List<Site>> userRegisteredSite = new MutableLiveData<>();
    private final MutableLiveData<List<Site>> userVolunteerSite = new MutableLiveData<>();
    private final MutableLiveData<Site> userHostedSite = new MutableLiveData<>();
    
    public SiteRepository(Context context) {
        this.context = context;
        
        this.db = FirebaseFirestore.getInstance();
        this.siteCollection = db.collection(Paths.SITE_COLLECTION_PATH);
        this.userCollection = db.collection(Paths.USER_COLLECTION_PATH);
        
        this.manager = new LocalStorageManager(context);
    }
    
    public MutableLiveData<List<Site>> getAllSiteData() {
        List<Site> siteList = new ArrayList<>();
        
        siteCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                        Site site = snapshot.toObject(Site.class);
                        siteList.add(site);
                    }
                    allSiteListData.postValue(siteList);
                })
                
                .addOnFailureListener(e -> {
                    Log.d("SITE: FETCH ERROR", Objects.requireNonNull(e.getMessage()));
                });
                
        return allSiteListData;
    }
    
    public MutableLiveData<Site> getSiteDataById(String siteId) {
        siteCollection
                .document(siteId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Site targetSite = null;
                    
                    if (documentSnapshot.exists()) {
                        targetSite = documentSnapshot.toObject(Site.class);
                        
                    } else {
                        Log.d("USER: FETCH ERROR", "Document not found!");
                    }
                    
                    siteData.postValue(targetSite);
                })
                .addOnFailureListener(e -> {
                    Log.d("SITE: FETCH ERROR", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(context, "SITE DOCUMENT: ERROR", Toast.LENGTH_SHORT).show();
                });
        return siteData;
    }
    
    public MutableLiveData<List<Site>> getUserRegisteredSite(String userId) {
        userCollection
                .document(userId)
                .get()
                .addOnSuccessListener(userDocumentSnapshot -> {
                    List<Site> registeredList = new ArrayList<>();
                    
                    if (userDocumentSnapshot.exists()) {
                        User currentUser = userDocumentSnapshot.toObject(User.class);
                        
                        if (currentUser != null) {
                            List<String> listOfRegisteredSiteId = currentUser.getListOfRegisteredSites();
                            
                            for (String siteId: listOfRegisteredSiteId) {
                                siteCollection
                                        .document(siteId)
                                        .get()
                                        .addOnSuccessListener(siteDocumentSnapshot -> {
                                            Site site;
                                            
                                            if (siteDocumentSnapshot.exists()) {
                                                site = siteDocumentSnapshot.toObject(Site.class);
                                                registeredList.add(site);
                                            } else {
                                                Log.d("SITE", "Document not found!");
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.d("SITE: Register Site Fetch Error", Objects.requireNonNull(e.getMessage()));
                                        
                                        });
                            }
                            userRegisteredSite.postValue(registeredList);
                            
                        } else {
                            Log.d("USER", "User is null");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("USER: Register Site Fetch Error", Objects.requireNonNull(e.getMessage()));
                });
        return userRegisteredSite;
    }
    
    public MutableLiveData<List<Site>> getUserVolunteerSite(String userId) {
        userCollection
                .document(userId)
                .get()
                .addOnSuccessListener(userDocumentSnapshot -> {
                    List<Site> volunteerSite = new ArrayList<>();
                    
                    if (userDocumentSnapshot.exists()) {
                        User currentUser = userDocumentSnapshot.toObject(User.class);
                        
                        if (currentUser != null) {
                            List<String> listOfVolunteerSiteId = currentUser.getListOfVolunteerSites();
                            
                            for (String siteId: listOfVolunteerSiteId) {
                                siteCollection
                                        .document(siteId)
                                        .get()
                                        .addOnSuccessListener(siteDocumentSnapshot -> {
                                            Site site;
                                            
                                            if (siteDocumentSnapshot.exists()) {
                                                site = siteDocumentSnapshot.toObject(Site.class);
                                                volunteerSite.add(site);
                                            } else {
                                                Log.d("SITE", "Document not found!");
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.d("SITE: Volunteer Site Fetch Error", Objects.requireNonNull(e.getMessage()));
                                            
                                        });
                            }
                            userVolunteerSite.postValue(volunteerSite);
                            
                        } else {
                            Log.d("USER", "User is null");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("USER: Volunteer Site Fetch Error", Objects.requireNonNull(e.getMessage()));
                });
        return userVolunteerSite;
    }
    
    public MutableLiveData<Site> getUserHostedSite(String userId) {
        userCollection
                .document(userId)
                .get()
                .addOnSuccessListener(userDocumentSnapshot -> {
                    if (userDocumentSnapshot.exists()) {
                        User currentUser = userDocumentSnapshot.toObject(User.class);

                        if (currentUser != null) {
                            String siteId = currentUser.getHostedSite();
                            
                            if (siteId == null || siteId.isEmpty()) {
                                userHostedSite.postValue(null);
                                return;
                            }
                            
                            siteCollection
                                    .document(siteId)
                                    .get()
                                    .addOnSuccessListener(siteDocumentSnapshot -> {
                                        Site hostedSite = null;
                                        if (siteDocumentSnapshot.exists()) {
                                            hostedSite = siteDocumentSnapshot.toObject(Site.class);
                                        }
                                        
                                        userHostedSite.postValue(hostedSite);
                                    });
                        } else {
                            Log.d("USER", "User is null");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("USER: Host Site Fetch Error", Objects.requireNonNull(e.getMessage()));
                });
        return userHostedSite;
    }
    
    public void createNewSite(Site site, FirebaseCallback<Site> callback) {
        siteCollection
                .add(site)
                .addOnSuccessListener(documentReference -> {
                    String siteId = documentReference.getId();
                    site.setSiteId(siteId);
                    callback.onSuccess(site);
                    
                    userCollection
                            .document(site.getHost())
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
                })
                .addOnFailureListener(e -> {
                    Log.d("CREATE", "Create Site failed!");
                    Toast.makeText(context, "Failed to create new site", Toast.LENGTH_SHORT).show();
                });
    }
    
    public void updateSiteId(String siteId, Site updateSite) {
        siteCollection
                .document(siteId)
                .update("siteId", updateSite.getSiteId())
                .addOnSuccessListener(e -> {
                    Toast.makeText(context, "Site images updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("SITE: UPDATE ID ERROR", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(context, "Failed to update site id", Toast.LENGTH_SHORT).show();
                });
    }
    
    public void updateSiteImages(String siteId, Site updatedSite) {
        siteCollection
                .document(siteId)
                .update("siteImageUrl", updatedSite.getSiteImageUrl())
                .addOnSuccessListener(e -> {
                    Toast.makeText(context, "Site images updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("SITE: UPDATE ERROR", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(context, "Failed to update site images", Toast.LENGTH_SHORT).show();
                });
    }
    
    public void addUserIntoSiteRegisteredList(String userId, String siteId, FirebaseCallback<Boolean> callback) {
        siteCollection
                .document(siteId)
                .update("listOfDonors", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(unused -> callback.onSuccess(true))
                .addOnFailureListener(e -> {
                    Log.e("UserId can't add into Site donor list.", Objects.requireNonNull(e.getMessage()));
                    callback.onFailure(true);
                });
    }
    
    public void addUserIntoSiteVolunteerList(String userId, String siteId, FirebaseCallback<Boolean> callback) {
        siteCollection
                .document(siteId)
                .update("listOfVolunteers", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(unused -> callback.onSuccess(true))
                .addOnFailureListener(e -> {
                    Log.e("UserId can't add into Site volunteer list.", Objects.requireNonNull(e.getMessage()));
                    callback.onFailure(true);
                });
    }
}

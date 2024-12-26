package com.sontung.blood.repo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.model.Notification;
import com.sontung.blood.model.Report;
import com.sontung.blood.shared.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationRepository {
    private final Context context;
    
    private final FirebaseFirestore db;
    private final CollectionReference userCollection;
    private final CollectionReference notificationCollection;
    
    private final MutableLiveData<Notification> notificationData = new MutableLiveData<>();
    private final MutableLiveData<List<Notification>> notificationMutableList = new MutableLiveData<>();
    
    public NotificationRepository(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.userCollection = db.collection(Paths.USER_COLLECTION_PATH);
        this.notificationCollection = db.collection(Paths.NOTIFICATION_COLLECTION_PATH);
    }
    
    public void createNotification(Notification notification, FirebaseCallback<Notification> callback) {
        notificationCollection
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    String notificationId = documentReference.getId();
                    notification.setNotificationId(notificationId);
                    callback.onSuccess(notification);
                    
                    userCollection
                            .document(notification.getReceiverId())
                            .update("listOfNotifications", FieldValue.arrayUnion(notificationId))
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(context, "Can not update notificationId into user listOfNotifications", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Can not update notificationId into user listOfNotifications", Toast.LENGTH_SHORT).show();
                                Log.d("NOTIFICATION: Cant update notificationId into listOfNotifications, Error: " + e.getMessage(), e.getMessage() != null ? e.getMessage() : "Error");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("CREATE", "Create Notification failed!");
                    Toast.makeText(context, "Failed to create new notification", Toast.LENGTH_SHORT).show();
                });
    }
    
    public void updateNotificationId(String notificationId, Notification updateNotification) {
        notificationCollection
                .document(notificationId)
                .update("notificationId", updateNotification.getNotificationId())
                .addOnSuccessListener(e -> {
                    Toast.makeText(context, "Notification ID updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("NOTIFICATION: UPDATE ID ERROR", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(context, "Failed to update notification id", Toast.LENGTH_SHORT).show();
                });
    }
    
    public MutableLiveData<List<Notification>> getNotificationDataByReceiverId(String receiverId) {
        notificationCollection
                .whereEqualTo("receiverId", receiverId)
                .orderBy("sentDate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> notificationList = new ArrayList<>();
                        
                        if (task.getResult() != null) {
                            for (DocumentSnapshot snapshot: task.getResult()) {
                                Notification notification = snapshot.toObject(Notification.class);
                                if (notification != null) {
                                    notificationList.add(notification);
                                }
                            }
                            
                            notificationMutableList.postValue(notificationList);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("NOTIFICATION", "Error getting notifications: " + e.getMessage());
                    Toast.makeText(context, "Failed to fetch notifications", Toast.LENGTH_SHORT).show();
                });
        return notificationMutableList;
    }
}

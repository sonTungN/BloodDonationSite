package com.sontung.blood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.model.Notification;
import com.sontung.blood.repo.NotificationRepository;
import com.sontung.blood.repo.ReportRepository;

import java.util.List;

public class NotificationViewModel extends AndroidViewModel {
    private final NotificationRepository notificationRepository;
    
    public NotificationViewModel(@NonNull Application application) {
        super(application);
        this.notificationRepository = new NotificationRepository(getApplication());
    }
    
    public void createNotification(Notification notification, FirebaseCallback<Notification> callback) {
        notificationRepository.createNotification(notification, callback);
    }
    
    public void updateNotificationId(String notificationId, Notification updateNotification) {
        notificationRepository.updateNotificationId(notificationId, updateNotification);
    }
    
    public MutableLiveData<List<Notification>> getNotificationDataByReceiverId(String receiverId) {
        return notificationRepository.getNotificationDataByReceiverId(receiverId);
    }
}

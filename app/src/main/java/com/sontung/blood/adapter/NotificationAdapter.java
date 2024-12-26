package com.sontung.blood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sontung.blood.R;
import com.sontung.blood.databinding.NotificationHorizontalCardBinding;
import com.sontung.blood.model.Notification;
import com.sontung.blood.utils.DateFormatter;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final Context context;
    private final List<Notification> notificationList;
    
    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationHorizontalCardBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.notification_horizontal_card,
                parent,
                false
        );
        return new ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.binding.setNotification(notification);
        holder.binding.notificationSentDate.setText(DateFormatter.toDateString(notification.getSentDate()));
    }
    
    @Override
    public int getItemCount() {
        return notificationList.size();
    }
    
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        NotificationHorizontalCardBinding binding;
        
        public ViewHolder(NotificationHorizontalCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                String notificationId = notificationList.get(pos).getNotificationId();
                
                Toast.makeText(context, notificationId, Toast.LENGTH_SHORT).show();
            });
        }
    }
}

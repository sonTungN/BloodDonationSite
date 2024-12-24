package com.sontung.blood.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sontung.blood.R;
import com.sontung.blood.databinding.ItemHorizontalCardBinding;
import com.sontung.blood.model.User;

import java.util.List;

public class VolunteerCardAdapter extends RecyclerView.Adapter<VolunteerCardAdapter.ViewHolder> {
    private final Context context;
    private final List<User> listOfVolunteers;
    
    public VolunteerCardAdapter(Context context, List<User> listOfVolunteers) {
        this.context = context;
        this.listOfVolunteers = listOfVolunteers;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHorizontalCardBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_horizontal_card,
                parent,
                false
        );
        return new ViewHolder(binding);
    }
    
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = listOfVolunteers.get(position);
        holder.binding.setUser(user);
        
        holder.binding.addReportBtn.setText("NO REPORT");
        holder.binding.addReportBtn.setEnabled(false);
        
        Glide.with(context)
                .load(user.getProfileUrl())
                .into(holder.binding.avatar);
    }
    
    @Override
    public int getItemCount() {
        return listOfVolunteers.size();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHorizontalCardBinding binding;
        
        public ViewHolder(ItemHorizontalCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                
                String userId = listOfVolunteers.get(pos).getUserId();
                Toast.makeText(context, "Volunteer IO:" + userId, Toast.LENGTH_SHORT).show();
            });
        }
    }
}

package com.sontung.blood.adapter;

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

public class DonorCardAdapter extends RecyclerView.Adapter<DonorCardAdapter.ViewHolder> {
    private final Context context;
    private final List<User> listOfDonors;
    
    public DonorCardAdapter(Context context, List<User> listOfDonors) {
        this.context = context;
        this.listOfDonors = listOfDonors;
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
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = listOfDonors.get(position);
        holder.binding.setUser(user);
        
        Glide.with(context)
                .load(user.getProfileUrl())
                .into(holder.binding.avatar);
    }
    
    @Override
    public int getItemCount() {
        return listOfDonors.size();
    }
    
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHorizontalCardBinding binding;
        
        public ViewHolder(ItemHorizontalCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(view -> {
                Toast.makeText(context, "Click add report to add new report", Toast.LENGTH_SHORT).show();
            });
            
            binding.addReportBtn.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                
                String userId = listOfDonors.get(pos).getUserId();
                Toast.makeText(context, "Donors IO:" + userId, Toast.LENGTH_SHORT).show();
            });
        }
    }
}

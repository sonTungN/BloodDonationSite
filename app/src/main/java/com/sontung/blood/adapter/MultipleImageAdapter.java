package com.sontung.blood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sontung.blood.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MultipleImageAdapter extends RecyclerView.Adapter<MultipleImageAdapter.ViewHolder> {
    private final List<String> listOfImageUrl;
    
    public MultipleImageAdapter(List<String> listOfImageUrl) {
        this.listOfImageUrl = listOfImageUrl;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_slider_item, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = listOfImageUrl.get(position);
        
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.imageView);
    }
    
    @Override
    public int getItemCount() {
        return listOfImageUrl != null ? listOfImageUrl.size() : 0;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSliderItem);
        }
    }
}

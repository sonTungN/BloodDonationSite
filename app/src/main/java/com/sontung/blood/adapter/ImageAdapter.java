package com.sontung.blood.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sontung.blood.R;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<Uri> imageUris;
    private final OnItemCountAfterDelete onItemCountAfterDelete;
    private final OnItemZoom onItemZoom;
    
    public ImageAdapter(
            Context context,
            OnItemCountAfterDelete onItemCountAfterDelete,
            OnItemZoom onItemZoom
    ) {
        this.context = context;
        this.onItemCountAfterDelete = onItemCountAfterDelete;
        this.onItemZoom = onItemZoom;
    }
    
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Uri> uriList) {
        this.imageUris = uriList;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.added_image, parent, false);
        return new ImageViewHolder(view, onItemCountAfterDelete, onItemZoom);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        
        Glide
                .with(holder.itemView.getContext())
                .load(imageUri)
                .into(holder.imageView);
        
        holder.deleteIcon.setOnClickListener(
                view -> {
                    imageUris.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    onItemCountAfterDelete.clickDelete(imageUris.size());
                });
    }
    
    @Override
    public int getItemCount() {
        if (imageUris != null) {
            return imageUris.size();
        }
        return 0;
    }
    
    public static class ImageViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final ImageView imageView;
        private final ImageView deleteIcon;
        
        private OnItemCountAfterDelete onItemCountAfterDelete;
        
        private final OnItemZoom onItemZoom;
        
        public ImageViewHolder(
                @NonNull View itemView,
                OnItemCountAfterDelete onItemCountAfterDelete,
                OnItemZoom onItemZoom) {
            super(itemView);
            this.onItemCountAfterDelete = onItemCountAfterDelete;
            this.onItemZoom = onItemZoom;
            imageView = itemView.findViewById(R.id.addedImageId);
            deleteIcon = itemView.findViewById(R.id.deleteImageIcon);
            itemView.setOnClickListener(this);
        }
        
        @Override
        public void onClick(View v) {
            if (onItemZoom != null) {
                onItemZoom.itemZoomClick(getAdapterPosition());
            }
        }
    }
    
    public interface OnItemCountAfterDelete {
        void clickDelete(int leftNum);
    }
    
    public interface OnItemZoom {
        void itemZoomClick(int position);
    }
}

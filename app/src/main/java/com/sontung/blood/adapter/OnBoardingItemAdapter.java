package com.sontung.blood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sontung.blood.R;
import com.sontung.blood.model.OnBoardingItem;

import java.util.List;

public class OnBoardingItemAdapter extends RecyclerView.Adapter<OnBoardingItemAdapter.ViewHolder> {
    private final List<OnBoardingItem> pageItems;

    // Constructor to initialize the list of items
    public OnBoardingItemAdapter(List<OnBoardingItem> pageItems) {
        this.pageItems = pageItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.onboarding_page_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnBoardingItem currentPageItem = pageItems.get(position);
        holder.imageView.setImageResource(currentPageItem.getImageResId());
        holder.title.setText(currentPageItem.getTitle());
        holder.description.setText(currentPageItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return pageItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }
}



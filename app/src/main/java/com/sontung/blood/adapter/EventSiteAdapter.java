package com.sontung.blood.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sontung.blood.R;
import com.sontung.blood.databinding.SiteProfileItemBinding;
import com.sontung.blood.model.Site;
import com.sontung.blood.utils.DateFormatter;
import com.sontung.blood.views.EventDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class EventSiteAdapter extends RecyclerView.Adapter<EventSiteAdapter.ViewHolder> {
    private final Context context;
    private final List<Site> siteList;
    private final List<Site> storedSiteList;
    
    public EventSiteAdapter(Context context, List<Site> siteList) {
        this.context = context;
        this.siteList = siteList;
        this.storedSiteList = new ArrayList<>(siteList);
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SiteProfileItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.site_profile_item,
                parent,
                false
        );
        
        return new ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Site site = siteList.get(position);
        List<String> imageUrl = site.getSiteImageUrl();
        
        holder.binding.setSite(site);
        holder.binding.siteDate.setText(DateFormatter.toDateString(site.getEventDate()));
        
        Glide.with(context)
                .load(imageUrl.get(0))
                .into(holder.binding.thumb);
    }
    
    @Override
    public int getItemCount() {
        return siteList.size();
    }
    
    // ----------------------- SETTING UP FILTER FOR SEARCH -----------------------
    private final Filter listOfFilteredSite = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence sequence) {
            List<Site> filteredList = new ArrayList<>();
            
            if (sequence == null || sequence.length() == 0) {
                filteredList.addAll(storedSiteList);
                
            } else {
                String pattern = sequence.toString().toLowerCase().trim();
                
                for (Site s: storedSiteList) {
                    if (s.getSiteName().toLowerCase().contains(pattern)) {
                        filteredList.add(s);
                    }
                }
            }
            
            FilterResults results = new FilterResults();
            results.values = filteredList;
            
            return results;
        }
        
        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence sequence, FilterResults results) {
            siteList.clear();
            siteList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    
    public Filter getFilter() { return listOfFilteredSite; }
    // ----------------------- SETTING UP FILTER FOR SEARCH -----------------------
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final SiteProfileItemBinding binding;
        
        public ViewHolder(SiteProfileItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                
                Intent i = new Intent(context, EventDetailActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("SITE_ID", siteList.get(pos).getSiteId());
                context.startActivity(i);
            });
        }
    }
}

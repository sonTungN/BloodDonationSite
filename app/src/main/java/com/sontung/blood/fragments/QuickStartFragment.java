package com.sontung.blood.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sontung.blood.R;
import com.sontung.blood.adapter.OnBoardingItemAdapter;
import com.sontung.blood.databinding.FragmentQuickStartBinding;
import com.sontung.blood.model.OnBoardingItem;
import com.sontung.blood.views.AuthGateWayActivity;
import com.sontung.blood.views.HomeActivity;
import com.sontung.blood.views.OnBoardingActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.Arrays;
import java.util.List;

public class QuickStartFragment extends Fragment {
    
    private FragmentQuickStartBinding binding;
    
    private ViewPager2 viewPager2;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        binding = FragmentQuickStartBinding.inflate(getLayoutInflater());
        viewPager2 = binding.viewPager2;
        
        List<OnBoardingItem> onboardPageItems = Arrays.asList(
                new OnBoardingItem(
                        "Welcome to PanTho",
                        "This application connects donors, volunteers, and event hosts to streamline blood donation and save lives.",
                        R.drawable.img_donation),
                
                new OnBoardingItem(
                        "Search Donation Events",
                        "Easily search for donation events by blood type and date, ensuring you're always informed about opportunities to contribute.",
                        R.drawable.onboarding_1),
                
                new OnBoardingItem(
                        "Become a Member",
                        "Effortlessly apply to be a donor or volunteer for events. Join on the scheduled date as displayed in event details.",
                        R.drawable.onboarding_3),
                
                new OnBoardingItem(
                        "Google Maps Tracking",
                        "View all the events you've joined or applied for on the map. Stay organized and never miss a location.",
                        R.drawable.img_google_map),
                
                new OnBoardingItem(
                        "Host your Site",
                        "Create and manage your own blood donation events. Update participants and track donation reports with ease.",
                        R.drawable.onboarding_4),
                
                new OnBoardingItem(
                        "Society Impact",
                        "Join the community in saving lives. Your contribution matters—donate blood and inspire others to do the same.",
                        R.drawable.img_society_impact)
        );
        
        viewPager2.setAdapter(new OnBoardingItemAdapter(onboardPageItems));
        
        DotsIndicator indicator = binding.dotsIndicator;
        indicator.attachTo(viewPager2);
        
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                boolean isLast = position == onboardPageItems.size() - 1;
                
                if (position > 0) {
                    binding.backBtn.setVisibility(View.VISIBLE);
                    
                } else {
                    binding.backBtn.setVisibility(View.GONE);
                }
                
                binding.beginBtn.setText(isLast ? "Let's Begin" : "Next");
            }
        });
        
        binding.backBtn.setOnClickListener(v -> {
            int currentPage = viewPager2.getCurrentItem();
            viewPager2.setCurrentItem(currentPage - 1);
        });
        
        binding.beginBtn.setOnClickListener(v -> {
            int currentPage = viewPager2.getCurrentItem();
            if (currentPage < onboardPageItems.size() - 1) {
                viewPager2.setCurrentItem(currentPage + 1);
            } else {
                navigateToGateWay();
            }
        });
        
        return binding.getRoot();
    }
    
    private void navigateToGateWay() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
    }
}
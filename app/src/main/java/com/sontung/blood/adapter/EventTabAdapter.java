package com.sontung.blood.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sontung.blood.fragments.EventFragment;
import com.sontung.blood.fragments.EventMapFragment;

public class EventTabAdapter extends FragmentStateAdapter {
    
    public EventTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new EventMapFragment();
        }
        return new EventFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
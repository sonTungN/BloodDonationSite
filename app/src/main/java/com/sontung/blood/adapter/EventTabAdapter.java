package com.sontung.blood.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sontung.blood.fragments.EventFragment;
import com.sontung.blood.fragments.EventMapFragment;

public class EventTabAdapter extends FragmentPagerAdapter {

    public EventTabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new EventMapFragment();
        }
        return new EventFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
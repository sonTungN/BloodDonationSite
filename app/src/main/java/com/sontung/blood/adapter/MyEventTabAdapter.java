package com.sontung.blood.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sontung.blood.fragments.CreateEventFragment;
import com.sontung.blood.fragments.EditEventFragment;
import com.sontung.blood.fragments.SiteMemberFragment;

public class MyEventTabAdapter extends FragmentStateAdapter {
    public MyEventTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new EditEventFragment();
            case 2:
                return new SiteMemberFragment();
            default:
                return new CreateEventFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 3;
    }
}

package com.sontung.blood.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sontung.blood.fragments.ContactUsFragment;
import com.sontung.blood.fragments.CreateEventFragment;
import com.sontung.blood.fragments.EditEventFragment;
import com.sontung.blood.fragments.QuickStartFragment;
import com.sontung.blood.fragments.SiteMemberFragment;

public class GuidelineTabAdapter extends FragmentStateAdapter {
    public GuidelineTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new ContactUsFragment();
        }
        return new QuickStartFragment();
    }
    
    @Override
    public int getItemCount() {
        return 2;
    }
}

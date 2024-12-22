package com.sontung.blood.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sontung.blood.fragments.CreateEventFragment;

public class CreateEventAdapter extends FragmentStateAdapter {
    public CreateEventAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
//            case 1:
//                return new EditEventFragment();
//            case 2:
//                return new CreateReportFragment();
//            case 3:
//                return new ViewReportFragment();
            default:
                return new CreateEventFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 1;
    }
}

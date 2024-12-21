package com.sontung.blood.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.sontung.blood.R;
import com.sontung.blood.adapter.OnBoardingItemAdapter;
import com.sontung.blood.databinding.ActivityOnBoardingBinding;
import com.sontung.blood.model.OnBoardingItem;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.Arrays;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    private ActivityOnBoardingBinding binding;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_boarding);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding);
        viewPager2 = binding.viewPager2;

        List<OnBoardingItem> onboardPageItems = Arrays.asList(
                new OnBoardingItem("Find Donors", "Instantly connect with verified donors nearby, reducing wait times and securing essential blood donations.", R.drawable.onboarding_1),
                new OnBoardingItem("Testing", "Quickly schedule health checks, reducing delays and ensuring readiness for confident, hassle-free donations.", R.drawable.onboarding_2),
                new OnBoardingItem("Donation Tracking", "Track donations, set reminders, and monitor impact, keeping the donation process organized and meaningful.", R.drawable.onboarding_3),
                new OnBoardingItem("Community Impact", "Join a compassionate donor community, inspiring generosity and strengthening life-saving networks.", R.drawable.onboarding_4)
        );

        viewPager2.setAdapter(new OnBoardingItemAdapter(onboardPageItems));

        DotsIndicator indicator = binding.dotsIndicator;
        indicator.attachTo(viewPager2);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                boolean isLast = position == onboardPageItems.size() - 1;

                binding.beginBtn.setText(isLast ? "Let's Begin" : "Next");
            }
        });

        binding.beginBtn.setOnClickListener(v -> {
            int currentPage = viewPager2.getCurrentItem();
            if (currentPage < onboardPageItems.size() - 1) {
                viewPager2.setCurrentItem(currentPage + 1);
            } else {
                navigateToGateWay();
            }
        });

        binding.skipBtn.setOnClickListener(v -> navigateToGateWay());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void navigateToGateWay() {
        Intent intent = new Intent(OnBoardingActivity.this, AuthGateWayActivity.class);
        startActivity(intent);
        
        finish();
    }
}
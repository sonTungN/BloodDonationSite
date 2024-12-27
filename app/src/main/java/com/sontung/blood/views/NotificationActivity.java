package com.sontung.blood.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.sontung.blood.R;
import com.sontung.blood.adapter.NotificationAdapter;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.ActivityHomeBinding;
import com.sontung.blood.databinding.ActivityNotificationBinding;
import com.sontung.blood.model.Notification;
import com.sontung.blood.model.User;
import com.sontung.blood.viewmodel.NotificationViewModel;
import com.sontung.blood.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    
    private ActivityNotificationBinding binding;
    private UserViewModel userViewModel;
    private NotificationViewModel notificationViewModel;
    
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    
    // Navbar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.noNotificationDisplay.setVisibility(View.GONE);
        
        setUpNotificationRecyclerView();
        setUpDrawer();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    private void setUpNotificationRecyclerView() {
        notificationRecyclerView = binding.notificationRecyclerView;
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                true
        );
        layoutManager.setStackFromEnd(true);
        notificationRecyclerView.setLayoutManager(layoutManager);
        
        notificationRecyclerView.hasFixedSize();
        notificationList = new ArrayList<>();
        
        notificationViewModel.getNotificationDataByReceiverId(userViewModel.getCurrentUserId()).observe(this, new Observer<List<Notification>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<Notification> notifications) {
                if (notifications.isEmpty()) {
                    binding.noNotificationDisplay.setVisibility(View.VISIBLE);
                } else {
                    binding.noNotificationDisplay.setVisibility(View.GONE);
                }
                binding.loadingLayout.setVisibility(View.GONE);
                
                notificationList.clear();
                notificationList.addAll(notifications);
                
                notificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationList);
                notificationRecyclerView.setAdapter(notificationAdapter);
                notificationAdapter.notifyDataSetChanged();
            }
        });
    }
    
    @SuppressLint("SetTextI18n")
    private void generalDrawerSetUp() {
        drawerLayout = binding.drawer;
        navigationView = binding.navigationView;
        
        binding.toolbarId.toolbarTitleId.setText("Notifications");
        binding.toolbarId.backIcon.setVisibility(View.GONE);
        
        View headerView = binding.navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_name);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        TextView navUserRole = headerView.findViewById(R.id.nav_user_role);
        ImageView navProfileImg = headerView.findViewById(R.id.profile_image);
        drawerLayout.closeDrawer(GravityCompat.START);
        
        navigationView.bringToFront();
        binding.toolbarId.backIcon.setOnClickListener(view -> finish());
        
        userViewModel.getUserDataById(userViewModel.getCurrentUserId(), new FirebaseCallback<>() {
            @Override
            public void onSuccess(List<User> t) {
            
            }
            
            @Override
            public void onSuccess(User user) {
                navName.setText(user.getUsername());
                navEmail.setText(user.getEmail());
                navUserRole.setText(user.getUserRole());
                
                Glide.with(getApplicationContext())
                        .load(user.getProfileUrl())
                        .into(navProfileImg);
            }
            
            @Override
            public void onFailure(List<User> t) {
            
            }
            
            @Override
            public void onFailure(User user) {
            
            }
        });
        
        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        R.string.open,
                        R.string.close
                );
        drawerLayout.addDrawerListener(drawerToggle);
        
        binding.toolbarId.menuIconId.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    
    private void setUpDrawer() {
        generalDrawerSetUp();
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.general_menu);
        navigationView.setCheckedItem(R.id.nav_notification);
        
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_event) {
                Intent intent = new Intent(this, EventActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_my_event) {
                Intent intent = new Intent(this, CreateEventActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_notification) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
                
            } else if (menuItem.getItemId() == R.id.nav_about_us) {
                Intent intent = new Intent(this, GuidelineActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_logout) {
                Intent intent = new Intent(this, OnBoardingActivity.class);
                finish();
                userViewModel.signOut();
                startActivity(intent);
            }
            return true;
        });
    }
}
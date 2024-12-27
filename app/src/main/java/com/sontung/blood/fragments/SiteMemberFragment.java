package com.sontung.blood.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sontung.blood.R;
import com.sontung.blood.adapter.DonorCardAdapter;
import com.sontung.blood.adapter.VolunteerCardAdapter;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.FragmentSiteMemberBinding;
import com.sontung.blood.model.User;
import com.sontung.blood.viewmodel.SiteViewModel;
import com.sontung.blood.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class SiteMemberFragment extends Fragment {
    
    private FragmentSiteMemberBinding binding;
    private UserViewModel userViewModel;
    private SiteViewModel siteViewModel;
    
    // RecyclerView
    private RecyclerView volunteerRecyclerView;
    private List<User> volunteerList;
    private VolunteerCardAdapter volunteerAdapter;
    
    private RecyclerView donorRecyclerView;
    private List<User> donorList;
    private DonorCardAdapter donorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSiteMemberBinding.inflate(getLayoutInflater());
        
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_site_member, container, false);
        
        setUpInitialState();
        setUpView();
        
        return binding.getRoot();
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private void setUpDonorRecyclerView(String siteId) {
        donorRecyclerView = binding.donorRecyclerView;
        donorRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        donorRecyclerView.hasFixedSize();
        donorList = new ArrayList<>();
        
        siteViewModel.getSiteDonorList(siteId).observe(getViewLifecycleOwner(), users -> {
            donorList.clear();
            donorList.addAll(users);
            
            if (donorList.isEmpty()) {
                binding.notFoundDonor.setVisibility(View.VISIBLE);
            } else {
                binding.notFoundDonor.setVisibility(View.GONE);
            }
            
            donorAdapter = new DonorCardAdapter(requireContext(), donorList, siteId);
            donorRecyclerView.setAdapter(donorAdapter);
            donorAdapter.notifyDataSetChanged();
        });
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private void setUpVolunteerRecyclerView(String siteId) {
        volunteerRecyclerView = binding.volunteerRecyclerView;
        volunteerRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        volunteerRecyclerView.hasFixedSize();
        volunteerList = new ArrayList<>();
        
        siteViewModel.getSiteVolunteerList(siteId).observe(getViewLifecycleOwner(), users -> {
            volunteerList.clear();
            volunteerList.addAll(users);
            
            if (volunteerList.isEmpty()) {
                binding.notFoundVolunteer.setVisibility(View.VISIBLE);
            } else {
                binding.notFoundVolunteer.setVisibility(View.GONE);
            }
            
            volunteerAdapter = new VolunteerCardAdapter(requireContext(), volunteerList);
            volunteerRecyclerView.setAdapter(volunteerAdapter);
            volunteerAdapter.notifyDataSetChanged();
        });
    }
    
    private void setUpOverviewCard(String siteId) {
        siteViewModel.getSiteDataById(siteId).observe(this, site -> {
            if (site == null) {
                binding.siteOverviewLayout.setVisibility(View.GONE);
            } else {
                binding.setCurrentSite(site);
            }
        });
    }
    
    private void fetchDataIntoView(String siteId) {
        setUpOverviewCard(siteId);
        setUpDonorRecyclerView(siteId);
        setUpVolunteerRecyclerView(siteId);
    }
    
    private void setUpView() {
        userViewModel.getUserDataById(userViewModel.getCurrentUserId(), new FirebaseCallback<User>() {
            @Override
            public void onSuccess(List<User> t) {
            
            }
            
            @Override
            public void onSuccess(User user) {
                setLoadingView(false);
                if (user.getHostedSite() == null) {
                    binding.notFoundSiteDonor.setVisibility(View.VISIBLE);
                    binding.notFoundDonor.setVisibility(View.GONE);
                    binding.notFoundSiteVolunteer.setVisibility(View.VISIBLE);
                    binding.notFoundVolunteer.setVisibility(View.GONE);
                    
                } else {
                    binding.notFoundSiteDonor.setVisibility(View.GONE);
                    binding.notFoundSiteVolunteer.setVisibility(View.GONE);
                    
                    fetchDataIntoView(user.getHostedSite());
                }
            }
            
            @Override
            public void onFailure(List<User> t) {
            
            }
            
            @Override
            public void onFailure(User user) {
            
            }
        });
    }
    
    private void setUpInitialState() {
        setLoadingView(true);
        binding.siteOverviewLayout.setVisibility(View.GONE);
        binding.notFoundSiteDonor.setVisibility(View.GONE);
        binding.notFoundDonor.setVisibility(View.GONE);
        binding.notFoundSiteVolunteer.setVisibility(View.GONE);
        binding.notFoundVolunteer.setVisibility(View.GONE);
    }
    
    private void setLoadingView(boolean isLoading) {
        if (isLoading) {
            binding.loadingLayout.setVisibility(View.VISIBLE);
            
        } else {
            binding.loadingLayout.setVisibility(View.GONE);
        }
    }
    
}
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
import android.widget.SearchView;

import com.sontung.blood.R;
import com.sontung.blood.adapter.EventSiteAdapter;
import com.sontung.blood.databinding.FragmentEventBinding;
import com.sontung.blood.model.Site;
import com.sontung.blood.viewmodel.SiteViewModel;


import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment {

    private FragmentEventBinding binding;
    private SiteViewModel siteViewModel;

    // Sites
    private RecyclerView siteRecyclerView;
    private EventSiteAdapter siteAdapter;
    private List<Site> siteList = new ArrayList<>();


    // Search and Filter
    private SearchView searchView;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //    userService.signOut();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        searchView = binding.searchView;
        setUpSiteRecyclerView();
        return binding.getRoot();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        binding = FragmentEventBinding.inflate(getLayoutInflater());
    }
    
    @Override
    public void onResume() {
        super.onResume();
        setUpSiteRecyclerView();
    }
    
    // Setting up the search engine
    @SuppressLint("NotifyDataSetChanged")
    private void setUpSiteToViews(List<Site> sites) {
        siteList.clear();
        siteList.addAll(sites);

        siteAdapter = new EventSiteAdapter(getContext(), siteList);
        siteRecyclerView.setAdapter(siteAdapter);
        siteAdapter.notifyDataSetChanged();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                siteAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    // Setting up Site Recycler View
    private void setUpSiteRecyclerView() {
        siteRecyclerView = binding.siteRecyclerView;
        siteRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        siteRecyclerView.hasFixedSize();
        siteList = new ArrayList<>();

        siteViewModel.getAllSiteData().observe(getViewLifecycleOwner(), this::setUpSiteToViews);
    }
}
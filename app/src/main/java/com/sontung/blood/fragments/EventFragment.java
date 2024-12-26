package com.sontung.blood.fragments;

import android.annotation.SuppressLint;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sontung.blood.R;
import com.sontung.blood.adapter.EventSiteAdapter;
import com.sontung.blood.databinding.FragmentEventBinding;
import com.sontung.blood.model.Site;
import com.sontung.blood.utils.DateComparer;
import com.sontung.blood.utils.DateFormatter;
import com.sontung.blood.utils.FieldValidation;
import com.sontung.blood.viewmodel.SiteViewModel;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventFragment extends Fragment {

    private FragmentEventBinding binding;
    private SiteViewModel siteViewModel;

    // Sites
    private RecyclerView siteRecyclerView;
    private EventSiteAdapter siteAdapter;
    private List<Site> siteList = new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentEventBinding.inflate(getLayoutInflater());
        
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_event, container, false);
        
        setUpInitialState();
        setUpSiteRecyclerView();
        setUpBloodTypeSpinner();
        setUpCalendarPicker();
        setUpButtonClickHandler();
        
        return binding.getRoot();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        setUpInitialState();
    }
    
    //----------------------------------------SET UP APPLY BUTTON-----------------------------------
    private boolean isSiteFilterValid() {
        clearErrorMessage();
        int invalidCount = 0;
        
        String startDateStr = binding.filterStartDate.getText().toString().trim();
        
        if (startDateStr.isEmpty()) {
            turnOnErrorMessage(binding.startDateEmpty, true);
            turnOnErrorMessage(binding.startDatePast, false);
            invalidCount++;
            
        } else if (DateComparer.isEventDatePassed(DateFormatter.toDate(startDateStr))) {
            turnOnErrorMessage(binding.startDateEmpty, false);
            turnOnErrorMessage(binding.startDatePast, true);
            invalidCount++;
        }
        
        return invalidCount == 0;
    }
    
    private void applyFilter() {
        if (!isSiteFilterValid()) {
            Toast.makeText(requireContext(), "ERROR: Some filter are invalid!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toast.makeText(requireContext(), "Filter Applied", Toast.LENGTH_SHORT).show();
        
        String bloodType = binding.bloodTypeSpinner.getSelectedItem().toString().trim();
        Date startDate = DateFormatter.toDate(binding.filterStartDate.getText().toString().trim());
        
        siteViewModel.getAllSiteDataWithQuery(bloodType, startDate)
                .observe(getViewLifecycleOwner(), sites -> {
                    binding.siteRecyclerView.setVisibility(View.VISIBLE);
                    
                    if (sites == null || sites.isEmpty()) {
                        binding.noEventDisplayingText.setVisibility(View.VISIBLE);
                        siteList.clear();
                        
                    } else {
                        binding.noEventDisplayingText.setVisibility(View.GONE);
                        setUpSiteToViews(sites);
                    }
                });
    }
    
    //----------------------------------------SET UP VIEWS------------------------------------------
    @SuppressLint("NotifyDataSetChanged")
    private void setUpSiteToViews(List<Site> sites) {
        siteList.clear();
        siteList.addAll(sites);

        siteAdapter = new EventSiteAdapter(getContext(), siteList);
        siteRecyclerView.setAdapter(siteAdapter);
        siteAdapter.notifyDataSetChanged();
        
        if (siteList.isEmpty()) {
            binding.noEventDisplayingText.setVisibility(View.VISIBLE);
        } else {
            binding.noEventDisplayingText.setVisibility(View.GONE);
        }

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    }
    
    private void setUpBloodTypeSpinner() {
        Spinner bloodTypeSpinner = binding.bloodTypeSpinner;
        ArrayAdapter<CharSequence> bloodTypesAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.blood_types,
                android.R.layout.simple_spinner_item
        );
        bloodTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeSpinner.setAdapter(bloodTypesAdapter);
        bloodTypeSpinner.setSelection(0);
    }
    
    private void setUpCalendarPicker() {
        binding.filterStartDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
                        
                        String formattedDate = DateFormatter.toDateString(calendar.getTime());
                        binding.filterStartDate.setText(formattedDate);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );
            
            datePickerDialog.show();
        });
    }
    
    private void setUpButtonClickHandler() {
        binding.saveFilterBtn.setOnClickListener(v -> applyFilter());
    }
    
    //----------------------------------------SET UP TOOLS FUNCTION---------------------------------
    private void setUpInitialState() {
        binding.noEventDisplayingText.setVisibility(View.VISIBLE);
        clearErrorMessage();
    }
    
    private void clearErrorMessage() {
        turnOnErrorMessage(binding.startDateEmpty, false);
        turnOnErrorMessage(binding.startDatePast, false);
    }
    
    private void turnOnErrorMessage(View view, Boolean isError) {
        if (isError) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
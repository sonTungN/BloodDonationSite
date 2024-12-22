package com.sontung.blood.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static java.util.Arrays.asList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.sontung.blood.BuildConfig;
import com.sontung.blood.R;
import com.sontung.blood.adapter.ImageAdapter;
import com.sontung.blood.databinding.FragmentCreateEventBinding;
import com.sontung.blood.model.Site;
import com.sontung.blood.model.User;
import com.sontung.blood.utils.FieldValidation;
import com.sontung.blood.viewmodel.ImageViewModel;
import com.sontung.blood.viewmodel.SiteViewModel;
import com.sontung.blood.viewmodel.UserViewModel;
import com.sontung.blood.views.EventDetailActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateEventFragment
        extends Fragment
        implements ImageAdapter.OnItemCountAfterDelete, ImageAdapter.OnItemZoom {
    
    private FragmentCreateEventBinding fragmentCreateSiteBinding;
    private UserViewModel userViewModel;
    private SiteViewModel siteViewModel;
    private ImageViewModel imageViewModel;
    
    private User currentUser;
    
    // Google Map displaying
    private View mapPanel;
    private Marker marker;
    private GoogleMap map;
    private LatLng coordinates;
    
    private final List<Uri> imageUriList = new ArrayList<>();
    private ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentCreateSiteBinding = FragmentCreateEventBinding.inflate(getLayoutInflater());
        
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        
        imageAdapter = new ImageAdapter(requireContext(), this, this);
        imageAdapter.setData(imageUriList);
        fragmentCreateSiteBinding.imageRecyclerView.setAdapter(imageAdapter);
        
        currentUser = userViewModel.getCurrentUserClass();
        if (currentUser != null) {
            if (currentUser.getHostedSite() != null) {
                fragmentCreateSiteBinding.siteDisplayingText.setVisibility(View.VISIBLE);
                fragmentCreateSiteBinding.createEventLayout.setVisibility(View.GONE);
                
            } else if (currentUser.getUserRole().equals("donor")) {
                setupDonorView();
            }
        }
        
        setUpAutoCompleteAddress();
        setUpBloodTypeSpinner();
        setUpButtonClickHandler();
    }
    
    private void setupDonorView() {
        fragmentCreateSiteBinding.siteDisplayingText.setVisibility(View.GONE);
        fragmentCreateSiteBinding.createEventLayout.setVisibility(View.VISIBLE);
        
        initialSetUp();
    }
    
    //----------------------------------------SET UP MAP VIEWS-------------------------------------
    private void setUpAutoCompleteAddress() {
        Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY);
        Places.createClient(requireContext());

        AutocompleteSupportFragment autoCompleteFragment = AutocompleteSupportFragment.newInstance();

        // Add it to the container using childFragmentManager
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.autocomplete_fragment, autoCompleteFragment)
                .commit();

        autoCompleteFragment.setActivityMode(AutocompleteActivityMode.FULLSCREEN);
        autoCompleteFragment.setPlaceFields(asList(
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        ));
        autoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                fragmentCreateSiteBinding.addressDisplay.setText(place.getAddress());
                coordinates = place.getLatLng();
                showLocationOnMap(place);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("ADDRESS", "An error occurred: " + status);
            }
        });
    }

    private void showLocationOnMap(Place place) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapPanel = fragmentCreateSiteBinding.stubMap.inflate();

            GoogleMapOptions options = new GoogleMapOptions();
            options.mapToolbarEnabled(false);

            mapFragment = SupportMapFragment.newInstance(options);
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.confirmation_map, mapFragment, "MAP")
                    .commitNow();
            mapFragment.getMapAsync(googleMap -> {
                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                try {
                    boolean isSuccess =
                            map.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            requireContext(),
                                            R.raw.style_raw));
                    if (!isSuccess) {
                        Log.e(TAG, "STYLE: Style parsing Error");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "STYLE: Style not found", e);
                }

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f));
                marker = map.addMarker(new MarkerOptions().position(coordinates));
            });
        } else {
            updateMapWithCoordinates(coordinates);
        }
    }

    private void updateMapWithCoordinates(LatLng cor) {
        marker.setPosition(cor);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cor, 18f));
        if (mapPanel.getVisibility() == View.GONE) {
            mapPanel.setVisibility(View.VISIBLE);
        }
    }

    //----------------------------------------SET UP VIEWS------------------------------------------
    private void setUpBloodTypeSpinner() {
        // Spinner
        Spinner bloodTypeSpinner = fragmentCreateSiteBinding.bloodTypeSpinner;
        ArrayAdapter<CharSequence> bloodTypesAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.blood_types,
                android.R.layout.simple_spinner_item
        );
        bloodTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeSpinner.setAdapter(bloodTypesAdapter);
        
        bloodTypeSpinner.setSelection(0);
    }
    
    private void setUpButtonClickHandler() {
        fragmentCreateSiteBinding.addImageBtn.setOnClickListener(view -> openFile());
        fragmentCreateSiteBinding.createSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSite();
            }
        });
    }
    
    //----------------------------------------SET UP CREATE SITE------------------------------------
    private boolean isSiteInputValid() {
        clearErrorMessage();
        int invalidCount = 0;
        String siteName = fragmentCreateSiteBinding.createSiteName.getText().toString();
        String siteDesc = fragmentCreateSiteBinding.createSiteDesc.getText().toString();
        String siteAddress = fragmentCreateSiteBinding.addressDisplay.getText().toString();
        int volunteerCap = Integer.parseInt(fragmentCreateSiteBinding.volunteerCap.getText().toString());
        int donorCap = Integer.parseInt(fragmentCreateSiteBinding.donorCap.getText().toString());
        
        if (!FieldValidation.isValidStringInRange(siteName, 6, 15)) {
            turnOnErrorMessage(fragmentCreateSiteBinding.createSiteNameError, true);
            invalidCount++;
        }
        
        if (!FieldValidation.isValidStringInRange(siteDesc, 0, 20)) {
            turnOnErrorMessage(fragmentCreateSiteBinding.createSiteDescErr, true);
            invalidCount++;
        }
        
        if (siteAddress.isEmpty()) {
            turnOnErrorMessage(fragmentCreateSiteBinding.createSiteAddressErr, true);
            invalidCount++;
        }
        
        if (!FieldValidation.isValidNumberInRange(volunteerCap, 1, 10)){
            turnOnErrorMessage(fragmentCreateSiteBinding.createVolunteerCapErr, true);
            invalidCount++;
        }
        
        if (!FieldValidation.isValidNumberInRange(donorCap, 1, 10)){
            turnOnErrorMessage(fragmentCreateSiteBinding.createDonorCapErr, true);
            invalidCount++;
        }
        
        return invalidCount == 0;
    }
    
    private void createSite() {
        if(!isSiteInputValid()) {
            Toast.makeText(requireContext(), "ERROR: Some input are invalid!", Toast.LENGTH_SHORT).show();
            return;
        }
    
        Site pendingCreatedSite =
                Site.builder()
                        .host(currentUser.getUserId())
                        .siteName(fragmentCreateSiteBinding.createSiteName.getText().toString())
                        .siteDesc(fragmentCreateSiteBinding.createSiteDesc.getText().toString())
                        .siteAddress(fragmentCreateSiteBinding.addressDisplay.getText().toString())
                        .requiredBloodType(fragmentCreateSiteBinding.bloodTypeSpinner.getSelectedItem().toString())
                        .donorMaxCapacity(Integer.parseInt(fragmentCreateSiteBinding.donorCap.getText().toString()))
                        .volunteerMaxCapacity(Integer.parseInt(fragmentCreateSiteBinding.volunteerCap.getText().toString()))
                        .latitude(String.valueOf(coordinates.latitude))
                        .longitude(String.valueOf(coordinates.longitude))
                        .build();
        
        siteViewModel.setUserViewModel(userViewModel);
        siteViewModel.createNewSite(pendingCreatedSite);
        
        if (imageUriList.isEmpty()) {
            navigateToRecentSiteDetails();
        }
        
        Site createdSite = siteViewModel.getUserHostedSite(userViewModel.getCurrentUserId()).getValue();
        List<String> imageUriStringList = imageUriList
                                            .stream()
                                            .map(Uri::toString)
                                            .collect(Collectors.toList());
        
        if (createdSite == null) {
            Toast.makeText(requireContext(), "Created Site in createSite() null", Toast.LENGTH_SHORT).show();
            
        } else {
            createdSite.setSiteImageUrl(imageUriStringList);
            imageViewModel.uploadImageToStorage(
                    imageUriList,
                    Objects.requireNonNull(siteViewModel.getUserHostedSite(userViewModel.getCurrentUserId()).getValue()).getSiteId()
            );
            
            siteViewModel.updateSiteImages(createdSite.getSiteId(), createdSite);
            navigateToRecentSiteDetails();
        }
    }
    
    private void navigateToRecentSiteDetails() {
        String siteId = Objects.requireNonNull(siteViewModel.getUserHostedSite(currentUser.getUserId()).getValue()).getSiteId();
        Intent i = new Intent(requireContext(), EventDetailActivity.class)
                .putExtra("SITE_ID", siteId);
        startActivity(i);
    }
    
    //----------------------------------------SET UP IMAGE UPLOADING--------------------------------
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("image/*")
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        
        chooseImageAction.launch(intent);
    }
    
    private final ActivityResultLauncher<Intent> chooseImageAction =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onActivityResult(ActivityResult activityResult) {
            int resultCode = activityResult.getResultCode();
            Intent intentData = activityResult.getData();
            
            if (resultCode == RESULT_OK && intentData != null) {
                if (intentData.getClipData() != null) {
                    int numOfImages = intentData.getClipData().getItemCount();
                    
                    for (int i = 0; i < numOfImages; i++) {
                        Uri imageUri = intentData.getClipData().getItemAt(i).getUri();
                        if (imageUriList.size() >= 3) {
                            Toast.makeText(requireContext(), "EXCEED: Maximum 3 pictures!", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        imageUriList.add(imageUri);
                    }
                    
                } else if (intentData.getData() != null) {
                    if(imageUriList.size() < 3) {
                        Uri imageUri = intentData.getData();
                        imageUriList.add(imageUri);
                        
                    } else {
                        Toast.makeText(requireContext(), "EXCEED: Maximum 3 pictures!", Toast.LENGTH_SHORT).show();
                    }
                }
                
                imageAdapter.setData(imageUriList);
                if (!imageUriList.isEmpty()) {
                    fragmentCreateSiteBinding.defaultImageLayout.setVisibility(View.INVISIBLE);
                } else {
                    fragmentCreateSiteBinding.defaultImageLayout.setVisibility(View.VISIBLE);
                }
                
            fragmentCreateSiteBinding.imageCount.setText(imageUriList.size() + "/3");
            }
        }
    });
    
    //----------------------------------------SET UP TOOLS FUNCTION---------------------------------
    private void initialSetUp() {
        fragmentCreateSiteBinding.defaultImageLayout.setVisibility(View.VISIBLE);
        clearErrorMessage();
    }
    
    private void clearErrorMessage() {
        turnOnErrorMessage(fragmentCreateSiteBinding.createSiteNameError, false);
        turnOnErrorMessage(fragmentCreateSiteBinding.createSiteDescErr, false);
        turnOnErrorMessage(fragmentCreateSiteBinding.createVolunteerCapErr, false);
        turnOnErrorMessage(fragmentCreateSiteBinding.createDonorCapErr, false);
        turnOnErrorMessage(fragmentCreateSiteBinding.createSiteAddressErr, false);
    }

    private void turnOnErrorMessage(View view, Boolean isError) {
        if (isError) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
    //----------------------------------------DONE -------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_create_event, container, false);

        return fragmentCreateSiteBinding.getRoot();
    }
    
    @SuppressLint("SetTextI18n")
    @Override
    public void clickDelete(int leftNum) {
        fragmentCreateSiteBinding.imageCount.setText(leftNum + "/3");
        
        if (imageUriList.isEmpty()) {
            fragmentCreateSiteBinding.imageCount.setVisibility(View.VISIBLE);
        } else {
            fragmentCreateSiteBinding.imageCount.setVisibility(View.INVISIBLE);
        }
    }
    
    @Override
    public void itemZoomClick(int position) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.image_zoom_view);
        dialog.setCancelable(true);
        
        Window reviewWindow = dialog.getWindow();
        assert reviewWindow != null;
        reviewWindow.setGravity(Gravity.CENTER);
        
        ImageView imageViewZoom = dialog.findViewById(R.id.imageZoomId);
        Glide
                .with(requireContext())
                .load(imageUriList.get(position))
                .into(imageViewZoom);
        dialog.show();
    }
}
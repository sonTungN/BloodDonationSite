package com.sontung.blood.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
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
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.FragmentCreateEventBinding;
import com.sontung.blood.model.Address;
import com.sontung.blood.model.Site;
import com.sontung.blood.model.User;
import com.sontung.blood.shared.Coordinates;
import com.sontung.blood.utils.FieldValidation;
import com.sontung.blood.viewmodel.ImageViewModel;
import com.sontung.blood.viewmodel.SiteViewModel;
import com.sontung.blood.viewmodel.UserViewModel;
import com.sontung.blood.views.EventDetailActivity;

import java.util.ArrayList;
import java.util.Arrays;
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
    
    private User currentUser = new User();
    
    // Google Map displaying
    private View mapPanel;
    private Marker marker;
    private GoogleMap map;
    private LatLng coordinates;
    
    private final List<Uri> imageUriList = new ArrayList<>();
    private ImageAdapter imageAdapter;
    
    private final List<Address> addressList = Coordinates.CREATE_ADDRESS_AVAILABLE;
    
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

//        userViewModel.getCurrentUserData().observe(this, user ->
//                currentUser = User.builder()
//                    .userId(user.getUserId())
//                    .username(user.getUsername())
//                    .email(user.getEmail())
//                    .bloodType(user.getBloodType())
//                    .profileUrl(user.getProfileUrl())
//                    .userRole(user.getUserRole())
//                    .hostedSite(user.getHostedSite())
//                    .listOfRegisteredSites(user.getListOfRegisteredSites())
//                    .listOfVolunteerSites(user.getListOfVolunteerSites())
//                    .build()
//        );

//        currentUser = userViewModel.getCurrentUserData();
//        Toast.makeText(requireContext(), "ID: " + currentUser.getUserId() + ", Name: " + currentUser.getUsername(), Toast.LENGTH_SHORT).show();

//        currentUser = userViewModel.getCurrentUserClass();
//
        
        userViewModel.getCurrentUserData(new FirebaseCallback<User>() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                if (currentUser != null) {
                    if (currentUser.getHostedSite() != null) {
                        fragmentCreateSiteBinding.siteDisplayingText.setVisibility(View.VISIBLE);
                        fragmentCreateSiteBinding.createEventLayout.setVisibility(View.GONE);
                        
                    } else if (currentUser.getUserRole().equals("donor")) {
                        setupDonorView();
                    }
//                    Toast.makeText(requireContext(), "Name: " + currentUser.getUsername(), Toast.LENGTH_SHORT).show();
                
                } else {
                    Toast.makeText(requireContext(), "currentUser is NULL", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Exception e) {
            
            }
        });
        
        Toast.makeText(requireContext(), "Name: " + currentUser.getUsername(), Toast.LENGTH_SHORT).show();

//        setUpAutoCompleteAddress();
        setUpAddressSpinner();
        setUpBloodTypeSpinner();
        setUpButtonClickHandler();
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        fragmentCreateSiteBinding = FragmentCreateEventBinding.inflate(inflater, container, false);
        inflater.inflate(R.layout.fragment_create_event, container, false);
        setUpInitialState();
        return fragmentCreateSiteBinding.getRoot();
    }
    
    private void setupDonorView() {
        fragmentCreateSiteBinding.siteDisplayingText.setVisibility(View.GONE);
        fragmentCreateSiteBinding.createEventLayout.setVisibility(View.VISIBLE);

//        initialSetUp();
    }
    
    //----------------------------------------SET UP MAP VIEWS--------------------------------------
    // SET UP AUTO COMPLETE ADDRESS
    /*
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
        autoCompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS_COMPONENTS
        ));
        autoCompleteFragment.setHint("Enter address");
        autoCompleteFragment.setCountry("VN");

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
    
     */
    
    // SHOW LOCATION ON MAP
    /*
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
     */
    
    private void updateMapWithCoordinates(LatLng cor) {
        if (map == null || marker == null) return;
        
        marker.setPosition(cor);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cor, 15f));
        if (mapPanel.getVisibility() == View.GONE) {
            mapPanel.setVisibility(View.VISIBLE);
        }
    }
    
    //----------------------------------------NEW SET UP MAP SPINNER--------------------------------
    private void setUpMapFragment(LatLng coordinates) {
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
                this.map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                
                if (map == null) {
                    return;
                }
                
                try {
                    boolean success =
                            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_raw));
                    if (!success) {
                        Log.e(TAG, "STYLE: Style parsing Error");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "STYLE: Style not found", e);
                }
                
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f));
                marker = map.addMarker(new MarkerOptions().position(coordinates));
            });
        } else {
            if (map != null) {
                updateMapWithCoordinates(coordinates);
            }
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
    
    private void setUpAddressSpinner() {
        Spinner addressSpinner = fragmentCreateSiteBinding.addressSpinner;
        
        ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                addressList.stream().map(Address::getName).collect(Collectors.toList())
        );
        
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressSpinner.setAdapter(addressAdapter);
        
        addressSpinner.setSelection(0);
        
        addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Address selectedAddress = addressList.get(position);
                coordinates = selectedAddress.getCoordinates();
                fragmentCreateSiteBinding.addressDisplay.setText(selectedAddress.getName());
                
                if (map != null) {
                    updateMapWithCoordinates(coordinates);
                } else {
                    setUpMapFragment(coordinates);
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    
    private void setUpButtonClickHandler() {
        fragmentCreateSiteBinding.addImageBtn.setOnClickListener(v -> openFile());
        fragmentCreateSiteBinding.createSiteButton.setOnClickListener(v -> createSite());
    }
    
    //----------------------------------------SET UP CREATE SITE------------------------------------
    private boolean isSiteInputValid() {
        clearErrorMessage();
        int invalidCount = 0;
        
        // Get input values
        String siteName = fragmentCreateSiteBinding.createSiteName.getText().toString();
        String siteDesc = fragmentCreateSiteBinding.createSiteDesc.getText().toString();
        String siteAddress = fragmentCreateSiteBinding.addressDisplay.getText().toString();
        String volunteerCapText = fragmentCreateSiteBinding.volunteerCap.getText().toString();
        String donorCapText = fragmentCreateSiteBinding.donorCap.getText().toString();
        
        if (!FieldValidation.isValidStringInRange(siteName, 6, 15)) {
            turnOnErrorMessage(fragmentCreateSiteBinding.createSiteNameError, true);
            invalidCount++;
        }
        
        if (!FieldValidation.isValidStringInRange(siteDesc, 0, 25)) {
            turnOnErrorMessage(fragmentCreateSiteBinding.createSiteDescErr, true);
            invalidCount++;
        }
        
        if (siteAddress.isEmpty()) {
            turnOnErrorMessage(fragmentCreateSiteBinding.createSiteAddressErr, true);
            invalidCount++;
        }
        
        try {
            if (volunteerCapText.isEmpty()) {
                turnOnErrorMessage(fragmentCreateSiteBinding.createVolunteerCapErr, true);
                invalidCount++;
            } else {
                int volunteerCap = Integer.parseInt(volunteerCapText);
                if (!FieldValidation.isValidNumberInRange(volunteerCap, 1, 20)) {
                    turnOnErrorMessage(fragmentCreateSiteBinding.createVolunteerCapErr, true);
                    invalidCount++;
                }
            }
            
            if (donorCapText.isEmpty()) {
                turnOnErrorMessage(fragmentCreateSiteBinding.createDonorCapErr, true);
                invalidCount++;
            } else {
                int donorCap = Integer.parseInt(donorCapText);
                if (!FieldValidation.isValidNumberInRange(donorCap, 1, 20)) {
                    turnOnErrorMessage(fragmentCreateSiteBinding.createDonorCapErr, true);
                    invalidCount++;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Please enter valid numbers for capacity", Toast.LENGTH_SHORT).show();
            invalidCount++;
        }
        
        return invalidCount == 0;
    }
    
    private void createSite() {
        if (!isSiteInputValid()) {
            Toast.makeText(requireContext(), "ERROR: Some input are invalid!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Current User: NULL", Toast.LENGTH_SHORT).show();
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

//        siteViewModel.setUserViewModel(userViewModel);
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
                                    if (imageUriList.size() < 3) {
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
    private void setUpInitialState() {
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
    //----------------------------------------SET UP IMAGE ZOOM AND DELETE -------------------------
    
    @SuppressLint("SetTextI18n")
    @Override
    public void clickDelete(int leftNum) {
        fragmentCreateSiteBinding.imageCount.setText(leftNum + "/3");
        
        if (imageUriList.isEmpty()) {
            fragmentCreateSiteBinding.defaultImageLayout.setVisibility(View.VISIBLE);
        } else {
            fragmentCreateSiteBinding.defaultImageLayout.setVisibility(View.INVISIBLE);
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
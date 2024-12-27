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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sontung.blood.R;
import com.sontung.blood.adapter.ImageAdapter;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.FragmentCreateEventBinding;
import com.sontung.blood.databinding.FragmentEditEventBinding;
import com.sontung.blood.model.Address;
import com.sontung.blood.model.Notification;
import com.sontung.blood.model.Site;
import com.sontung.blood.model.User;
import com.sontung.blood.shared.Coordinates;
import com.sontung.blood.utils.DateFormatter;
import com.sontung.blood.utils.FieldValidation;
import com.sontung.blood.viewmodel.ImageViewModel;
import com.sontung.blood.viewmodel.NotificationViewModel;
import com.sontung.blood.viewmodel.SiteViewModel;
import com.sontung.blood.viewmodel.UserViewModel;
import com.sontung.blood.views.EventActivity;
import com.sontung.blood.views.EventDetailActivity;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EditEventFragment
        extends Fragment
        implements ImageAdapter.OnItemCountAfterDelete, ImageAdapter.OnItemZoom {

    private FragmentEditEventBinding binding;
    private UserViewModel userViewModel;
    private SiteViewModel siteViewModel;
    private ImageViewModel imageViewModel;
    private NotificationViewModel notificationViewModel;

    // Google Map displaying
    private View mapPanel;
    private Marker marker;
    private GoogleMap map;
    private LatLng coordinates;

    private final List<Uri> imageUriList = new ArrayList<>();
    private ImageAdapter imageAdapter;

    private final List<Address> addressList = Coordinates.CREATE_ADDRESS_AVAILABLE;

    private Spinner bloodTypeSpinner;
    private ArrayAdapter<CharSequence> bloodTypesAdapter;

    private Spinner addressSpinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentEditEventBinding.inflate(getLayoutInflater());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_edit_event, container, false);
        binding.siteDisplayingText.setVisibility(View.GONE);
        binding.createEventLayout.setVisibility(View.GONE);

        setUpInitialState();
        setUpAddressSpinner();
        setUpBloodTypeSpinner();

        imageAdapter = new ImageAdapter(requireContext(), this, this);

        fetchSiteDataIntoView();

        return binding.getRoot();
    }


    //----------------------------------------SET UP MAP VIEWS--------------------------------------
    private void updateMapWithCoordinates(LatLng cor) {
        if (map == null || marker == null) return;

        marker.setPosition(cor);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cor, 15f));
        if (mapPanel.getVisibility() == View.GONE) {
            mapPanel.setVisibility(View.VISIBLE);
        }
    }

    //----------------------------------------NEW SET UP MAP FRAGMENT-------------------------------
    private void setUpMapFragment(LatLng coordinates) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapPanel = binding.stubMap.inflate();

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
        bloodTypeSpinner = binding.bloodTypeSpinner;
        bloodTypesAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.blood_types,
                android.R.layout.simple_spinner_item
        );
        bloodTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeSpinner.setAdapter(bloodTypesAdapter);

        bloodTypeSpinner.setSelection(0);
    }

    private void setUpAddressSpinner() {
        addressSpinner = binding.addressSpinner;
        ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                addressList.stream().map(Address::getName).collect(Collectors.toList())
        );

        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressSpinner.setAdapter(addressAdapter);

        addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Address selectedAddress = addressList.get(position);
                coordinates = selectedAddress.getCoordinates();
                binding.addressDisplay.setText(selectedAddress.getAddress());

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

    private void setUpButtonClickHandler(Site site, User host) {
        binding.addImageBtn.setOnClickListener(v -> openFile());
        binding.createSiteButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "UPDATE SITE", Toast.LENGTH_SHORT).show();
            updateSite(site, host);
        });
    }

    private void fetchSiteDataIntoView() {
        userViewModel.getUserDataById(userViewModel.getCurrentUserId(), new FirebaseCallback<>() {
            @Override
            public void onSuccess(List<User> t) {

            }

            @Override
            public void onSuccess(User user) {
                if (user.getHostedSite() == null) {
                    binding.siteDisplayingText.setVisibility(View.VISIBLE);
                    binding.createEventLayout.setVisibility(View.GONE);

                } else {
                    binding.siteDisplayingText.setVisibility(View.GONE);
                    binding.createEventLayout.setVisibility(View.VISIBLE);

                    siteViewModel.getSiteDataById(user.getHostedSite(), new FirebaseCallback<>() {
                        @Override
                        public void onSuccess(List<Site> t) {

                        }

                        @Override
                        public void onSuccess(Site site) {
                            setUpSiteImageDetail(site);
                            setUpSiteDetail(site);
                            setUpSiteSpinnerDetail(site);
                            setUpSiteEventDate(site);
                            setUpButtonClickHandler(site, user);
                        }

                        @Override
                        public void onFailure(List<Site> t) {

                        }

                        @Override
                        public void onFailure(Site site) {

                        }
                    });
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

    @SuppressLint("SetTextI18n")
    private void setUpSiteImageDetail(Site site) {
        if (site.getSiteImageUrl().isEmpty()) {
            binding.defaultImageLayout.setVisibility(View.VISIBLE);
        } else {
            binding.defaultImageLayout.setVisibility(View.GONE);
        }

        for (String imageUrl : site.getSiteImageUrl()) {
            imageUriList.add(Uri.parse(imageUrl));
        }
        binding.imageCount.setText(imageUriList.size() + "/3");

        imageAdapter.setData(imageUriList);
        binding.imageRecyclerView.setAdapter(imageAdapter);
    }

    private void setUpSiteDetail(Site site) {
        binding.createSiteName.setText(site.getSiteName());
        binding.createSiteDesc.setText(site.getSiteDesc());
        binding.donorCap.setText(String.valueOf(site.getDonorMaxCapacity()));
        binding.volunteerCap.setText(String.valueOf(site.getVolunteerMaxCapacity()));
    }

    private void setUpSiteSpinnerDetail(Site site) {
        int bloodTypePosition = bloodTypesAdapter.getPosition(site.getRequiredBloodType());
        bloodTypeSpinner.setSelection(bloodTypePosition);

        String siteAddress = site.getSiteAddress();
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).getAddress().equals(siteAddress)) {
                addressSpinner.setSelection(i);
                break;
            }
        }
    }

    private void setUpSiteEventDate(Site site) {
        binding.createSiteDate.setText(DateFormatter.toDateString(site.getEventDate()));
    }

    //----------------------------------------SET UP CREATE SITE------------------------------------
    private boolean isSiteInputValid() {
        clearErrorMessage();
        int invalidCount = 0;

        String siteName = binding.createSiteName.getText().toString().trim();
        String siteDesc = binding.createSiteDesc.getText().toString().trim();
        String siteAddress = binding.addressDisplay.getText().toString().trim();

        if (imageUriList.isEmpty()) {
            turnOnErrorMessage(binding.createAddImageErr, true);
            invalidCount++;
        }

        if (FieldValidation.isValidStringInRange(siteName, 6, 15)) {
            turnOnErrorMessage(binding.createSiteNameError, true);
            invalidCount++;
        }

        if (FieldValidation.isValidStringInRange(siteDesc, 10, 25)) {
            turnOnErrorMessage(binding.createSiteDescErr, true);
            invalidCount++;
        }

        if (siteAddress.isEmpty()) {
            turnOnErrorMessage(binding.createSiteAddressErr, true);
            invalidCount++;
        }

        return invalidCount == 0;
    }

    private void updateSite(Site site, User host) {
        if (!isSiteInputValid()) {
            Toast.makeText(requireContext(), "ERROR: Some input are invalid!", Toast.LENGTH_SHORT).show();
            return;
        }

        Site pendingUpdatedSite =
                Site.builder()
                        .host(userViewModel.getCurrentUserId())
                        .siteId(site.getSiteId())
                        .siteName(binding.createSiteName.getText().toString().trim())
                        .siteDesc(binding.createSiteDesc.getText().toString().trim())
                        .siteAddress(binding.addressDisplay.getText().toString().trim())
                        .siteImageUrl(imageUriList.stream().map(Uri::toString).collect(Collectors.toList()))
                        .requiredBloodType(binding.bloodTypeSpinner.getSelectedItem().toString().trim())
                        .donorMaxCapacity(Integer.parseInt(binding.donorCap.getText().toString().trim()))
                        .volunteerMaxCapacity(Integer.parseInt(binding.volunteerCap.getText().toString().trim()))
                        .latitude(String.valueOf(coordinates.latitude))
                        .longitude(String.valueOf(coordinates.longitude))
                        .listOfDonors(site.getListOfDonors())
                        .listOfVolunteers(site.getListOfVolunteers())
                        .listOfReports(site.getListOfReports())
                        .eventDate(DateFormatter.toDate(binding.createSiteDate.getText().toString().trim()))
                        .build();

        siteViewModel.updateSite(site.getSiteId(), pendingUpdatedSite, new FirebaseCallback<Site>() {
            @Override
            public void onSuccess(List<Site> t) {
            }

            @Override
            public void onSuccess(Site site) {
                List<Uri> updatedImages = imageUriList.stream().filter((uri) -> !uri.toString().contains("https://firebasestorage.googleapis.com")).collect(Collectors.toList());

                if (updatedImages.isEmpty()){
                    sendNotifications(site, host);
                    Intent i = new Intent(getContext(), EventDetailActivity.class);
                    i.putExtra("SITE_ID", site.getSiteId());
                    startActivity(i);
                    return;
                }
                imageViewModel.uploadSiteImageToStorage(updatedImages, site.getSiteId(), new FirebaseCallback<>() {
                    @Override
                    public void onSuccess(List<String> imageUrls) {
                        pendingUpdatedSite.setSiteImageUrl(imageUrls);
                        siteViewModel.updateSiteImages(site.getSiteId(), pendingUpdatedSite);
                        sendNotifications(site, host);
                        Intent i = new Intent(getContext(), EventDetailActivity.class);
                        i.putExtra("SITE_ID", site.getSiteId());
                        startActivity(i);
                    }

                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onFailure(List<String> t) {

                    }

                    @Override
                    public void onFailure(String s) {

                    }
                });
            }

            @Override
            public void onFailure(List<Site> t) {
            }

            @Override
            public void onFailure(Site site) {
            }
        });


    }

    //----------------------------------------SET UP IMAGE UPLOADING--------------------------------
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("image/*")
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        chooseImageAction.launch(intent);
    }

    private void sendNotifications(Site site, User host) {
        List<String> listOfAllSiteMembers = site.getListOfDonors();
        listOfAllSiteMembers.addAll(site.getListOfVolunteers());
        listOfAllSiteMembers.remove(site.getHost());

        String message = "We’ve updated our blood donation site. Check it out!";

        for (String memberId : listOfAllSiteMembers) {
            Notification pendingSentNotification =
                    Notification.builder()
                            .senderId(host.getUserId())
                            .senderEmail(host.getEmail())
                            .receiverId(memberId)
                            .siteId(site.getSiteId())
                            .title("SITE UPDATED")
                            .desc(message)
                            .build();

            notificationViewModel.createNotification(pendingSentNotification, new FirebaseCallback<>() {
                @Override
                public void onSuccess(List<Notification> t) {

                }

                @Override
                public void onSuccess(Notification notification) {
                    notificationViewModel.updateNotificationId(notification.getNotificationId(), notification);

                }

                @Override
                public void onFailure(List<Notification> t) {

                }

                @Override
                public void onFailure(Notification notification) {

                }
            });
        }
    }

    private final ActivityResultLauncher<Intent> chooseImageAction =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<>() {
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
                                    binding.defaultImageLayout.setVisibility(View.GONE);
                                } else {
                                    binding.defaultImageLayout.setVisibility(View.VISIBLE);
                                }

                                binding.imageCount.setText(imageUriList.size() + "/3");
                            }
                        }
                    });

    //----------------------------------------SET UP TOOLS FUNCTION---------------------------------
    private void setUpInitialState() {
        binding.defaultImageLayout.setVisibility(View.VISIBLE);
        binding.donorCap.setEnabled(false);
        binding.volunteerCap.setEnabled(false);
        binding.bloodTypeSpinner.setEnabled(false);
        binding.createSiteDate.setEnabled(false);
        binding.createSiteDate.setFocusable(false);

        clearErrorMessage();
    }

    private void clearErrorMessage() {
        turnOnErrorMessage(binding.createAddImageErr, false);
        turnOnErrorMessage(binding.createSiteNameError, false);
        turnOnErrorMessage(binding.createSiteDescErr, false);
        turnOnErrorMessage(binding.createVolunteerCapErr, false);
        turnOnErrorMessage(binding.createDonorCapErr, false);
        turnOnErrorMessage(binding.createSiteAddressErr, false);
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
        binding.imageCount.setText(leftNum + "/3");

        if (imageUriList.isEmpty()) {
            binding.defaultImageLayout.setVisibility(View.VISIBLE);
        } else {
            binding.defaultImageLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void itemZoomClick(int position) {
        showZoomDialog(imageUriList.get(position));
    }

    private void showZoomDialog(Uri imageUri) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ImageView imageView = new ImageView(requireContext());

        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(this)
                .load(imageUri)
                .into(imageView);

        dialog.setContentView(imageView);
        dialog.show();

        imageView.setOnClickListener(v -> dialog.dismiss());
    }
}
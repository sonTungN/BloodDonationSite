package com.sontung.blood.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.sontung.blood.R;
import com.sontung.blood.adapter.ImageAdapter;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.ActivitySignUpBinding;
import com.sontung.blood.model.User;
import com.sontung.blood.shared.Types;
import com.sontung.blood.utils.FieldValidation;
import com.sontung.blood.viewmodel.ImageViewModel;
import com.sontung.blood.viewmodel.UserViewModel;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class SignUpActivity
        extends AppCompatActivity
        implements ImageAdapter.OnItemCountAfterDelete, ImageAdapter.OnItemZoom {

    private ActivitySignUpBinding binding;
    private UserViewModel userViewModel;
    private ImageViewModel imageViewModel;
    
    // ImageUploading
    private Uri profileAvatar;

    // Widget
    private Spinner bloodTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        
        setUpBloodTypeSpinner();
        setUpInitialState();
        setUpButtonClickHandlerEvent();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    //----------------------------------------SET UP VIEW LAYOUT------------------------------------
    private void setUpBloodTypeSpinner() {
        bloodTypeSpinner = binding.signupBloodType;
        // Create an ArrayAdapter from the resource
        ArrayAdapter<CharSequence> bloodTypesAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_types,
                android.R.layout.simple_spinner_item
        );
        bloodTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeSpinner.setAdapter(bloodTypesAdapter);
    }
    
    private void setUpButtonClickHandlerEvent() {
        binding.addAvatarBtn.setOnClickListener(v -> openFile());
        
        binding.signupBtn.setOnClickListener(v -> signUpUser());

        binding.signinCta.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });
    }
    
    private void signUpUser() {
        if (!isSiteInputValid()) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }
        
        User pendingCreatedUser =
                User.builder()
                        .email(binding.signupEmail.getText().toString().trim())
                        .password(binding.signupPassword.getText().toString().trim())
                        .username(binding.signupDisplayName.getText().toString().trim())
                        .bloodType(bloodTypeSpinner.getSelectedItem().toString())
                        .userRole(Types.USER_PERMISSION)
                        .build();
        
        userViewModel.signUpUser(pendingCreatedUser, new FirebaseCallback<User>() {
            @Override
            public void onSuccess(List<User> t) {
            
            }
            
            @Override
            public void onSuccess(User user) {
                imageViewModel.uploadUserAvatarToStorage(profileAvatar, user.getUserId(), new FirebaseCallback<String>() {
                    @Override
                    public void onSuccess(List<String> t) {
                    
                    }
                    
                    @Override
                    public void onSuccess(String imageUrl) {
                        user.setProfileUrl(imageUrl);
                        userViewModel.updateUserProfileAvatar(user.getUserId(), user);
                        
                        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(i);
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
            public void onFailure(List<User> t) {
            
            }
            
            @Override
            public void onFailure(User user) {
            
            }
        });
    }
    
    //----------------------------------------SET UP IMAGE UPLOADING--------------------------------
    private void openFile() {
        Intent intent =
                new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        chooseImageAction.launch(intent);
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
                                profileAvatar = intentData.getData();
                                
                                InputStream chooseProfileAvatar;
                                try {
                                    chooseProfileAvatar = getContentResolver().openInputStream(profileAvatar);
                                    Bitmap bitmap = BitmapFactory.decodeStream(chooseProfileAvatar);
                                    binding.signUpAvatar.setImageBitmap(bitmap);
                                    
                                } catch (FileNotFoundException e) {
                                    Toast.makeText(SignUpActivity.this, "Error updating choosing image to view", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
    
    //----------------------------------------SET UP TOOLS FUNCTION---------------------------------
    private boolean isSiteInputValid() {
        clearErrorMessage();
        int invalidCount = 0;
        
        String siteName = binding.signupDisplayName.getText().toString();
        String siteDesc = binding.signupEmail.getText().toString();
        String siteAddress = binding.signupPassword.getText().toString();
        
        if (!FieldValidation.isValidStringInRange(siteName, 6, 15)) {
            turnOnErrorMessage(binding.signupDisplayNameErr, true);
            invalidCount++;
        }
        
        if (!FieldValidation.isValidStringInRange(siteDesc, 0, 25)) {
            turnOnErrorMessage(binding.signupEmailErr, true);
            invalidCount++;
        }
        
        if (siteAddress.isEmpty()) {
            turnOnErrorMessage(binding.signupPasswordErr, true);
            invalidCount++;
        }
        
        return invalidCount == 0;
    }
    
    private void setUpInitialState() {
        binding.signUpAvatar.setImageResource(R.drawable.img_default_avatar);
        clearErrorMessage();
        bloodTypeSpinner.setSelection(0);
    }
    
    private void clearErrorMessage() {
        turnOnErrorMessage(binding.signupDisplayNameErr, false);
        turnOnErrorMessage(binding.signupEmailErr, false);
        turnOnErrorMessage(binding.signupPasswordErr, false);
    }
    
    private void turnOnErrorMessage(View view, Boolean isError) {
        if (isError) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void clickDelete(int leftNum) {
    
    }
    
    @Override
    public void itemZoomClick(int position) {
    
    }
}
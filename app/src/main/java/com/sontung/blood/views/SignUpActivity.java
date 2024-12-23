package com.sontung.blood.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.sontung.blood.R;
import com.sontung.blood.databinding.ActivitySignUpBinding;
import com.sontung.blood.utils.FieldValidation;
import com.sontung.blood.viewmodel.UserViewModel;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private UserViewModel userViewModel;

    // Widget
    private Spinner bloodTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        setUpBloodTypeSpinner();
        setUpInitialState();
        
        setUpButtonClickHandlerEvent();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

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
        binding.signupBtn.setOnClickListener(v -> {
            if (!isSiteInputValid()) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String email = binding.signupEmail.getText().toString().trim();
            String password = binding.signupPassword.getText().toString().trim();
            String displayName = binding.signupDisplayName.getText().toString().trim();
            String bloodType = bloodTypeSpinner.getSelectedItem().toString();

            userViewModel.signUpUserWithEmailAndPassword(email, password, displayName, bloodType);
        });

        binding.signinCta.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });
    }
    
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
}
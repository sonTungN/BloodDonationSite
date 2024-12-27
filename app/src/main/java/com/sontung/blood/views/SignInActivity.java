package com.sontung.blood.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.sontung.blood.R;
import com.sontung.blood.databinding.ActivitySignInBinding;
import com.sontung.blood.utils.FieldValidation;
import com.sontung.blood.viewmodel.UserViewModel;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        setUpInitialState();
        setUpButtonClickHandler();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setUpButtonClickHandler() {
        binding.signinBtn.setOnClickListener(v -> {
            if (!isLogInInputValid()) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String email = binding.signinEmail.getText().toString().trim();
            String password = binding.signinPassword.getText().toString().trim();

            userViewModel.signInUserWithEmailAndPassword(email, password);
        });

        binding.signupCta.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }
    
    private boolean isLogInInputValid() {
        clearErrorMessage();
        int invalidCount = 0;
        
        String email = binding.signinEmail.getText().toString().trim();
        String password = binding.signinPassword.getText().toString().trim();
        
        if (!FieldValidation.isValidEmail(email) || email.isEmpty()) {
            turnOnErrorMessage(binding.signinEmailErr, true);
            invalidCount++;
        }
        
        if (FieldValidation.isValidStringInRange(password, 6, 15)) {
            turnOnErrorMessage(binding.signinPasswordErr, true);
            invalidCount++;
        }
        
        return invalidCount == 0;
    }
    
    private void setUpInitialState() {
        clearErrorMessage();
    }
    
    private void clearErrorMessage() {
        turnOnErrorMessage(binding.signinEmailErr, false);
        turnOnErrorMessage(binding.signinPasswordErr, false);
    }
    
    private void turnOnErrorMessage(View view, Boolean isError) {
        if (isError) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
package com.example.unicalendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


import androidx.appcompat.app.AppCompatActivity;

public class signup extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private FirebaseAuth mAuth;
    private Button signupButton;
    private TextView loginRedirectText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        signupButton = findViewById(R.id.Register);
        loginRedirectText = findViewById(R.id.existingUserLink);

        signupButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!isValidEmail(email)) {
                Toast.makeText(signup.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPasswordStrong(password)) {
                Toast.makeText(signup.this, "Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character.", Toast.LENGTH_LONG).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new
                                    Intent(signup.this, MainActivity.class));
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                // This means the email is already in use
                                Toast.makeText(signup.this, "This email is already registered. Please log in.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(signup.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }                        }
                    });
        });
        loginRedirectText.setOnClickListener(v -> startActivity(new
                Intent(signup.this, login.class)));
    }
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8
                && password.chars().anyMatch(Character::isUpperCase)
                && password.chars().anyMatch(Character::isLowerCase)
                && password.chars().anyMatch(Character::isDigit)
                && password.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{}|;:,.<>?/".indexOf(c) >= 0);
    }
}

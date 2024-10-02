package com.example.unicalendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView newUserLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in, redirect to MainActivity
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the login activity
            return; // Exit onCreate
        }

        // Bind views
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        newUserLink = findViewById(R.id.newUserLink);

        // Set onClickListener for login button
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate email
            if (!isValidEmail(email)) {
                Toast.makeText(login.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate password
            if (password.isEmpty()) {
                Toast.makeText(login.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Authentication for login
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Login success, get the current user
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String emailcheck = user.getEmail(); // Get the email of the logged-in user
                                Log.d("FirebaseUser", "Email: " + emailcheck);

                                // Pass email to MainActivity and start the activity
                                Intent intent = new Intent(login.this, MainActivity.class);
                                intent.putExtra("email", emailcheck);
                                startActivity(intent);
                                finish(); // Close the login activity
                            }
                        } else {
                            // Log the error and show a toast for login failure
                            Log.w("Login", "Login failed: ", task.getException());
                            Toast.makeText(login.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Redirect to signup activity when the user clicks on "New User? Sign up"
        newUserLink.setOnClickListener(v -> startActivity(new Intent(login.this, signup.class)));
    }

    // Method to validate the email format
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

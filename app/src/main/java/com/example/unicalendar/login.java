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

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    Button loginButton;
    TextView newUserLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        newUserLink = findViewById(R.id.newUserLink);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!isValidEmail(email)) {
                Toast.makeText(login.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(login.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Authentication for login
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Log successful login
                            Log.d("Login", "Login successful for: " + email);
                            Intent intent = new Intent(login.this, MainActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("password", password);
                            startActivity(intent);
                            finish();
                        } else {
                            // Log failure reason
                            Log.w("Login", "Login failed: ", task.getException());
                            Toast.makeText(login.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        // Redirect to signup activity
        newUserLink.setOnClickListener(v ->
                startActivity(new Intent(login.this, signup.class)));
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

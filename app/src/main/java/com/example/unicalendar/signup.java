package com.example.unicalendar;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class signup extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput, rePasswordInput;
    private Spinner roleSpinner;
    private Button signupButton;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameInput = findViewById(R.id.userName);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        rePasswordInput = findViewById(R.id.repassword);
        roleSpinner = findViewById(R.id.roles);
        signupButton = findViewById(R.id.Register);
        dbHelper = new DBHelper(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String rePassword = rePasswordInput.getText().toString().trim();
                String role = roleSpinner.getSelectedItem().toString();

                // Validate input
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty() || role.isEmpty()) {
                    Toast.makeText(signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(rePassword)) {
                    Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Handle user roles
                if (role.equals("Student") || role.equals("Faculty")) {
                    registerUser(username, email, password, role);
                } else if (role.equals("Club Authority")) {
                    handleClubAuthorityRequest(username, email, password);
                }
            }
        });
    }

    private void registerUser(String username, String email, String password, String role) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);
        long newRowId = db.insert("users", null, values);

        if (newRowId != -1) {
            // Save login credentials using SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UniCalPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.putString("role", role);
            editor.apply();

            // Redirect to the main/calendar page
            Intent intent = new Intent(signup.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(signup.this, "Signup failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleClubAuthorityRequest(String username, String email, String password) {
        // Store the request in the 'requests' table for admin approval
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("role", "Club Authority");
        long newRowId = db.insert("requests", null, values);

        if (newRowId != -1) {
            Toast.makeText(signup.this, "Request sent for admin approval", Toast.LENGTH_SHORT).show();
            finish();  // Close signup activity
        } else {
            Toast.makeText(signup.this, "Failed to send request", Toast.LENGTH_SHORT).show();
        }
    }
}

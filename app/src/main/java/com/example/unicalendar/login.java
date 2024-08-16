package com.example.unicalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView newUserLink;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        newUserLink = findViewById(R.id.newUserLink);
        dbHelper = new DBHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UniCalPrefs", MODE_PRIVATE);
        if (sharedPreferences.contains("email")) {
            // Directly go to MainActivity if user is already logged in
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (validateLogin(email, password)) {
                    // Save login credentials using SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.apply();

                    // Redirect to MainActivity
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        newUserLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to SignUpActivity
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateLogin(String email, String password) {
        // Check for special admin credentials
        if (email.equals("admin") && password.equals("admin@root")) {
            return true; // Special admin login
        }

        // Check for regular user credentials
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { "email", "password" };
        String selection = "email = ? AND password = ?";
        String[] selectionArgs = { email, password };

        Cursor cursor = db.query(
                "users",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
}

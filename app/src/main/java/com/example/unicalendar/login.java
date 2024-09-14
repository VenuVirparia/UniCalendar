package com.example.unicalendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

//    private EditText emailInput, passwordInput;
//    private Button loginButton;
//    private TextView newUserLink;
//    private DBHelper dbHelper;

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
        //dbHelper = new DBHelper(this);


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

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new
                                    Intent(login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(login.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        newUserLink.setOnClickListener(v ->
                startActivity(new Intent(login.this,
                        signup.class)));
    }
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

//        SharedPreferences sharedPreferences = getSharedPreferences("UniCalPrefs", MODE_PRIVATE);
//        if (sharedPreferences.contains(emailInput.getText().toString()7)) {
//            // Directly go to MainActivity if user is already logged in
//            Intent intent = new Intent(login.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = emailInput.getText().toString().trim();
//                String password = passwordInput.getText().toString().trim();
//
//                if (email.isEmpty() || password.isEmpty()) {
//                    Toast.makeText(login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (validateLogin(email, password)) {
//                    // Save login credentials using SharedPreferences
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("email", email);
//                    editor.apply();
//
//                    // Redirect to MainActivity
//                    Intent intent = new Intent(login.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Toast.makeText(login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        newUserLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Redirect to SignUpActivity
//                Intent intent = new Intent(login.this, signup.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private boolean validateLogin(String email, String password) {
//        // Check for special admin credentials
//        if (email.equals("admin") && password.equals("admin@root")) {
//            return true; // Special admin login
//        }
//
//        // Check for regular user credentials
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        String[] projection = { "email", "password" };
//        String selection = "email = ? AND password = ?";
//        String[] selectionArgs = { email, password };
//
//        Cursor cursor = db.query(
//                "users",
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                null
//        );
//
//        boolean isValid = cursor.getCount() > 0;
//        cursor.close();
//        return isValid;
//    }
//}

package com.example.unicalendar;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        ImageView imageView = findViewById(R.id.imageView);
        TextView uniCalendarText = findViewById(R.id.UniCalendar);
        TextView tagLineText = findViewById(R.id.tagLine);
        TextView developerNameText = findViewById(R.id.developerName);

        // Apply the animation to the views
        imageView.startAnimation(fadeInAnimation);
        uniCalendarText.startAnimation(fadeInAnimation);
        tagLineText.startAnimation(fadeInAnimation);
        developerNameText.startAnimation(fadeInAnimation);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check login status
                SharedPreferences sharedPreferences = getSharedPreferences("UniCalPrefs", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.contains("email"); // Check if email exists in SharedPreferences

                if (isLoggedIn) {
                    // User is logged in, redirect to MainActivity
                    startActivity(new Intent(splashScreen.this, MainActivity.class));
                } else {
                    // User is not logged in, redirect to LoginActivity
                    startActivity(new Intent(splashScreen.this, login.class));
                }

                finish(); // Close the splash screen activity after redirecting
            }
        }, 3000);
    }
}

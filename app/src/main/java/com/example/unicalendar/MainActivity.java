package com.example.unicalendar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.threetenabp.AndroidThreeTen;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    // Set the same size for ic_profile as the logo




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        AndroidThreeTen.init(this);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);  // Disable the home button (hamburger menu)


//        Button testButton = findViewById(R.id.test_firebase_button);
//
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Pass MainActivity.this instead of "this"
//                firebaseDatabaseTester tester = new firebaseDatabaseTester(MainActivity.this);
//                tester.testDatabaseWrite(); // Optional: to close MainActivity
//            }
//        });
        drawerLayout = findViewById(R.id.drawer_layout);


        Button logout_button = findViewById(R.id.logout_button);
        if (logout_button != null) {
            logout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();  // Add sign-out logic
                    startActivity(new Intent(MainActivity.this, login.class));
                    finish(); // Optional: to close MainActivity
                }
            });
        }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            TextView userEmailTextView = findViewById(R.id.user_email);  // Add this line
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                userEmailTextView.setText(userEmail);  // Display user's email
            }
            // Initialize Firebase Database reference
            databaseReference = FirebaseDatabase.getInstance().getReference("events");

            // Set up fragments
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_upper, new CalendarFragment(),"calendar_fragment")
                    .replace(R.id.fragment_container_lower, new EventListFragment())
                    .commit();

        // Set default date to today in "dd-MM-yyyy" format
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        updateEventList(today);
        }

    // Inflate the menu with the profile icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem profileMenuItem = menu.findItem(R.id.action_profile);
        if (profileMenuItem != null) {
            ImageView profileIcon = (ImageView) profileMenuItem.getActionView();
            if (profileIcon != null) {
                profileIcon.getLayoutParams().width = 50;  // Set width
                profileIcon.getLayoutParams().height = 50; // Set height
                profileIcon.requestLayout(); // Request layout update
            }
        }
        return true;
    }

    public CalendarFragment getCalendarFragment() {
        return (CalendarFragment) getSupportFragmentManager().findFragmentByTag("calendar_fragment");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            drawerLayout.openDrawer(GravityCompat.END); // Open the drawer from the right
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateEventList(String selectedDate) {
        databaseReference.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String eventId = eventSnapshot.getKey();  // Capture the Firebase event ID
                    String eventName = eventSnapshot.child("name").getValue(String.class);
                    String eventTime = eventSnapshot.child("time").getValue(String.class);
                    String eventVenue = eventSnapshot.child("venue").getValue(String.class);
                    String eventDetails = eventSnapshot.child("details").getValue(String.class);
                    String eventClub = eventSnapshot.child("club").getValue(String.class);
                    String classroomNumber = eventSnapshot.child("classroomNumber").getValue(String.class);

                    if (eventName != null) {
                        // Pass the eventId to the Event constructor
                        Event event = new Event(eventId, selectedDate, eventName, eventTime, eventVenue, eventClub, eventDetails, classroomNumber);
                        events.add(event);
                    }
                }

                // Find the EventListFragment and update he event list
                EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container_lower);

                if (eventListFragment != null) {
                    eventListFragment.updateEventList(events, selectedDate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Database error: " + databaseError.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load events. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

package com.example.unicalendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase and ThreeTen library
        FirebaseApp.initializeApp(this);
        AndroidThreeTen.init(this);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);  // Disable the home button (hamburger menu)

        // Initialize drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);

        // Set up logout button and its click listener
        Button logoutButton = findViewById(R.id.logout_button);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();  // Log out from Firebase
                    startActivity(new Intent(MainActivity.this, login.class));  // Redirect to login
                    finish();  // Close MainActivity
                }
            });
        }

        // Display the logged-in user's email in the drawer
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TextView userEmailTextView = findViewById(R.id.user_email);
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            userEmailTextView.setText(userEmail);  // Display user's email
        }

        // Initialize Firebase Database reference for events
        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        // Set up fragments: CalendarFragment in the upper part, EventListFragment in the lower part
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_upper, new CalendarFragment(), "calendar_fragment")
                .replace(R.id.fragment_container_lower, new EventListFragment())
                .commit();

        // Set default date to today in "dd-MM-yyyy" format and update event list
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        updateEventList(today);
    }

    // Inflate the menu with the profile icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Set up profile icon size
        MenuItem profileMenuItem = menu.findItem(R.id.action_profile);
        if (profileMenuItem != null) {
            ImageView profileIcon = (ImageView) profileMenuItem.getActionView();
            if (profileIcon != null) {
                profileIcon.getLayoutParams().width = 50;  // Set width
                profileIcon.getLayoutParams().height = 50; // Set height
                profileIcon.requestLayout();  // Request layout update
            }
        }
        return true;
    }

    // Handle profile menu item click to open the drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            drawerLayout.openDrawer(GravityCompat.END);  // Open drawer from the right
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Get the CalendarFragment by its tag
    public CalendarFragment getCalendarFragment() {
        return (CalendarFragment) getSupportFragmentManager().findFragmentByTag("calendar_fragment");
    }

    // Update the event list for the selected date from Firebase
    public void updateEventList(String selectedDate) {
        databaseReference.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve event details from Firebase
                    String eventId = eventSnapshot.getKey();
                    String eventName = eventSnapshot.child("name").getValue(String.class);
                    String eventTime = eventSnapshot.child("time").getValue(String.class);
                    String eventVenue = eventSnapshot.child("venue").getValue(String.class);
                    String eventDetails = eventSnapshot.child("details").getValue(String.class);
                    String eventClub = eventSnapshot.child("club").getValue(String.class);
                    String classroomNumber = eventSnapshot.child("classroomNumber").getValue(String.class);

                    // Create an Event object if eventName is not null
                    if (eventName != null) {
                        Event event = new Event(eventId, selectedDate, eventName, eventTime, eventVenue, eventClub, eventDetails, classroomNumber);
                        events.add(event);
                    }
                }

                // Update the event list in EventListFragment
                EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_lower);
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

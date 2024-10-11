package com.example.unicalendar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    // Set the same size for ic_profile as the logo



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // getSupportActionBar().setTitle("UniCalendar");
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);  // Disable the home button (hamburger menu)
        //getSupportActionBar().setLogo(R.drawable.logo);  // Set your logo here


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
                    .replace(R.id.fragment_container_upper, new CalendarFragment())
                    .replace(R.id.fragment_container_lower, new EventListFragment())
                    .commit();


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




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            drawerLayout.openDrawer(GravityCompat.END); // Open the drawer from the right
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateEventList(String selectedDate) {
        // Fetch events for the selected date from Firebase
        databaseReference.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String eventName = eventSnapshot.child("name").getValue(String.class);
                    String eventTime = eventSnapshot.child("time").getValue(String.class);
                    String eventVenue = eventSnapshot.child("venue").getValue(String.class);
                    String eventDetails = eventSnapshot.child("details").getValue(String.class);
                    events.add(new Event(eventName, eventTime, eventVenue, eventDetails));
                }

                EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container_lower);

                if (eventListFragment != null) {
                    eventListFragment.updateEventList(events);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    // Admin dialog for adding events
    public void showAddEventDialog(String selectedDate) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_event);

        EditText eventNameEditText = dialog.findViewById(R.id.event_name);
        EditText eventTimeEditText = dialog.findViewById(R.id.event_time);
        EditText eventVenueEditText = dialog.findViewById(R.id.event_venue);
        EditText eventDetailsEditText = dialog.findViewById(R.id.event_details);
        Button saveButton = dialog.findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameEditText.getText().toString();
                String eventTime = eventTimeEditText.getText().toString();
                String eventVenue = eventVenueEditText.getText().toString();
                String eventDetails = eventDetailsEditText.getText().toString();

                saveEventToDatabase(selectedDate, eventName, eventTime, eventVenue, eventDetails);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveEventToDatabase(String selectedDate, String name, String time, String venue, String details) {
        DatabaseReference eventRef = databaseReference.child(selectedDate).push();
        eventRef.child("name").setValue(name);
        eventRef.child("time").setValue(time);
        eventRef.child("venue").setValue(venue);
        eventRef.child("details").setValue(details);
    }
}

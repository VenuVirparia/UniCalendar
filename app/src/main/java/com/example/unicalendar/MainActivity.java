package com.example.unicalendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            // Open profile activity
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to update the event list in EventListFragment
    public void updateEventList(String selectedDate) {
        // Simulate fetching events from the database for the selected date
        ArrayList<String> events = fetchEventsForDate(selectedDate);

        // Find the EventListFragment
        EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_lower);

        if (eventListFragment != null) {
            // Pass the events to the fragment to update the list
            eventListFragment.updateEventList(events);
        }
    }

    // Simulated method to fetch events for a specific date (replace with actual database logic)
    private ArrayList<String> fetchEventsForDate(String date) {
        ArrayList<String> events = new ArrayList<>();
        events.add("Event 1 - Venue A - 10:00 AM");
        events.add("Event 2 - Venue B - 12:00 PM");
        events.add("Event 3 - Venue C - 2:00 PM");
        return events;
    }
}

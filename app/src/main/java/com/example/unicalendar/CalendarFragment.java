package com.example.unicalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CalendarFragment extends Fragment {

    private String selectedDate;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        FloatingActionButton fab = view.findViewById(R.id.fab_admin);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        // Get current logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            Log.d("CalendarFragment", "User Email: " + userEmail);

            // Check if the user is an admin
            if ("admin@gmail.com".equals(userEmail)) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        } else {
            Log.d("CalendarFragment", "No user is logged in.");
            fab.setVisibility(View.GONE);
        }

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

            // Update the event list for the selected date
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateEventList(selectedDate);
            }
        });

        // Handle admin FAB click for adding events
        fab.setOnClickListener(v -> openEventDialog());

        return view;
    }

    private void openEventDialog() {
        Log.d("CalendarFragment", "FAB clicked, opening dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_event, null);

        builder.setView(dialogView);
        builder.setTitle("Add Event on " + selectedDate);

        EditText eventNameEditText = dialogView.findViewById(R.id.event_name);
        EditText eventTimeEditText = dialogView.findViewById(R.id.event_time);
        EditText eventVenueEditText = dialogView.findViewById(R.id.event_venue);
        EditText eventDetailsEditText = dialogView.findViewById(R.id.event_details);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String eventName = eventNameEditText.getText().toString();
            String eventTime = eventTimeEditText.getText().toString();
            String eventVenue = eventVenueEditText.getText().toString();
            String eventDetails = eventDetailsEditText.getText().toString();

            if (!eventName.isEmpty() && !eventTime.isEmpty() && !eventVenue.isEmpty()) {
                saveEventToFirebase(eventName, eventTime, eventVenue, eventDetails);
            } else {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Save event to Firebase under the selected date
    private void saveEventToFirebase(String name, String time, String venue, String details) {
        DatabaseReference eventRef = databaseReference.child(selectedDate).push();
        eventRef.child("name").setValue(name);
        eventRef.child("time").setValue(time);
        eventRef.child("venue").setValue(venue);
        eventRef.child("details").setValue(details);
        Toast.makeText(getContext(), "Event added successfully", Toast.LENGTH_SHORT).show();
    }
}

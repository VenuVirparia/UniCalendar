//package com.example.unicalendar;
//
//import android.app.AlertDialog;
//import android.app.TimePickerDialog;
//import android.os.Bundle;
//import android.view.LayoutInflater;

//import android.widget.ArrayAdapter;
//import java.util.ArrayList;
//import java.util.Locale;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.AppCompatSpinner;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.Calendar;
//import java.util.List;
//
//public class EventListFragment extends Fragment implements EventAdapter.OnEventClickListener{
//
//    private RecyclerView eventRecyclerView;
//    private EventAdapter eventAdapter;
//    private List<Event> eventList;
//    private DatabaseReference eventDatabaseRef;
//    TextView titleTextView;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
//        titleTextView = view.findViewById(R.id.eventListTitle);
//
//        eventRecyclerView = view.findViewById(R.id.eventListRecyclerView);
//        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        eventList = new ArrayList<>();
//        eventAdapter = new EventAdapter((ArrayList<Event>) eventList, this);
//        eventRecyclerView.setAdapter(eventAdapter);
//
//        // Reference to the 'events' node in Firebase
//        eventDatabaseRef = FirebaseDatabase.getInstance().getReference("events");
//
//        // Fetching the events
//        fetchEvents();
//
//        return view;
//    }
//
//    // Fetch all events from Firebase
//    private void fetchEvents() {
//        eventDatabaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                eventList.clear(); // Clear previous list
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Event event = snapshot.getValue(Event.class);
//                    if (event != null) {
//                        eventList.add(event); // Add event to list
//                    }
//                }
//                eventAdapter.notifyDataSetChanged(); // Update RecyclerView
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Show edit event dialog
//    private void showEditEventDialog(Event event) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_edit_event, null);
//        builder.setView(dialogView);
//
//        // Initialize views
//        EditText eventNameEditText = dialogView.findViewById(R.id.event_name);
//        TextView eventTimeTextView = dialogView.findViewById(R.id.event_time);
//        AppCompatSpinner eventVenueSpinner = dialogView.findViewById(R.id.venue_spinner);
//        EditText eventDetailsEditText = dialogView.findViewById(R.id.event_details);
//        AppCompatSpinner eventClubSpinner = dialogView.findViewById(R.id.club_spinner);
//        EditText eventClassroomNumberEditText = dialogView.findViewById(R.id.event_classroom_number);
//        Button saveButton = dialogView.findViewById(R.id.save_button);
//
//        // Set initial values from the event object
//        eventNameEditText.setText(event.getName());
//        eventTimeTextView.setText(event.getTime());
//        eventDetailsEditText.setText(event.getDetails());
//        eventClassroomNumberEditText.setText(event.getClassroomNumber());
//
//        // Load venues and clubs into the spinners
//        getVenues(eventVenueSpinner);
//        getClubs(eventClubSpinner);
//
//        // Time picker for the event time
//        eventTimeTextView.setOnClickListener(v -> {
//            Calendar currentTime = Calendar.getInstance();
//            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
//            int minute = currentTime.get(Calendar.MINUTE);
//
//            TimePickerDialog timePicker = new TimePickerDialog(getContext(), (view, hourOfDay, minuteOfHour) -> {
//                eventTimeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour));
//            }, hour, minute, true);
//            timePicker.show();
//        });
//
//        // Save button logic
//        saveButton.setOnClickListener(v -> {
//            // Retrieve updated values
//            String updatedName = eventNameEditText.getText().toString();
//            String updatedTime = eventTimeTextView.getText().toString();
//            String updatedVenue = eventVenueSpinner.getSelectedItem().toString();
//            String updatedDetails = eventDetailsEditText.getText().toString();
//            String updatedClub = eventClubSpinner.getSelectedItem().toString();
//            String updatedClassroom = eventClassroomNumberEditText.getText().toString();
//
//            // Update the event in Firebase
//            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getId());
//            eventRef.child("name").setValue(updatedName);
//            eventRef.child("time").setValue(updatedTime);
//            eventRef.child("venue").setValue(updatedVenue);
//            eventRef.child("details").setValue(updatedDetails);
//            eventRef.child("club").setValue(updatedClub);
//            eventRef.child("classroomNumber").setValue(updatedClassroom)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getContext(), "Event Edit Successful", Toast.LENGTH_SHORT).show();
//
//                            // Reflect changes in the UI
//                            event.setName(updatedName);
//                            event.setTime(updatedTime);
//                            event.setVenue(updatedVenue);
//                            event.setDetails(updatedDetails);
//                            event.setClub(updatedClub);
//                            event.setClassroomNumber(updatedClassroom);
//
//                            eventAdapter.notifyDataSetChanged(); // Update UI
//                        } else {
//                            Toast.makeText(getContext(), "Event Edit Unsuccessful", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//            builder.create().dismiss(); // Close dialog
//        });
//
//        builder.show();
//    }
//
//    // Fetch venues from Firebase and populate spinner
//    private void getVenues(AppCompatSpinner venueSpinner) {
//        DatabaseReference venuesRef = FirebaseDatabase.getInstance().getReference("venues");
//        venuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<String> venueList = new ArrayList<>();
//                for (DataSnapshot venueSnapshot : snapshot.getChildren()) {
//                    String venue = venueSnapshot.getValue(String.class);
//                    if (venue != null) {
//                        venueList.add(venue);
//                    }
//                }
//                ArrayAdapter<String> venueAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, venueList);
//                venueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                venueSpinner.setAdapter(venueAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Failed to load venues", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Fetch clubs from Firebase and populate spinner
//    private void getClubs(AppCompatSpinner clubSpinner) {
//        DatabaseReference clubsRef = FirebaseDatabase.getInstance().getReference("clubs");
//        clubsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<String> clubList = new ArrayList<>();
//                for (DataSnapshot clubSnapshot : snapshot.getChildren()) {
//                    String club = clubSnapshot.getValue(String.class);
//                    if (club != null) {
//                        clubList.add(club);
//                    }
//                }
//                ArrayAdapter<String> clubAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, clubList);
//                clubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                clubSpinner.setAdapter(clubAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Failed to load clubs", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Show delete confirmation dialog
//    private void showDeleteEventDialog(Event event) {
//        new AlertDialog.Builder(getContext())
//                .setMessage("Are you sure you want to delete this event?")
//                .setPositiveButton("Delete", (dialog, which) -> {
//                    // Delete event from Firebase
//                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getId());
//                    eventRef.removeValue()
//                            .addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(getContext(), "Event Deleted Successfully", Toast.LENGTH_SHORT).show();
//                                    eventList.remove(event); // Remove event from the local list
//                                    eventAdapter.notifyDataSetChanged(); // Refresh RecyclerView
//                                } else {
//                                    Toast.makeText(getContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }
//
//    @Override
//    public void onEventClick(Event event) {
//        showEventDetailsDialog(event);
//    }
//
//    @Override
//    public void onEditEvent(Event event) {
//        showEditEventDialog(event);
//    }
//
//    @Override
//    public void onDeleteEvent(Event event) {
//showEventDetailsDialog(event);
//    }
//    // Method to display event details in a dialog
//    private void showEventDetailsDialog(Event event) {
//        if (getContext() != null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            LayoutInflater inflater = getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.dialog_event_details, null);
//
//            builder.setView(dialogView);
//
//            TextView eventName = dialogView.findViewById(R.id.event_name);
//            TextView eventTime = dialogView.findViewById(R.id.event_time);
//            TextView eventVenue = dialogView.findViewById(R.id.event_venue);
//            TextView eventDetails = dialogView.findViewById(R.id.event_details);
//            TextView eventClub = dialogView.findViewById(R.id.event_club);
//            TextView eventClassroom = dialogView.findViewById(R.id.event_classroom);
//
//            eventName.setText(event.getName());
//            eventTime.setText("Time: " + event.getTime());
//            eventVenue.setText("Venue: " + event.getVenue());
//            eventDetails.setText("Details: " + event.getDetails());
//            eventClub.setText("Club: " + event.getClub());
//
//            if (event.getVenue() != null && event.getVenue().toLowerCase().contains("classroom") && event.getClassroomNumber() != null) {
//                eventClassroom.setVisibility(View.VISIBLE);
//                eventClassroom.setText("Classroom Number: " + event.getClassroomNumber());
//            } else {
//                eventClassroom.setVisibility(View.GONE);
//            }
//
//            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
//
//            builder.show();
//        }
//    }
//    // Method to update the event list
//    public void updateEventList(ArrayList<Event> events, String date) {
//        updateTitle(date);
//        eventList.clear();
//        if (events != null && !events.isEmpty()) {
//            eventList.addAll(events);
//        }
//        eventAdapter.notifyDataSetChanged();
//    }
//    private void updateTitle(String date) {
//        titleTextView.setText(String.format("Events on %s", date));
//    }
//
//}

package com.example.unicalendar;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventListFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private RecyclerView eventListRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();
    private TextView titleTextView;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        titleTextView = view.findViewById(R.id.eventListTitle);
        eventListRecyclerView = view.findViewById(R.id.eventListRecyclerView);
        eventAdapter = new EventAdapter(eventList, this);
        eventListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventListRecyclerView.setAdapter(eventAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        // Set default date to today
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        updateTitle(today);

        // Request events for today's date
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateEventList(today);
        }

        return view;
    }

    // Method to update the event list
    public void updateEventList(ArrayList<Event> events, String date) {
        updateTitle(date);
        eventList.clear();
        if (events != null && !events.isEmpty()) {
            eventList.addAll(events);
        }
        eventAdapter.notifyDataSetChanged();
    }

    private void updateTitle(String date) {
        titleTextView.setText(String.format("Events on %s", date));
    }

    @Override
    public void onEventClick(Event event) {
        showEventDetailsDialog(event);
    }

    // Method to display event details in a dialog
    private void showEventDetailsDialog(Event event) {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_event_details, null);

            builder.setView(dialogView);

            TextView eventName = dialogView.findViewById(R.id.event_name);
            TextView eventTime = dialogView.findViewById(R.id.event_time);
            TextView eventVenue = dialogView.findViewById(R.id.event_venue);
            TextView eventDetails = dialogView.findViewById(R.id.event_details);
            TextView eventClub = dialogView.findViewById(R.id.event_club);
            TextView eventClassroom = dialogView.findViewById(R.id.event_classroom);

            eventName.setText(event.getName());
            eventTime.setText("Time: " + event.getTime());
            eventVenue.setText("Venue: " + event.getVenue());
            eventDetails.setText("Details: " + event.getDetails());
            eventClub.setText("Club: " + event.getClub());

            if (event.getVenue() != null && event.getVenue().toLowerCase().contains("classroom") && event.getClassroomNumber() != null) {
                eventClassroom.setVisibility(View.VISIBLE);
                eventClassroom.setText("Classroom Number: " + event.getClassroomNumber());
            } else {
                eventClassroom.setVisibility(View.GONE);
            }

            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

            builder.show();
        }
    }
    @Override
    public void onEditEvent(Event event){
        //showEditEventDialog(event);
        Toast.makeText(getContext(), "OnEditEvent Clicked", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onDeleteEvent(Event event){
        showDeleteEventDialog(event);
        //Toast.makeText(getContext(), "OnDeleteEvent Clicked", Toast.LENGTH_SHORT).show();
    }
    private void deleteEvent(Event event) {
        if (getActivity() instanceof MainActivity) {
            CalendarFragment calendarFragment = ((MainActivity) getActivity()).getCalendarFragment();
            if (calendarFragment != null) {
                calendarFragment.deleteEvent(event);
                // The UI update will be handled by the MainActivity's updateEventList method
            } else {
                Toast.makeText(getContext(), "Error: Calendar fragment not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Error: Unable to delete event", Toast.LENGTH_SHORT).show();
        }
    }
//    private void showEditEventDialog(Event event) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_edit_event, null);
//        builder.setView(dialogView);
//
//        EditText eventNameEditText = dialogView.findViewById(R.id.event_name);
//        TextView eventTimeTextView = dialogView.findViewById(R.id.event_time);
//        Spinner venueSpinner = dialogView.findViewById(R.id.venue_spinner);
//        Spinner clubSpinner = dialogView.findViewById(R.id.club_spinner);
//        EditText eventDetailsEditText = dialogView.findViewById(R.id.event_details);
//        EditText classroomNumberEditText = dialogView.findViewById(R.id.event_classroom_number);
//
//        // Set current values
//        eventNameEditText.setText(event.getName());
//        eventTimeTextView.setText(event.getTime());
//        eventDetailsEditText.setText(event.getDetails());
//        classroomNumberEditText.setText(event.getClassroomNumber());
//
//        // Populate spinners (you'll need to implement these methods)
//        populateVenueSpinner(venueSpinner, event.getVenue());
//        populateClubSpinner(clubSpinner, event.getClub());
//
//        // Time picker
//        eventTimeTextView.setOnClickListener(v -> {
//            Calendar calendar = Calendar.getInstance();
//            new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
//                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
//                eventTimeTextView.setText(time);
//            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
//        });
//
//        builder.setPositiveButton("Save", (dialog, which) -> {
//            // Update event with new values
//            event.setName(eventNameEditText.getText().toString());
//            event.setTime(eventTimeTextView.getText().toString());
//            event.setVenue(venueSpinner.getSelectedItem().toString());
//            event.setClub(clubSpinner.getSelectedItem().toString());
//            event.setDetails(eventDetailsEditText.getText().toString());
//            event.setClassroomNumber(classroomNumberEditText.getText().toString());
//
//            // Update in Firebase
//            updateEventInFirebase(event);
//        });
//
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//
//        builder.show();
//    }

    private void showDeleteEventDialog(Event event) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEvent(event))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

//    private void updateEventInFirebase(Event event) {
//        String dateKey = event.getDateKey();
//        DatabaseReference eventRef = databaseReference.child(dateKey).child(event.getId());
//        eventRef.setValue(event).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
//                eventAdapter.notifyDataSetChanged();
//            } else {
//                Toast.makeText(getContext(), "Failed to update event", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void deleteEventFromFirebase(Event event) {
//
//        DatabaseReference eventRef = databaseReference.child(event.getId());
//        eventRef.removeValue().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
//                eventList.remove(event);
//                eventAdapter.notifyDataSetChanged();
//            } else {
//                Toast.makeText(getContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private String getDateKeyFromEvent(Event event) {
//        // Implement this method to extract the date key from the event
//        // For example, if you store the date in the event object, return it in the format "dd-MM-yyyy"
//        return ""; // Placeholder
//    }

//    private void populateVenueSpinner(Spinner spinner, String currentVenue) {
//        // Implement this method to populate the venue spinner
//        // Use an ArrayAdapter with a list of venues
//        // Set the selection to the currentVenue
//
//    }
//
//    private void populateClubSpinner(Spinner spinner, String currentClub) {
//        // Implement this method to populate the club spinner
//        // Use an ArrayAdapter with a list of clubs
//        // Set the selection to the currentClub
//    }
}

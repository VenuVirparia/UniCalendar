package com.example.unicalendar;
import com.jakewharton.threetenabp.AndroidThreeTen;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.app.TimePickerDialog;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;


public class CalendarFragment extends Fragment {

    private String selectedDate;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        MaterialCalendarView materialCalendarView = view.findViewById(R.id.calendarView);
        FloatingActionButton fab = view.findViewById(R.id.fab_admin);
        materialCalendarView.addDecorator(new TodayDecorator(getContext()));

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

        // Set today's date in the calendar view
        materialCalendarView.setSelectedDate(CalendarDay.today());
        // Set up date change listener
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = date.getDay() + "/" + (date.getMonth()) + "/" + date.getYear();
            String formattedDate = selectedDate.replace("/", "-"); // Convert date format for Firebase key

            // Update the event list for the selected date
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateEventList(formattedDate);
            }
        });
        // Set the default date to today
//        Calendar calendar = Calendar.getInstance();
//        selectedDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
//
//        // Set today's date in the calendar view
//        calendarView.setDate(calendar.getTimeInMillis(), true, true);
//
//        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
//            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//            String formattedDate = selectedDate.replace("/", "-"); // Convert date format for Firebase key
//
//            // Update the event list for the selected date
//            if (getActivity() instanceof MainActivity) {
//                ((MainActivity) getActivity()).updateEventList(formattedDate);
//            }
//        });

        // Handle admin FAB click for adding events
        fab.setOnClickListener(v -> openEventDialog(selectedDate));
        loadAndHighlightEvents(materialCalendarView);
        return view;
    }

    private void openEventDialog(String date) {
        Log.d("CalendarFragment", "FAB clicked, opening dialog for date: " + date);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_event, null);

        builder.setView(dialogView);
        TextView title = dialogView.findViewById(R.id.dialog_title);
        title.setText("Add Event on " + date);

        EditText eventNameEditText = dialogView.findViewById(R.id.event_name);
        TextView eventTimeEditText = dialogView.findViewById(R.id.event_time);
        Spinner clubSpinner = dialogView.findViewById(R.id.club_spinner);
        Spinner venueSpinner = dialogView.findViewById(R.id.venue_spinner);
        EditText eventDetailsEditText = dialogView.findViewById(R.id.event_details);
        ImageView closeButton = dialogView.findViewById(R.id.close_button);
        EditText classroomNumberEditText = dialogView.findViewById(R.id.event_classroom_number);

        // Set up club spinner
        ArrayAdapter<CharSequence> clubAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.clubs_array, android.R.layout.simple_spinner_item);
        clubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(clubAdapter);

        // Set up venue spinner
        ArrayAdapter<CharSequence> venueAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.venue_array, android.R.layout.simple_spinner_item);
        venueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        venueSpinner.setAdapter(venueAdapter);

        // Set up the venue spinner item selection listener
        venueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVenue = parent.getItemAtPosition(position).toString();
                if (selectedVenue.equals("ClassRoom")) {
                    classroomNumberEditText.setVisibility(View.VISIBLE);
                } else {
                    classroomNumberEditText.setVisibility(View.GONE);
                    classroomNumberEditText.setText(""); // Clear the input when not selected
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        eventTimeEditText.setOnClickListener(v -> {
            // Get current time
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Create a TimePickerDialog in 24-hour format
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view, selectedHour, selectedMinute) -> {
                        // Format time to display in EditText (24-hour format)
                        String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                        eventTimeEditText.setText(formattedTime); // Set time in EditText
                    }, hour, minute, true); // Set to true for 24-hour format

            // Show the TimePickerDialog
            timePickerDialog.show();
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Add a custom "Save" button to the dialog
        Button saveButton = dialogView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText().toString();
            String eventTime = eventTimeEditText.getText().toString();
            String eventVenue = venueSpinner.getSelectedItem().toString();
            String eventClub = clubSpinner.getSelectedItem().toString();
            String eventDetails = eventDetailsEditText.getText().toString();
            String classroomNumber = classroomNumberEditText.getText().toString();

            if (!eventName.isEmpty() && !eventVenue.isEmpty()) {
                // If the venue is ClassRoom, ensure the classroom number is valid
                if (eventVenue.equals("ClassRoom") && !classroomNumber.isEmpty()) {
                    // Validate classroom number
                    int classroomNum;
                    try {
                        classroomNum = Integer.parseInt(classroomNumber);
                        if (classroomNum < 1 || classroomNum > 35) {
                            Toast.makeText(getContext(), "Classroom number must be between 1 and 35", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid classroom number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // Save time or placeholder if not selected
                saveEventToFirebase(eventName, eventTime.isEmpty() ? "-" : eventTime, eventVenue, eventClub, eventDetails, classroomNumber, dialog);
            } else {
                Toast.makeText(getContext(), "Event name and venue are required", Toast.LENGTH_SHORT).show();
            }
        });

        // Set the close button action
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Save event to Firebase under the selected date
    private void saveEventToFirebase(String name, String time, String venue, String club, String details, String classroomNumber, AlertDialog dialog) {
        String formattedDate = selectedDate.replace("/", "-"); // Convert date format for Firebase key
        DatabaseReference eventRef = databaseReference.child(formattedDate).push(); // Push creates a unique event ID
        String eventId = eventRef.getKey();

        // Create a new Event object
        Event newEvent = new Event(eventId, formattedDate, name, time, venue, club, details, classroomNumber);

        // Save event data to Firebase under the selected date
        eventRef.setValue(newEvent).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                Toast.makeText(getContext(), "Event added successfully!", Toast.LENGTH_SHORT).show();
                updateCalendarView();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateEventList(formattedDate);
                }
            } else {
                Toast.makeText(getContext(), "Failed to save event. Try again.", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error saving event: " + task.getException().getMessage());
            }
        });
    }

    // New method to delete an event
    public void deleteEvent(Event event) {
        String dateKey = event.getDateKey();
        String eventId = event.getId();

        DatabaseReference dateRef = databaseReference.child(dateKey);
        DatabaseReference eventRef = dateRef.child(eventId);

        eventRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();

                // Check if this was the last event for the date
                dateRef.get().addOnCompleteListener(dateTask -> {
                    if (dateTask.isSuccessful()) {
                        if (!dateTask.getResult().exists()) {
                            // If no events left, remove the date node
                            dateRef.removeValue();
                        }
                        updateCalendarView();
                    }
                });

                // Update the event list
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateEventList(dateKey);
                }
            } else {
                Toast.makeText(getContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateEvent(Event event) {
        String dateKey = event.getDateKey();
        String eventId = event.getId();

        DatabaseReference eventRef = databaseReference.child(dateKey).child(eventId);

        Map<String, Object> eventUpdates = new HashMap<>();
        eventUpdates.put("name", event.getName());
        eventUpdates.put("time", event.getTime());
        eventUpdates.put("venue", event.getVenue());
        eventUpdates.put("club", event.getClub());
        eventUpdates.put("details", event.getDetails());
        eventUpdates.put("classroomNumber", event.getClassroomNumber());

        eventRef.updateChildren(eventUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
                updateCalendarView();
                // Update the event list
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateEventList(dateKey);
                }
            } else {
                Toast.makeText(getContext(), "Failed to update event", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateCalendarView() {
        MaterialCalendarView calendarView = getView().findViewById(R.id.calendarView);
        calendarView.removeDecorators();
        loadAndHighlightEvents(calendarView);
    }

//        // Prepare data to be saved
//        Map<String, Object> eventData = new HashMap<>();
//        eventData.put("name", name);
//        eventData.put("time", time);
//        eventData.put("venue", venue);
//        eventData.put("club", club);
//        eventData.put("details", details);
//
//        // If the venue is a classroom, add the classroom number
//        if (venue.equals("ClassRoom")) {
//            eventData.put("classroomNumber", classroomNumber);
//        }
//
//        // Save event data to Firebase under the selected date
//        eventRef.setValue(eventData).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                // If saving is successful, dismiss dialog and show a success message
//                dialog.dismiss();
//                Toast.makeText(getContext(), "Event added successfully!", Toast.LENGTH_SHORT).show();
//                // Update the event list immediately after saving
//                if (getActivity() instanceof MainActivity) {
//                    ((MainActivity) getActivity()).updateEventList(formattedDate);
//                }
//            } else {
//                // If saving fails, show an error message
//                Toast.makeText(getContext(), "Failed to save event. Try again.", Toast.LENGTH_SHORT).show();
//                Log.e("FirebaseError", "Error saving event: " + task.getException().getMessage());
//            }
//        });
//    }

    private void loadAndHighlightEvents(MaterialCalendarView calendarView) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, DayEvents> eventsByDate = new HashMap<>();

                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String dateKey = dateSnapshot.getKey();
                    if (dateKey != null) {
                        String[] dateParts = dateKey.split("-");
                        if (dateParts.length == 3) {
                            try {
                                int day = Integer.parseInt(dateParts[0]);
                                int month = Integer.parseInt(dateParts[1]); // CalendarDay uses 0-indexed months
                                int year = Integer.parseInt(dateParts[2]);
                                CalendarDay eventDay = CalendarDay.from(year, month, day);

                                DayEvents dayEvents = eventsByDate.computeIfAbsent(dateKey, k -> new DayEvents(eventDay));

                                for (DataSnapshot eventSnapshot : dateSnapshot.getChildren()) {
                                    String club = eventSnapshot.child("club").getValue(String.class);
                                    if (club != null) {
                                        dayEvents.addClub(club);
                                    }
                                }


                            } catch (NumberFormatException e) {
                                Log.e("CalendarFragment", "Invalid date format: " + dateKey);
                            }
                        }
                    }
                }

                // Apply decorators for each day with events
                for (DayEvents dayEvents : eventsByDate.values()) {
                    if (!dayEvents.getClubs().isEmpty()) {
                        calendarView.addDecorator(new ClubEventDecorator(getContext(), dayEvents));
                    }                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CalendarFragment", "Failed to load events: " + databaseError.getMessage());
            }
        });
    }
}

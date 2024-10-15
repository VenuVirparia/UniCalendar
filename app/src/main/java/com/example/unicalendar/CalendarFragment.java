package com.example.unicalendar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.app.TimePickerDialog;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.threeten.bp.format.DateTimeFormatter; // Correct import for ThreeTenABP
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


public class CalendarFragment extends Fragment {

    private String selectedDate;
    private DatabaseReference databaseReference;
    MaterialCalendarView materialCalendarView;
    private View customHeaderView;
    private TextView monthYearText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        materialCalendarView = view.findViewById(R.id.calendarView);
        FloatingActionButton fab = view.findViewById(R.id.fab_admin);
        materialCalendarView.addDecorator(new TodayDecorator(getContext()));
        customHeaderView = view.findViewById(R.id.custom_header);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        setupCustomHeader();
        checkAdminStatus(fab);
        setupCalendarView();

        fab.setOnClickListener(v -> openEventDialog(selectedDate));
        loadAndHighlightEvents(materialCalendarView);
        return view;

    }
    private void checkAdminStatus(FloatingActionButton fab) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && "admin@gmail.com".equals(currentUser.getEmail())) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    private void setupCalendarView() {
        materialCalendarView.setSelectedDate(CalendarDay.today());
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%d-%d", date.getDay(), date.getMonth(), date.getYear());
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateEventList(selectedDate);
            }
        });

    }

    private void openEventDialog(String date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_event, null);
        builder.setView(dialogView);

        TextInputEditText eventNameInput = dialogView.findViewById(R.id.event_name);
        TextInputEditText eventTimeInput = dialogView.findViewById(R.id.event_time);
        AutoCompleteTextView clubSpinner = dialogView.findViewById(R.id.club_spinner);
        AutoCompleteTextView venueSpinner = dialogView.findViewById(R.id.venue_spinner);
        TextInputEditText eventDetailsInput = dialogView.findViewById(R.id.event_details);
        ImageView closeButton = dialogView.findViewById(R.id.close_button);
        TextInputLayout classroomNumberLayout = dialogView.findViewById(R.id.classroom_number_layout);
        TextInputEditText classroomNumberInput = dialogView.findViewById(R.id.event_classroom_number);
        TextInputLayout venueLayout = dialogView.findViewById(R.id.venue_layout);


        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText(String.format("Add Event on %s", date));

        setupSpinners(clubSpinner, venueSpinner);
        setupTimePicker(eventTimeInput);

        AlertDialog dialog = builder.create();

        MaterialButton saveButton = dialogView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> saveEvent(eventNameInput, eventTimeInput, venueSpinner, clubSpinner,
                eventDetailsInput, classroomNumberInput, dialog));

        closeButton.setOnClickListener(v -> dialog.dismiss());

        setupVenueSpinnerListener(venueSpinner, classroomNumberLayout);

        // Add listener for club spinner to handle Holiday events
        clubSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedClub = (String) parent.getItemAtPosition(position);
            if ("Holiday".equals(selectedClub)) {
                eventTimeInput.setVisibility(View.GONE);
                venueSpinner.setVisibility(View.GONE);
                classroomNumberLayout.setVisibility(View.GONE);
                venueLayout.setVisibility(View.GONE);
            } else {
                eventTimeInput.setVisibility(View.VISIBLE);
                venueSpinner.setVisibility(View.VISIBLE);
                venueLayout.setVisibility(View.VISIBLE);
                // ClassRoom visibility is handled in setupVenueSpinnerListener
            }
        });

        dialog.show();
    }

    private void setupSpinners(AutoCompleteTextView clubSpinner, AutoCompleteTextView venueSpinner) {
        ArrayAdapter<CharSequence> clubAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.clubs_array, android.R.layout.simple_dropdown_item_1line);
        clubSpinner.setAdapter(clubAdapter);

        ArrayAdapter<CharSequence> venueAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.venue_array, android.R.layout.simple_dropdown_item_1line);
        venueSpinner.setAdapter(venueAdapter);
    }

    private void setupTimePicker(TextInputEditText eventTimeInput) {
        eventTimeInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(getContext(),
                    (view, hourOfDay, minute) -> {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        eventTimeInput.setText(formattedTime);
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
    }

    private void setupVenueSpinnerListener(AutoCompleteTextView venueSpinner, TextInputLayout classroomNumberLayout) {
        venueSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedVenue = (String) parent.getItemAtPosition(position);
            classroomNumberLayout.setVisibility(selectedVenue.equals("ClassRoom") ? View.VISIBLE : View.GONE);
        });
    }

    private void saveEvent(TextInputEditText eventNameInput, TextInputEditText eventTimeInput,
                           AutoCompleteTextView venueSpinner, AutoCompleteTextView clubSpinner,
                           TextInputEditText eventDetailsInput, TextInputEditText classroomNumberInput,
                           AlertDialog dialog) {
        String eventName = eventNameInput.getText().toString();
        String eventTime = eventTimeInput.getText().toString();
        String eventVenue = venueSpinner.getText().toString();
        String eventClub = clubSpinner.getText().toString();
        String eventDetails = eventDetailsInput.getText().toString();
        String classroomNumber = classroomNumberInput.getText().toString();

        if (validateInputs(eventName, eventVenue, classroomNumber, eventClub, eventDetails)) {
            saveEventToFirebase(eventName, eventTime.isEmpty() ? "-" : eventTime,
                    eventVenue.isEmpty() ? "-" : eventVenue,
                    eventClub, eventDetails,
                    classroomNumber.isEmpty() ? "-" : classroomNumber,
                    dialog);
        }
    }

    private boolean validateInputs(String eventName, String eventVenue, String classroomNumber, String eventClub, String eventDetails) {
        if (eventName.isEmpty()) {
            Toast.makeText(getContext(), "Event name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!"Holiday".equals(eventClub) && eventDetails.isEmpty()) {
            Toast.makeText(getContext(), "Event details are required for non-holiday events", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventVenue.equals("ClassRoom") && !classroomNumber.isEmpty()) {
            try {
                int classroomNum = Integer.parseInt(classroomNumber);
                if (classroomNum < 1 || classroomNum > 35) {
                    Toast.makeText(getContext(), "Classroom number must be between 1 and 35", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid classroom number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
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


    private void loadAndHighlightEvents(MaterialCalendarView calendarView) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Map<String, DayEvents> eventsByDate = new HashMap<>();

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

                                ///DayEvents dayEvents = eventsByDate.computeIfAbsent(dateKey, k -> new DayEvents(eventDay));
                                String eventType = "Other";
                                for (DataSnapshot eventSnapshot : dateSnapshot.getChildren()) {
                                    String club = eventSnapshot.child("club").getValue(String.class);
                                    if ("External Exam".equals(club) || "Internal Exam".equals(club)) {
                                        eventType = "Exam";
                                        break;
                                    } else if ("Holiday".equals(club)) {
                                        eventType = "Holiday";
                                        break;
                                    }
                                }

                                if (getContext() != null) {
                                    EventDecorator decorator = new EventDecorator(getContext(), eventDay, eventType);
                                    calendarView.addDecorator(decorator);
                                } else {
                                    Log.e("CalendarFragment", "Context is null when creating EventDecorator");
                                }                            } catch (NumberFormatException e) {
                                Log.e("CalendarFragment", "Invalid date format: " + dateKey);
                            }
                        }
                    }
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CalendarFragment", "Failed to load events: " + databaseError.getMessage());
            }
        });
    }

    private void setupCustomHeader() {
        monthYearText = customHeaderView.findViewById(R.id.month_year);
        TextView previousButton = customHeaderView.findViewById(R.id.previous_month);
        TextView nextButton = customHeaderView.findViewById(R.id.next_month);

        materialCalendarView.setTopbarVisible(false);

        updateHeaderText(materialCalendarView.getCurrentDate());

        previousButton.setOnClickListener(v -> materialCalendarView.goToPrevious());
        nextButton.setOnClickListener(v -> materialCalendarView.goToNext());
        monthYearText.setOnClickListener(v -> showMonthYearPicker());

        materialCalendarView.setOnMonthChangedListener((widget, date) -> updateHeaderText(date));
    }

    private void updateHeaderText(CalendarDay date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
        String formattedDate = date.getDate().format(formatter);
        monthYearText.setText(formattedDate);
    }

    private void showMonthYearPicker() {
        CalendarDay currentDate = materialCalendarView.getCurrentDate();
        int year = currentDate.getYear();
        int month = currentDate.getMonth() - 1; // MonthYearPickerDialog uses 0-based months

        MonthYearPickerDialog monthYearPickerDialog = MonthYearPickerDialog.newInstance(month, year);
        monthYearPickerDialog.setListener((view, year1, month1, dayOfMonth) ->
                materialCalendarView.setCurrentDate(CalendarDay.from(year1, month1 + 1, 1)));
        monthYearPickerDialog.show(getParentFragmentManager(), "MonthYearPickerDialog");
    }
}


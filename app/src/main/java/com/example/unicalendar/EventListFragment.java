

package com.example.unicalendar;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

    @Override
    public void onEditEvent(Event event){
        showEditEventDialog(event);
        //Toast.makeText(getContext(), "OnEditEvent Clicked", Toast.LENGTH_SHORT).show();

    }
    private void showEditEventDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_event, null);
        builder.setView(dialogView);

        EditText eventNameEditText = dialogView.findViewById(R.id.event_name);
        TextView eventTimeTextView = dialogView.findViewById(R.id.event_time);
        Spinner venueSpinner = dialogView.findViewById(R.id.venue_spinner);
        Spinner clubSpinner = dialogView.findViewById(R.id.club_spinner);
        EditText eventDetailsEditText = dialogView.findViewById(R.id.event_details);
        EditText classroomNumberEditText = dialogView.findViewById(R.id.event_classroom_number);

        // Set current values
        eventNameEditText.setText(event.getName());
        eventTimeTextView.setText(event.getTime());
        eventDetailsEditText.setText(event.getDetails());
        classroomNumberEditText.setText(event.getClassroomNumber());

        // Populate spinners (you'll need to implement these methods)
        populateVenueSpinner(venueSpinner, event.getVenue());
        populateClubSpinner(clubSpinner, event.getClub());

        // Time picker
        eventTimeTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                eventTimeTextView.setText(time);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        AlertDialog dialog = builder.create();
        // Add a custom "Save" button to the dialog
        Button saveButton = dialogView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {

            event.setName(eventNameEditText.getText().toString());
            event.setTime(eventTimeTextView.getText().toString());
            event.setVenue(venueSpinner.getSelectedItem().toString());
            event.setClub(clubSpinner.getSelectedItem().toString());
            event.setDetails(eventDetailsEditText.getText().toString());
            event.setClassroomNumber(classroomNumberEditText.getText().toString());
//



        // Update in Firebase
            if (getActivity() instanceof MainActivity) {
                CalendarFragment calendarFragment = ((MainActivity) getActivity()).getCalendarFragment();
                if (calendarFragment != null) {
                    calendarFragment.updateEvent(event);
                    dialog.dismiss(); // Dismiss the dialog after saving
                } else {
                    Toast.makeText(getContext(), "Error: Calendar fragment not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error: Unable to update event", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView closeButton = dialogView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteEventDialog(Event event) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEvent(event))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }



    private void populateVenueSpinner(Spinner spinner, String currentVenue) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.venue_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (currentVenue != null) {
            int position = adapter.getPosition(currentVenue);
            spinner.setSelection(position);
        }
    }

    private void populateClubSpinner(Spinner spinner, String currentClub) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.clubs_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (currentClub != null) {
            int position = adapter.getPosition(currentClub);
            spinner.setSelection(position);
        }
    }
}

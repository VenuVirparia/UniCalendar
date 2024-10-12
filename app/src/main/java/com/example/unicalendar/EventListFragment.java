package com.example.unicalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventListFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private RecyclerView eventListRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();
    private TextView titleTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        titleTextView = view.findViewById(R.id.eventListTitle);
        eventListRecyclerView = view.findViewById(R.id.eventListRecyclerView);
        eventAdapter = new EventAdapter(eventList, this);
        eventListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventListRecyclerView.setAdapter(eventAdapter);

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
}
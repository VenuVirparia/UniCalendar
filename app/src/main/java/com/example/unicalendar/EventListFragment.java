package com.example.unicalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class EventListFragment extends Fragment {

    private ListView eventListView;
    private ArrayAdapter<String> eventAdapter;
    private ArrayList<String> eventList = new ArrayList<>();
    private ArrayList<Event> eventObjects = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        eventListView = view.findViewById(R.id.eventListView);
        eventAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, eventList);
        eventListView.setAdapter(eventAdapter);

        eventListView.setOnItemClickListener((parent, view1, position, id) -> {
            Event selectedEvent = eventObjects.get(position);
            showEventDetailsDialog(selectedEvent);
        });

        return view;
    }

    // Method to update the event list
    public void updateEventList(ArrayList<Event> events) {
        eventList.clear();
        eventObjects.clear();

        for (Event event : events) {
            eventList.add(event.getName() + " - " + event.getVenue() + " - " + event.getTime());
            eventObjects.add(event);  // Store the entire event object for details
        }

        eventAdapter.notifyDataSetChanged();
    }

    // Method to display event details in a dialog
    private void showEventDetailsDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_event_details, null);

        builder.setView(dialogView);
        builder.setTitle(event.getName());

        TextView eventTime = dialogView.findViewById(R.id.event_time);
        TextView eventVenue = dialogView.findViewById(R.id.event_venue);
        TextView eventDetails = dialogView.findViewById(R.id.event_details);

        eventTime.setText("Time: " + event.getTime());
        eventVenue.setText("Venue: " + event.getVenue());
        eventDetails.setText("Details: " + event.getDetails());

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}

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

import java.util.ArrayList;

public class EventListFragment extends Fragment {

    private RecyclerView eventListRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        eventListRecyclerView = view.findViewById(R.id.eventListRecyclerView);
        eventAdapter = new EventAdapter(eventList, this::showEventDetailsDialog);
        eventListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventListRecyclerView.setAdapter(eventAdapter);

        return view;
    }

    // Method to update the event list
    public void updateEventList(ArrayList<Event> events) {
        eventList.clear();
        eventList.addAll(events);
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

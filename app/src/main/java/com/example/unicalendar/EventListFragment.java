package com.example.unicalendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class EventListFragment extends Fragment {

    private ListView eventListView;
    private ArrayAdapter<String> eventAdapter;
    private ArrayList<String> eventList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        eventListView = view.findViewById(R.id.eventListView);
        eventAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, eventList);
        eventListView.setAdapter(eventAdapter);

        return view;
    }

    // Method to update the event list
    public void updateEventList(ArrayList<String> events) {
        eventList.clear();  // Clear the old list
        eventList.addAll(events);  // Add the new events
        eventAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the list
    }
}

package com.example.unicalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private FloatingActionButton fab;
    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        fab = view.findViewById(R.id.fab_admin);

        // Get the email and password passed from login
        String userEmail = getActivity().getIntent().getStringExtra("email");
        String userPassword = getActivity().getIntent().getStringExtra("password");

        // Check if both email and password match the admin credentials
        if ("admin@gmail.com".equals(userEmail) && "Admin@0000".equals(userPassword)) {
            fab.setVisibility(View.VISIBLE);  // Show FAB for admin
        } else {
            fab.setVisibility(View.GONE);  // Hide FAB for non-admin users
        }

        // Handle date selection
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            // Communicate with EventListFragment to update the event list
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateEventList(selectedDate);
            }
        });

        // Handle FAB click for admin
        fab.setOnClickListener(v -> openEventDialog());

        return view;
    }

    private void openEventDialog() {
        // Show dialog to add/edit/delete event
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Manage Event on " + selectedDate);
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Handle save action
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}

package com.example.unicalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment {

    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        FloatingActionButton fab = view.findViewById(R.id.fab_admin);

        if (getActivity() != null) {
            String userEmail = getActivity().getIntent().getStringExtra("email");
            String userPassword = getActivity().getIntent().getStringExtra("password");

            Log.d("CalendarFragment", "User Email: " + userEmail);
            Log.d("CalendarFragment", "User Password: " + userPassword);

            if ("admin@gmail.com".equals(userEmail)) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        }

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateEventList(selectedDate);
            }
        });

        fab.setOnClickListener(v -> openEventDialog());

        return view;
    }

    private void openEventDialog() {
        Log.d("CalendarFragment", "FAB clicked, opening dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Manage Event on " + selectedDate);
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Handle save action
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}

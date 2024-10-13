package com.example.unicalendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> eventList;
    private OnEventClickListener listener;
    //private String selectedDate;

    public EventAdapter(ArrayList<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
        //this.selectedDate=selectedDate;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        if (event != null) {
            holder.bind(event, listener);
        }
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if ("admin@gmail.com".equals(userEmail)) {
            holder.editIcon.setVisibility(View.VISIBLE);
            holder.deleteIcon.setVisibility(View.VISIBLE);

            // Edit button listener
            holder.editIcon.setOnClickListener(v -> listener.onEditEvent(event));

            // Delete button listener
            holder.deleteIcon.setOnClickListener(v -> listener.onDeleteEvent(event));
        } else {
            holder.editIcon.setVisibility(View.GONE);
            holder.deleteIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnEventClickListener {
        void onEventClick(Event event);
        void onEditEvent(Event event);
        void onDeleteEvent(Event event);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventClubTextView;
        public ImageView editIcon;
        public ImageView deleteIcon;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name);
            eventClubTextView = itemView.findViewById(R.id.event_club);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }

        void bind(Event event, OnEventClickListener listener) {
            eventNameTextView.setText(event.getName());
            eventClubTextView.setText(event.getClub());

            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }
    }
}
package com.example.unicalendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> eventList;
    private OnEventClickListener listener;

    public EventAdapter(ArrayList<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
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
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventClubTextView;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name);
            eventClubTextView = itemView.findViewById(R.id.event_club);
        }

        void bind(Event event, OnEventClickListener listener) {
            eventNameTextView.setText(event.getName());
            eventClubTextView.setText(event.getClub());
            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }
    }
}
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
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTextView;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTextView = itemView.findViewById(android.R.id.text1);
        }

        void bind(Event event, OnEventClickListener listener) {
            eventTextView.setText(event.getName() + " - " + event.getVenue() + " - " + event.getTime());
            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }
    }
}

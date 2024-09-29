package com.example.unicalendar;

public class Event {
    private String name;
    private String time;
    private String venue;
    private String details;

    // Empty constructor required for Firebase
    public Event() {}

    public Event(String name, String time, String venue, String details) {
        this.name = name;
        this.time = time;
        this.venue = venue;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getVenue() {
        return venue;
    }

    public String getDetails() {
        return details;
    }
}

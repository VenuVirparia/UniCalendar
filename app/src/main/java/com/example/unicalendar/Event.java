package com.example.unicalendar;

public class Event {
    private String name;
    private String time;
    private String venue;
    private String details;
    private String club; // New field
    private String classroomNumber;

    // Empty constructor required for Firebase
    public Event() {}

    // Constructor with parameters
    public Event(String eventName, String eventTime, String eventVenue, String eventDetails, String eventClub, String classroomNumber) {
        this.name = eventName;
        this.time = eventTime;
        this.venue = eventVenue;
        this.details = eventDetails;
        this.club = eventClub;
        this.classroomNumber = classroomNumber;
    }

    // Getters
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

    public String getClub() {
        return club;
    }

    public String getClassroomNumber() {
        return classroomNumber;
    }
}

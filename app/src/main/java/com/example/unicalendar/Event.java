package com.example.unicalendar;

public class Event {
    private String id;
    private String dateKey;
    private String name;
    private String time;
    private String venue;
    private String club;
    private String details;
    private String classroomNumber;

    // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    public Event() {}

    public Event(String id, String dateKey, String name, String time, String venue, String club, String details, String classroomNumber) {
        this.id = id;
        this.dateKey = dateKey;
        this.name = name;
        this.time = time;
        this.venue = venue;
        this.club = club;
        this.details = details;
        this.classroomNumber = classroomNumber;
    }

    // Getters and setters for all properties
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDateKey() { return dateKey; }
    public void setDateKey(String dateKey) { this.dateKey = dateKey; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getClassroomNumber() { return classroomNumber; }
    public void setClassroomNumber(String classroomNumber) { this.classroomNumber = classroomNumber; }
}
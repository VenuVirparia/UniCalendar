package com.example.unicalendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;

public class DayEvents {
    private final CalendarDay date;
    private final List<String> clubs;

    public DayEvents(CalendarDay date) {
        this.date = date;
        this.clubs = new ArrayList<>();
    }

    public void addClub(String club) {
        if (!clubs.contains(club)) {
            clubs.add(club);
        }
    }

    public int getEventCount() {
        return clubs.size();
    }

    public List<String> getClubs() {
        return clubs != null ? clubs : new ArrayList<>();
    }

    public CalendarDay getDate() {
        return date;
    }
}
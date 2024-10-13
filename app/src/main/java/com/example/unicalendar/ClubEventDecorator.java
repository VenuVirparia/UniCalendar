package com.example.unicalendar;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class ClubEventDecorator implements DayViewDecorator {

    private final CalendarDay date;
    private final String club;

    public ClubEventDecorator(CalendarDay date, String club) {
        this.date = date;
        this.club = club;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }


    @Override
    public void decorate(DayViewFacade view) {
        int color;
        switch (club) {
            case "University":
                color = Color.parseColor("#FF5733"); // Vibrant orange
                break;
            case "Sports Club":
                color = Color.parseColor("#2ECC71"); // Bright green
                break;
            case "Samvaad":
                color = Color.parseColor("#1F618D"); // Dark blue
                break;
            case "IETE":
                color = Color.parseColor("#58D68D"); // Light green
                break;
            case "GDSC":
                color = Color.parseColor("#F39C12"); // Orange-yellow
                break;
            case "CSI":
                color = Color.parseColor("#9B59B6"); // Purple
                break;
            case "Shutterbugs":
                color = Color.parseColor("#E74C3C"); // Red
                break;
            case "Readers Community":
                color = Color.parseColor("#D35400"); // Dark orange
                break;
            case "Decibel":
                color = Color.parseColor("#34495E"); // Slate blue
                break;
            case "Holiday":
                color = Color.parseColor("#F1C40F"); // Yellow
                break;
            case "Internal Exam":
                color = Color.parseColor("#E67E22"); // Burnt orange
                break;
            case "External Exam":
                color = Color.parseColor("#C0392B"); // Crimson red
                break;
            default:
                color = Color.GRAY; // Default color for any other club
        }
        view.addSpan(new DotSpan(8, color)); // Set the size and color of the dot
    }

}


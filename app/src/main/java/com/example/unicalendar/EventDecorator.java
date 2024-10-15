package com.example.unicalendar;

import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class EventDecorator implements DayViewDecorator {

    private final CalendarDay date;
    private int color;
    private final Context context;
    private final String eventType;

    public EventDecorator(Context context, CalendarDay date, String eventType) {
        this.date = date;
        this.context = context.getApplicationContext(); // Use application context
        this.eventType = eventType;
        setColorForEventType();
    }

    private void setColorForEventType() {
        if (context == null) {
            // Set a default color if context is null
            this.color = 0xFF000000; // Black color
            return;
        }

        @ColorRes int colorResId;
        switch (eventType) {
            case "Exam":
                colorResId = R.color.exam;
                break;
            case "Holiday":
                colorResId = R.color.holiday;
                break;
            default:
                colorResId = R.color.other;
        }

        try {
            this.color = ContextCompat.getColor(context, colorResId);
        } catch (Exception e) {
            // If there's any issue getting the color, set a default
            this.color = 0xFF000000; // Black color
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && date.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, color));
    }
}
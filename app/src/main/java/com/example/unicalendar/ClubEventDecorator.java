package com.example.unicalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

public class ClubEventDecorator implements DayViewDecorator {
    private final Context context;
    private final DayEvents dayEvents;

    public ClubEventDecorator(Context context, DayEvents dayEvents) {
        this.context = context;
        this.dayEvents = dayEvents;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dayEvents.getDate().equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new EventBackgroundSpan(context, dayEvents));
    }

    private class EventBackgroundSpan implements LineBackgroundSpan {
        private final Paint paint;
        private final DayEvents events;

        EventBackgroundSpan(Context context, DayEvents events) {
            this.events = events;
            this.paint = new Paint();
            paint.setAntiAlias(true);
        }

        @Override
        public void drawBackground(Canvas canvas, Paint paint,
                                   int left, int right, int top, int baseline, int bottom,
                                   CharSequence text, int start, int end, int lnum) {
            int color;
            List<String> eventTypes = events.getClubs();
            if (eventTypes.isEmpty()) {
                // No events, don't draw anything
                return;
            }
            if (eventTypes.size() > 1) {
                color = ContextCompat.getColor(context, R.color.more);
            } else {
                String eventType = eventTypes.get(0);
                switch (eventType) {
                    case "External Exam":
                    case "Internal Exam":
                        color = ContextCompat.getColor(context, R.color.exam);
                        break;
                    case "Holiday":
                        color = ContextCompat.getColor(context, R.color.holiday);
                        break;
                    case "University":
                        color = ContextCompat.getColor(context, R.color.university);
                        break;
                    default:
                        color = ContextCompat.getColor(context, R.color.other);
                }
            }

            int radius = (right - left) / 3;
            int centerX = (right + left) / 2;
            int centerY = (bottom + top) / 2;

            this.paint.setColor(color);
            canvas.drawCircle(centerX, centerY, radius, this.paint);
        }
    }
}

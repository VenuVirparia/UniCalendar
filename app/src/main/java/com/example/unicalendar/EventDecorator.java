package com.example.unicalendar;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class EventDecorator implements DayViewDecorator {

        private final CalendarDay date;
        private final int color;

        public  EventDecorator(Context context, CalendarDay date, String eventType) {
            this.date = date;
            switch (eventType) {
                case "Exam":
                    this.color = ContextCompat.getColor(context, R.color.exam);
                    break;
                case "Holiday":
                    this.color = ContextCompat.getColor(context, R.color.holiday);
                    break;
                default:
                    this.color = ContextCompat.getColor(context, R.color.other);
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date.equals(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(8, color));
        }
    }
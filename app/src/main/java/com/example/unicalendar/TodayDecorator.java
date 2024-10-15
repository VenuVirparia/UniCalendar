package com.example.unicalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import androidx.core.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class TodayDecorator implements DayViewDecorator {
    private final CalendarDay today;
    private final Context context;

    public TodayDecorator(Context context) {
        this.today = CalendarDay.today();
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new TodaySpan(context));
    }

    private class TodaySpan implements LineBackgroundSpan {
        private final Paint paint;

        TodaySpan(Context context) {
            this.paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(ContextCompat.getColor(context, R.color.todaysDate));
        }

        @Override
        public void drawBackground(Canvas canvas, Paint paint,
                                   int left, int right, int top, int baseline, int bottom,
                                   CharSequence text, int start, int end, int lnum) {
            int radius = (right - left) / 3;
            int centerX = (right + left) / 2;
            int centerY = (bottom + top) / 2;

            canvas.drawCircle(centerX, centerY, radius, this.paint);
        }
    }
}
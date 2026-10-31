package com.soogbad.soogbadcalendar;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonthGrid extends LinearLayout {

    private static final int WEEKS = 6, DAYS_IN_WEEK = 7, MAX_VISIBLE_EVENTS = 3;

    private final DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
    private final View[] cells = new View[WEEKS * DAYS_IN_WEEK];

    public MonthGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        addView(createWeekdayHeader(context), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LayoutInflater inflater = LayoutInflater.from(context);
        for(int week = 0; week < WEEKS; week++) {
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(HORIZONTAL);
            for(int dayOfWeek = 0; dayOfWeek < DAYS_IN_WEEK; dayOfWeek++) {
                View cell = inflater.inflate(R.layout.month_day_cell, row, false);
                row.addView(cell, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
                cells[week * DAYS_IN_WEEK + dayOfWeek] = cell;
            }
            addView(row, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
        }
    }
    private LinearLayout createWeekdayHeader(Context context) {
        LinearLayout header = new LinearLayout(context);
        header.setOrientation(HORIZONTAL);
        for(int i = 0; i < DAYS_IN_WEEK; i++) {
            TextView label = new TextView(context);
            label.setText(firstDayOfWeek.plus(i).getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            label.setGravity(Gravity.CENTER);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            label.setTextColor(ContextCompat.getColor(context, com.soogbad.sharedmodule.R.color.lightGray));
            int paddingPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
            label.setPadding(0, paddingPx, 0, paddingPx);
            header.addView(label, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        }
        return header;
    }

    public void setMonth(YearMonth month, List<Event> events) {
        LocalDate today = LocalDate.now();
        LocalDate day = month.atDay(1).with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
        for(View cell : cells) {
            bindCell(cell, day, YearMonth.from(day).equals(month), day.equals(today), Event.getEventsOn(events, day));
            day = day.plusDays(1);
        }
    }
    private void bindCell(View cell, LocalDate day, boolean inMonth, boolean isToday, ArrayList<Event> dayEvents) {
        Context context = getContext();
        TextView dayNumber = cell.findViewById(R.id.dayNumberTextView);
        dayNumber.setText(String.valueOf(day.getDayOfMonth()));
        cell.setAlpha(inMonth ? 1f : 0.4f);
        cell.setTag(day);
        dayNumber.setBackgroundResource(isToday ? R.drawable.month_today_circle : 0);
        LinearLayout eventsContainer = cell.findViewById(R.id.dayEventsContainer);
        eventsContainer.removeAllViews();
        for(int i = 0; i < Math.min(dayEvents.size(), MAX_VISIBLE_EVENTS); i++) {
            TextView eventTitlePreview = (TextView)LayoutInflater.from(context).inflate(R.layout.event_title_preview, eventsContainer, false);
            eventTitlePreview.setText(dayEvents.get(i).Title);
            eventTitlePreview.setBackgroundTintList(ColorStateList.valueOf(dayEvents.get(i).Options.EventColor.color));
            eventsContainer.addView(eventTitlePreview);
        }
        if(dayEvents.size() > MAX_VISIBLE_EVENTS) {
            TextView more = new TextView(context);
            more.setText(String.format(Locale.getDefault(), "+%d", dayEvents.size() - MAX_VISIBLE_EVENTS));
            more.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            more.setTextColor(ContextCompat.getColor(context, com.soogbad.sharedmodule.R.color.lightGray));
            more.setGravity(Gravity.CENTER_HORIZONTAL);
            eventsContainer.addView(more);
        }
    }

}

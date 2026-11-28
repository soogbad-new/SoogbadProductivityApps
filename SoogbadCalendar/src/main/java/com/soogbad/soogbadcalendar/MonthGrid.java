package com.soogbad.soogbadcalendar;

import androidx.annotation.Nullable;
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

import com.google.android.material.card.MaterialCardView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MonthGrid extends LinearLayout {

    private static final int WEEKS = 6, DAYS_IN_WEEK = 7, MAX_VISIBLE_EVENTS = 3;
    private final DayOfWeek firstDayOfWeek = DayOfWeek.SUNDAY;

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
        header.setBackgroundColor(ContextCompat.getColor(context, com.soogbad.sharedmodule.R.color.darkBlue));
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
        int weeks = (int)(ChronoUnit.DAYS.between(day, month.atEndOfMonth()) / DAYS_IN_WEEK) + 1;
        HashMap<LocalDate, ArrayList<Event>> eventsByDay = Event.groupEventsByDay(events, day, day.plusDays((long)weeks * DAYS_IN_WEEK - 1));
        for(int week = 0; week < WEEKS; week++) {
            getChildAt(week + 1).setVisibility(week < weeks ? VISIBLE : GONE);
            if(week >= weeks) continue;
            for(int dayOfWeek = 0; dayOfWeek < DAYS_IN_WEEK; dayOfWeek++) {
                bindCell(cells[week * DAYS_IN_WEEK + dayOfWeek], day, YearMonth.from(day).equals(month), day.equals(today), eventsByDay.get(day), MAX_VISIBLE_EVENTS + WEEKS - weeks);
                day = day.plusDays(1);
            }
        }
    }
    private void bindCell(View cell, LocalDate day, boolean inMonth, boolean isToday, @Nullable ArrayList<Event> dayEvents, int maxVisibleEvents) {
        Context context = getContext();
        TextView dayNumber = cell.findViewById(R.id.dayNumberTextView);
        dayNumber.setText(String.valueOf(day.getDayOfMonth()));
        cell.setAlpha(inMonth ? 1f : 0.4f);
        cell.setTag(day);
        dayNumber.setBackgroundResource(isToday ? R.drawable.month_today_circle : 0);
        LinearLayout eventsContainer = cell.findViewById(R.id.dayEventsContainer);
        eventsContainer.removeAllViews();
        eventsContainer.setWeightSum(maxVisibleEvents);
        if(dayEvents == null) return;
        int visibleEvents = dayEvents.size() > maxVisibleEvents ? maxVisibleEvents - 1 : dayEvents.size();
        for(int i = 0; i < visibleEvents; i++) {
            MaterialCardView eventTitlePreview = (MaterialCardView)LayoutInflater.from(context).inflate(R.layout.event_title_preview, eventsContainer, false);
            ((TextView)eventTitlePreview.findViewById(R.id.eventTitlePreviewText)).setText(CustomHyphenator.markBreakPoints(dayEvents.get(i).Title));
            eventTitlePreview.setCardBackgroundColor(ColorStateList.valueOf(dayEvents.get(i).Options.EventColor.color));
            eventsContainer.addView(eventTitlePreview);
        }
        if(dayEvents.size() > visibleEvents) {
            TextView more = new TextView(context);
            more.setText(String.format(Locale.getDefault(), "+%d", dayEvents.size() - visibleEvents));
            more.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            more.setTextColor(ContextCompat.getColor(context, com.soogbad.sharedmodule.R.color.lightGray));
            more.setGravity(Gravity.CENTER);
            eventsContainer.addView(more, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
        }
    }

}

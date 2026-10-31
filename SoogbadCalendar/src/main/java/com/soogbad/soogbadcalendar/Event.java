package com.soogbad.soogbadcalendar;

import com.soogbad.sharedmodule.core.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

public class Event extends Item<Event.Options> implements Item.SchedulableItem {

    public Event(String uuid, String title, Options options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static Event create(String uuid, String title, Options options) {
        return new Event(uuid, title, options);
    }

    @SuppressWarnings("CanBeFinal")
    public static class Options extends Item.Options {

        public Options(LocalDateTime time, EventColor eventColor, Schedule repeatSchedule) { Time = time; EventColor = eventColor; RepeatSchedule = repeatSchedule; }

        public LocalDateTime Time;
        public EventColor EventColor;
        public Schedule RepeatSchedule;

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("time", Time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                json.put("eventColor", EventColor.name());
                json.put("repeatSchedule", RepeatSchedule.name());
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }

    }

    public static Options parseOptionsFromJson(JSONObject json) {
        try {
            LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(json.getLong("time")), ZoneId.systemDefault());
            EventColor eventColor = EventColor.valueOf(json.optString("eventColor", EventColor.PURPLE.name()));
            Schedule repeatSchedule = Schedule.valueOf(json.getString("repeatSchedule"));
            return new Options(time, eventColor, repeatSchedule);
        } catch(JSONException e) { throw new RuntimeException(e); }
    }

    public static Options getDefaultOptions(LocalDate day) {
        return new Options(day.atTime(8, 0), EventColor.PURPLE, Schedule.NONE);
    }

    public static ArrayList<Event> getEventsOn(List<Event> events, LocalDate day) {
        ArrayList<Event> dayEvents = new ArrayList<>();
        for(Event event : events)
            if(event.Options.Time.toLocalDate().equals(day)) dayEvents.add(event);
        dayEvents.sort(Comparator.comparing((Event event) -> event.Options.Time));
        return dayEvents;
    }

    @Override
    public Calendar getNextOccurrence() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = Options.Time;
        ChronoUnit scheduleUnit = Options.RepeatSchedule.unit;
        if(!next.isAfter(now)) {
            if(scheduleUnit == null) return null;
            else {
                long count = Math.max(scheduleUnit.between(Options.Time, now), 0);
                next = Options.Time.plus(count, scheduleUnit);
                while(!next.isAfter(now)) {
                    count++;
                    next = Options.Time.plus(count, scheduleUnit);
                }
            }
        }
        return GregorianCalendar.from(next.atZone(ZoneId.systemDefault()));
    }

    public enum EventColor {
        PURPLE(0xFF3E2973);

        public final int color;
        EventColor(int color) { this.color = color; }
    }

    public enum Schedule {
        NONE(null),
        DAILY(ChronoUnit.DAYS),
        WEEKLY(ChronoUnit.WEEKS),
        MONTHLY(ChronoUnit.MONTHS);

        public final ChronoUnit unit;
        Schedule(ChronoUnit unit) { this.unit = unit; }

        public String displayName() {
            String name = name();
            return name.charAt(0) + name.substring(1).toLowerCase();
        }
    }

}

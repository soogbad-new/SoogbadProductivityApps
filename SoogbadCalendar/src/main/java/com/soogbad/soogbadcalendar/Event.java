package com.soogbad.soogbadcalendar;

import android.app.NotificationManager;

import com.soogbad.sharedmodule.core.Item;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Event extends Item<Event.Options> implements Item.SchedulableItem {

    public Event(String uuid, String title, Options options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static Event create(String uuid, String title, Options options) {
        return new Event(uuid, title, options);
    }

    @SuppressWarnings("CanBeFinal")
    public static class Options extends Item.Options {

        public Options(LocalDateTime time, EventColor eventColor, Importance importance, Schedule repeatSchedule, Set<LocalDate> deletedOccurrences) {
            Time = time; EventColor = eventColor; Importance = importance; RepeatSchedule = repeatSchedule; DeletedOccurrences = deletedOccurrences;
        }

        public LocalDateTime Time;
        public EventColor EventColor;
        public Importance Importance;
        public Schedule RepeatSchedule;
        public Set<LocalDate> DeletedOccurrences;

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("time", Time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                json.put("eventColor", EventColor.name());
                json.put("importance", Importance.name());
                json.put("repeatSchedule", RepeatSchedule.name());
                JSONArray deletedOccurrences = new JSONArray();
                for(LocalDate date : DeletedOccurrences) deletedOccurrences.put(date.toString());
                json.put("deletedOccurrences", deletedOccurrences);
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }

    }

    public static Options parseOptionsFromJson(JSONObject json) {
        try {
            LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(json.getLong("time")), ZoneId.systemDefault());
            EventColor eventColor = EventColor.valueOf(json.getString("eventColor"));
            Importance importance = Importance.valueOf(json.optString("importance", Importance.SOUND.name()));
            Schedule repeatSchedule = Schedule.valueOf(json.getString("repeatSchedule"));
            Set<LocalDate> deletedOccurrences = new HashSet<>();
            JSONArray deletedOccurrencesJson = json.optJSONArray("deletedOccurrences");
            if(deletedOccurrencesJson != null)
                for(int i = 0; i < deletedOccurrencesJson.length(); i++) deletedOccurrences.add(LocalDate.parse(deletedOccurrencesJson.getString(i)));
            return new Options(time, eventColor, importance, repeatSchedule, deletedOccurrences);
        } catch(JSONException e) { throw new RuntimeException(e); }
    }

    public static Options getDefaultOptions(LocalDate day) {
        return new Options(day.atTime(8, 0), EventColor.GREEN, Importance.SOUND, Schedule.NONE, new HashSet<>());
    }

    public boolean occursOn(LocalDate day) {
        LocalDate start = Options.Time.toLocalDate();
        ChronoUnit scheduleUnit = Options.RepeatSchedule.unit;
        if(scheduleUnit == null) return day.equals(start); // if repeat schedule is none, just compare between the dates
        if(day.isBefore(start) || Options.DeletedOccurrences.contains(day)) return false;
        long count = scheduleUnit.between(start, day);
        return start.plus(count, scheduleUnit).equals(day) || start.plus(count + 1, scheduleUnit).equals(day); // plus() clamps to month end (Jan 31 -> Feb 28), in which case between() comes out one short
    }

    public static ArrayList<Event> getEventsOn(List<Event> events, LocalDate day) {
        ArrayList<Event> dayEvents = new ArrayList<>();
        for(Event event : events)
            if(event.occursOn(day)) dayEvents.add(event);
        dayEvents.sort(Comparator.comparing((Event event) -> event.Options.Time.toLocalTime()));
        return dayEvents;
    }

    public static HashMap<LocalDate, ArrayList<Event>> groupEventsByDay(List<Event> events, LocalDate from, LocalDate to) {
        HashMap<LocalDate, ArrayList<Event>> eventsByDay = new HashMap<>();
        for(LocalDate day = from; !day.isAfter(to); day = day.plusDays(1))
            eventsByDay.put(day, new ArrayList<>());
        for(Event event : events) {
            LocalDate start = event.Options.Time.toLocalDate();
            ChronoUnit scheduleUnit = event.Options.RepeatSchedule.unit;
            if(scheduleUnit == null) {
                ArrayList<Event> dayEvents = eventsByDay.get(start);
                if(dayEvents != null)
                    dayEvents.add(event);
            }
            else {
                long count = Math.max(scheduleUnit.between(start, from), 0);
                for(LocalDate day = start.plus(count, scheduleUnit); !day.isAfter(to); day = start.plus(++count, scheduleUnit))
                    if(!day.isBefore(from) && !event.Options.DeletedOccurrences.contains(day))
                        eventsByDay.get(day).add(event);
            }
        }
        for(ArrayList<Event> dayEvents : eventsByDay.values())
            dayEvents.sort(Comparator.comparing((Event event) -> event.Options.Time.toLocalTime()));
        return eventsByDay;
    }

    @Override
    public Calendar getNextOccurrence() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = Options.Time;
        ChronoUnit scheduleUnit = Options.RepeatSchedule.unit;
        if(scheduleUnit == null) {
            if(!next.isAfter(now)) return null;
            else return GregorianCalendar.from(next.atZone(ZoneId.systemDefault()));
        }
        else {
            long count = Math.max(scheduleUnit.between(Options.Time, now), 0);
            next = Options.Time.plus(count, scheduleUnit);
            while(!next.isAfter(now) || Options.DeletedOccurrences.contains(next.toLocalDate())) {
                count++;
                next = Options.Time.plus(count, scheduleUnit);
            }
            return GregorianCalendar.from(next.atZone(ZoneId.systemDefault()));
        }
    }

    public enum EventColor {
        GREEN(0xFF4FA626);

        public final int color;
        EventColor(int color) { this.color = color; }
    }

    public enum Importance {
        SILENT("soogbad_calendar_events_silent", NotificationManager.IMPORTANCE_LOW),
        SOUND("soogbad_calendar_events_sound", NotificationManager.IMPORTANCE_HIGH);

        public final String channelId;
        public final int channelImportance;
        Importance(String channelId, int channelImportance) { this.channelId = channelId; this.channelImportance = channelImportance; }

        public String displayName() {
            String name = name();
            return name.charAt(0) + name.substring(1).toLowerCase();
        }
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

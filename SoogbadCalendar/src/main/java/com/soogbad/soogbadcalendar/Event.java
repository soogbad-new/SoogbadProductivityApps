package com.soogbad.soogbadcalendar;

import com.soogbad.sharedmodule.core.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Calendar;

public class Event extends Item<Event.Options> implements Item.SchedulableItem {

    public Event(String uuid, String title, Options options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static Event create(String uuid, String title, Options options) {
        return new Event(uuid, title, options);
    }

    @SuppressWarnings("CanBeFinal")
    public static class Options extends Item.Options {

        public Options(Date time, Schedule repeatSchedule) { Time = time; RepeatSchedule = repeatSchedule; }

        public Date Time;
        public Schedule RepeatSchedule;

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("time", Time.getTime());
                json.put("repeatSchedule", RepeatSchedule.name());
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }

    }

    public static Options parseOptionsFromJson(JSONObject json) {
        try {
            Date time = new Date(json.getLong("time"));
            Schedule repeatSchedule = Schedule.valueOf(json.getString("repeatSchedule"));
            return new Options(time, repeatSchedule);
        } catch(JSONException e) { throw new RuntimeException(e); }
    }

    public static Options getDefaultOptions() {
        return new Options(new Date(), Schedule.NONE);
    }

    @Override
    public Calendar getNextOccurrence() {
        return null;
    }

    public enum Schedule {
        NONE,
        DAILY,
        WEEKLY,
        MONTHLY
    }

}

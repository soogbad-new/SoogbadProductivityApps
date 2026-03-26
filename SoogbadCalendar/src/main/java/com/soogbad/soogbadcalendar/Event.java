package com.soogbad.soogbadcalendar;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.Schedule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Event extends Item<Event.EventOptions> {

    public Event(String uuid, String title, EventOptions options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static Event create(String uuid, String title, EventOptions options) {
        return new Event(uuid, title, options);
    }

    public static class EventOptions extends ItemOptions {
        public Date Time;
        public Schedule RepeatSchedule;

        public EventOptions(Date time, Schedule repeatSchedule) { Time = time; RepeatSchedule = repeatSchedule; }

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("time", Time.getTime());
                json.put("repeatSchedule", RepeatSchedule.name());
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }

        public static EventOptions fromJson(JSONObject json) {
            try {
                Date time = new Date(json.getLong("time"));
                Schedule repeatSchedule = Schedule.valueOf(json.getString("repeatSchedule"));
                return new EventOptions(time, repeatSchedule);
            } catch(JSONException e) { throw new RuntimeException(e); }
        }
    }

}

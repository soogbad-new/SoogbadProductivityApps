package com.soogbad.soogbadreminders;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.Schedule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Reminder extends Item<Reminder.ReminderOptions> {

    public Reminder(String uuid, String title, ReminderOptions options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static Reminder create(String uuid, String title, ReminderOptions options) {
        return new Reminder(uuid, title, options);
    }

    public static class ReminderOptions extends SchedulableItemOptions {

        public ReminderOptions(Date time, Schedule repeatSchedule) { Time = time; RepeatSchedule = repeatSchedule; }

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("time", Time.getTime());
                json.put("repeatSchedule", RepeatSchedule.name());
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }

        public static ReminderOptions fromJson(JSONObject json) {
            try {
                Date time = new Date(json.getLong("time"));
                Schedule repeatSchedule = Schedule.valueOf(json.getString("repeatSchedule"));
                return new ReminderOptions(time, repeatSchedule);
            } catch(JSONException e) { throw new RuntimeException(e); }
        }
    }

}

package com.soogbad.soogbadreminders;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.Schedule;

import java.util.Date;

public class Reminder extends Item<Reminder.ReminderOptions> {

    public Reminder(String uuid, String title, ReminderOptions options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static Reminder create(String uuid, String title, ReminderOptions options) {
        return new Reminder(uuid, title, options);
    }

    public static class ReminderOptions extends ItemOptions {
        public Date Time;
        public Schedule RepeatSchedule;
    }

}

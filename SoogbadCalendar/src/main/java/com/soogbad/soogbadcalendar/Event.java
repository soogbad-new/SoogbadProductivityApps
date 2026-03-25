package com.soogbad.soogbadcalendar;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.Schedule;

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
    }

}

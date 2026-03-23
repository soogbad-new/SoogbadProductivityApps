package com.soogbad.soogbadcalendar;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.Schedule;

import java.util.Date;

public class Event extends Item {

    public Event(String uuid, String title, Date time, Schedule repeatSchedule) {
        UUID = uuid; Title = title; Time = time; RepeatSchedule = repeatSchedule;
    }

    public Date Time;
    public Schedule RepeatSchedule;

    public static Event create(String uuid, String title) {
        return new Event(uuid, title);
    }

}

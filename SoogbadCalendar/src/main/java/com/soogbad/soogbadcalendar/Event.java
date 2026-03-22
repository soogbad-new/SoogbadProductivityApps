package com.soogbad.soogbadcalendar;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.Schedule;

import java.util.Date;

public class Event extends Item {

    public Event(String UUID, String Title, Date time, Schedule repeatSchedule) {
        super(UUID, Title);
        this.Time = time;
        this.RepeatSchedule = repeatSchedule;
    }

    public Date Time;
    public Schedule RepeatSchedule;

}

package com.soogbad.soogbadcalendar;

import com.soogbad.sharedmodule.BaseItem;
import com.soogbad.sharedmodule.Schedule;

import java.util.Date;

public class Event extends BaseItem {

    public Event(String UUID, String Title, String Content, Date time, Schedule repeatSchedule) {
        super(UUID, Title, Content);
        this.Time = time;
        this.RepeatSchedule = repeatSchedule;
    }

    public Date Time;
    public Schedule RepeatSchedule;

}

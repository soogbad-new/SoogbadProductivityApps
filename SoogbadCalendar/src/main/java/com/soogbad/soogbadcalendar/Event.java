package com.soogbad.soogbadcalendar;

import com.soogbad.commonmodule.BaseItem;
import com.soogbad.commonmodule.Schedule;

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

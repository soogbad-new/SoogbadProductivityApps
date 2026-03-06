package com.soogbad.soogbadreminders;

import com.soogbad.sharedmodule.BaseItem;
import com.soogbad.sharedmodule.Schedule;

import java.util.Date;

public class Reminder extends BaseItem {

    public Reminder(String UUID, String Title, String Content, Date time, Schedule repeatSchedule) {
        super(UUID, Title, Content);
        this.Time = time;
        this.RepeatSchedule = repeatSchedule;
    }

    public Date Time;
    public Schedule RepeatSchedule;

}

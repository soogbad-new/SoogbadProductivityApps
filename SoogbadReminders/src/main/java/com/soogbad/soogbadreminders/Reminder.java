package com.soogbad.soogbadreminders;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.Schedule;

import java.util.Date;

public class Reminder extends Item {

    public Reminder(String UUID, String Title, Date time, Schedule repeatSchedule) {
        super(UUID, Title);
        this.Time = time;
        this.RepeatSchedule = repeatSchedule;
    }

    public Date Time;
    public Schedule RepeatSchedule;

}

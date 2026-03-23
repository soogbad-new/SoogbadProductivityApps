package com.soogbad.soogbadreminders;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.Schedule;

import java.util.Date;
import java.util.Map;

public class Reminder extends Item {

    public Reminder(String uuid, String title) {
        UUID = uuid; Title = title;
    }

    public static Reminder create(String uuid, String title) {
        return new Reminder(uuid, title);
    }

}

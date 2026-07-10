package com.soogbad.soogbadtodo;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.richtext.RichTextSerializer;

import android.text.SpannedString;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class TodoList extends Item<TodoList.Options> implements Item.SchedulableItem {

    public TodoList(String uuid, String title, Options options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static TodoList create(String uuid, String title, Options options) {
        return new TodoList(uuid, title, options);
    }

    public static class Options extends Item.Options {

        public Options(DayOfWeek day, int hour, int minute, SpannedString defaultContent, boolean skipNextRun) {
            Day = day; Hour = hour; Minute = minute; DefaultContent = defaultContent; SkipNextRun = skipNextRun;
        }

        public DayOfWeek Day;
        public int Hour;
        public int Minute;
        public SpannedString DefaultContent;
        public boolean SkipNextRun;

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("dayOfWeek", Day.ordinal());
                json.put("hour", Hour);
                json.put("minute", Minute);
                json.put("skipNextRun", SkipNextRun);
                json.put("defaultContent", new JSONObject(RichTextSerializer.serialize(DefaultContent)));
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }
        
    }

    public static Options parseOptionsFromJson(JSONObject json) {
        try {
            DayOfWeek day = DayOfWeek.values()[json.getInt("dayOfWeek")];
            int hour = json.getInt("hour");
            int minute = json.getInt("minute");
            boolean skipNextRun = json.getBoolean("skipNextRun");
            SpannedString defaultContent = RichTextSerializer.deserialize(json.getJSONObject("defaultContent").toString());
            return new Options(day, hour, minute, defaultContent, skipNextRun);
        } catch(JSONException e) { throw new RuntimeException(e); }
    }

    public static Options getDefaultOptions() {
        return new Options(DayOfWeek.MONDAY, 9, 0, new SpannedString(""), false);
    }

    @Override
    public Calendar getNextOccurrence() {
        Calendar now = Calendar.getInstance();
        Calendar nextOccurrence = (Calendar)now.clone();
        nextOccurrence.set(Calendar.DAY_OF_WEEK, Options.Day.calendarDay);
        nextOccurrence.set(Calendar.HOUR_OF_DAY, Options.Hour); nextOccurrence.set(Calendar.MINUTE, Options.Minute);
        nextOccurrence.set(Calendar.SECOND, 0); nextOccurrence.set(Calendar.MILLISECOND, 0);
        if(!nextOccurrence.after(now))
            nextOccurrence.add(Calendar.DAY_OF_YEAR, 7);
        return nextOccurrence;
    }

    public enum DayOfWeek {
        SUNDAY(Calendar.SUNDAY), MONDAY(Calendar.MONDAY), TUESDAY(Calendar.TUESDAY), WEDNESDAY(Calendar.WEDNESDAY), THURSDAY(Calendar.THURSDAY), FRIDAY(Calendar.FRIDAY), SATURDAY(Calendar.SATURDAY);

        DayOfWeek(int calendarDay) { this.calendarDay = calendarDay; }

        public final int calendarDay;

        public String displayName() {
            String name = name();
            return name.charAt(0) + name.substring(1).toLowerCase();
        }
    }

}

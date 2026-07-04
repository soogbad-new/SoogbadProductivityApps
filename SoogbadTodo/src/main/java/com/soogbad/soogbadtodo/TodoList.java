package com.soogbad.soogbadtodo;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.richtext.RichTextSerializer;

import android.text.SpannedString;

import org.json.JSONException;
import org.json.JSONObject;

public class TodoList extends Item<TodoList.Options> {

    public TodoList(String uuid, String title, Options options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static TodoList create(String uuid, String title, Options options) {
        return new TodoList(uuid, title, options);
    }

    public static class Options extends Item.Options {

        public Options(DayOfWeek day, int hour, int minute, SpannedString defaultText, boolean skipNextRun) {
            Day = day; Hour = hour; Minute = minute; DefaultText = defaultText; SkipNextRun = skipNextRun;
        }

        public DayOfWeek Day;
        public int Hour;
        public int Minute;
        public SpannedString DefaultText;
        public boolean SkipNextRun;

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("dayOfWeek", Day.ordinal());
                json.put("hour", Hour);
                json.put("minute", Minute);
                json.put("skipNextRun", SkipNextRun);
                json.put("defaultText", new JSONObject(RichTextSerializer.serialize(DefaultText)));
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }

        public static Options fromJson(JSONObject json) {
            try {
                DayOfWeek day = DayOfWeek.values()[json.getInt("dayOfWeek")];
                int hour = json.getInt("hour");
                int minute = json.getInt("minute");
                boolean skipNextRun = json.getBoolean("skipNextRun");
                SpannedString defaultText = RichTextSerializer.deserialize(json.getJSONObject("defaultText").toString());
                return new Options(day, hour, minute, defaultText, skipNextRun);
            } catch(JSONException e) { throw new RuntimeException(e); }
        }
    }

    public enum DayOfWeek {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

        public String displayName() {
            String name = name();
            return name.charAt(0) + name.substring(1).toLowerCase();
        }
    }

}

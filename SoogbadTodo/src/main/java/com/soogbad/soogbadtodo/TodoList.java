package com.soogbad.soogbadtodo;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.Schedule;
import com.soogbad.sharedmodule.richtext.RichTextSerializer;

import android.text.SpannedString;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class TodoList extends Item<TodoList.TodoListOptions> {

    public TodoList(String uuid, String title, TodoListOptions options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static TodoList create(String uuid, String title, TodoListOptions options) {
        return new TodoList(uuid, title, options);
    }

    public static class TodoListOptions extends Item.ItemOptions {

        public TodoListOptions(Date time, Schedule repeatSchedule, SpannedString defaultText, boolean skipNextRun) { Time = time; RepeatSchedule = repeatSchedule; DefaultText = defaultText; SkipNextRun = skipNextRun; }

        public Date Time;
        public Schedule RepeatSchedule;
        public SpannedString DefaultText;
        public boolean SkipNextRun;

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("time", Time.getTime());
                json.put("repeatSchedule", RepeatSchedule.name());
                json.put("skipNextRun", SkipNextRun);
                json.put("defaultText", new JSONObject(RichTextSerializer.serialize(DefaultText)));
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }

        public static TodoListOptions fromJson(JSONObject json) {
            try {
                Date time = new Date(json.getLong("time"));
                Schedule repeatSchedule = Schedule.valueOf(json.getString("repeatSchedule"));
                boolean skipNextRun = json.getBoolean("skipNextRun");
                SpannedString defaultText = RichTextSerializer.deserialize(json.getJSONObject("defaultText").toString());
                return new TodoListOptions(time, repeatSchedule, defaultText, skipNextRun);
            } catch(JSONException e) { throw new RuntimeException(e); }
        }
    }

}

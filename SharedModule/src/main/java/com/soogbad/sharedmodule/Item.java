package com.soogbad.sharedmodule;

import android.text.SpannedString;

import org.json.JSONObject;

import java.util.Date;

public abstract class Item<O extends Item.ItemOptions> {

    public String UUID;
    public String Title;
    public SpannedString Content;
    public O Options;

    @FunctionalInterface
    public interface Creator<T extends Item<O>, O extends ItemOptions> {
        @SuppressWarnings("unused")
        T create(String uuid, String title, O options);
    }

    @FunctionalInterface
    public interface OptionsParser<O extends ItemOptions> {
        O parse(JSONObject ignoredJson);
    }

    public static class ItemOptions {
        public JSONObject toJson() { return new JSONObject(); }
    }

    public static class SchedulableItemOptions extends ItemOptions {
        public Date Time;
        public Schedule RepeatSchedule;
    }

}

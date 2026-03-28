package com.soogbad.sharedmodule;

import android.text.SpannedString;

import org.json.JSONObject;

public abstract class Item<O extends Item.ItemOptions> {

    public String UUID;
    public String Title;
    public SpannedString Content;
    public O Options;

    @FunctionalInterface
    public interface Creator<T extends Item<O>, O extends ItemOptions> {
        T create(String uuid, String title, O options);
    }

    @FunctionalInterface
    public interface OptionsParser<O extends ItemOptions> {
        O parse(JSONObject json);
    }

    public static class ItemOptions {
        public JSONObject toJson() { return new JSONObject(); }
    }

}

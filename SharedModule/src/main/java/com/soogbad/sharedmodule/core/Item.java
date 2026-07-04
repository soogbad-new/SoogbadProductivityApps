package com.soogbad.sharedmodule.core;

import org.json.JSONObject;

public abstract class Item<O extends Item.Options> {

    public String UUID;
    public String Title;
    public O Options;
    public long DeletedAt;

    @FunctionalInterface
    public interface Creator<T extends Item<O>, O extends Options> {
        @SuppressWarnings("unused")
        T create(String uuid, String title, O options);
    }

    @FunctionalInterface
    public interface OptionsParser<O extends Options> {
        O parse(JSONObject ignoredJson);
    }

    public static class Options {
        public JSONObject toJson() { return new JSONObject(); }
    }

}

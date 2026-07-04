package com.soogbad.soogbadnotes;

import com.soogbad.sharedmodule.core.Item;

import org.json.JSONException;
import org.json.JSONObject;

public class Note extends Item<Note.Options> {

    public Note(String uuid, String title, Options options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static Note create(String uuid, String title, Options options) {
        return new Note(uuid, title, options);
    }

    public static class Options extends Item.Options {
        public long LastViewed;

        public Options(long lastViewed) { LastViewed = lastViewed; }

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("lastViewed", LastViewed);
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }

        public static Options fromJson(JSONObject json) {
            try {
                long lastViewed = json.getLong("lastViewed");
                return new Options(lastViewed);
            } catch(JSONException e) { throw new RuntimeException(e); }
        }
    }

}

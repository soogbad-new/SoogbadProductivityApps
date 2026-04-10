package com.soogbad.soogbadnotes;

import com.soogbad.sharedmodule.Item;

import org.json.JSONException;
import org.json.JSONObject;

public class Note extends Item<Note.NoteOptions> {

    public Note(String uuid, String title, NoteOptions options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static Note create(String uuid, String title, NoteOptions options) {
        return new Note(uuid, title, options);
    }

    public static class NoteOptions extends ItemOptions {
        public long LastViewed;

        public NoteOptions(long lastViewed) { LastViewed = lastViewed; }

        @Override
        public JSONObject toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("lastViewed", LastViewed);
                return json;
            } catch(JSONException e) { throw new RuntimeException(e); }
        }

        public static NoteOptions fromJson(JSONObject json) {
            try {
                long lastViewed = json.getLong("lastViewed");
                return new NoteOptions(lastViewed);
            } catch(JSONException e) { throw new RuntimeException(e); }
        }
    }

}

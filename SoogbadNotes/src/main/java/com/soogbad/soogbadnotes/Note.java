package com.soogbad.soogbadnotes;

import com.soogbad.sharedmodule.Item;

import org.json.JSONObject;

public class Note extends Item<Note.NoteOptions> {

    public Note(String uuid, String title, NoteOptions options) {
        UUID = uuid; Title = title; Options = options;
    }

    public static Note create(String uuid, String title, NoteOptions options) {
        return new Note(uuid, title, options);
    }

    public static class NoteOptions extends ItemOptions {
        public static NoteOptions fromJson(JSONObject ignoredJson) {
            return new NoteOptions();
        }
    }

}

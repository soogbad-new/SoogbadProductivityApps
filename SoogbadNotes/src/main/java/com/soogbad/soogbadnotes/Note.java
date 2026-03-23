package com.soogbad.soogbadnotes;

import com.soogbad.sharedmodule.Item;

public class Note extends Item {

    public Note(String uuid, String title) {
        UUID = uuid; Title = title;
    }

    public static Note create(String uuid, String title) {
        return new Note(uuid, title);
    }

}

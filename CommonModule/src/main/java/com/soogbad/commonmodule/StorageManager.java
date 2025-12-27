package com.soogbad.commonmodule;

import android.app.Application;

import java.util.ArrayList;

public class StorageManager {

    private ArrayList<BaseNote> noteItems = new ArrayList<>();

    public StorageManager(Application app) {
        load();
    }
    private void load() {
        noteItems.add(null);
    }

    public ArrayList<BaseNote> getNoteItems() {
        return noteItems;
    }
    public void setNoteItems(ArrayList<BaseNote> noteItems) {
        this.noteItems = noteItems;
    }

}

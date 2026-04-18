package com.soogbad.soogbadnotes;

import android.app.Application;

import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.RichCharacterStyle;
import com.soogbad.sharedmodule.StorageManager;

public class SoogbadNotesApplication extends Application {

    private ItemsManager<Note, Note.NoteOptions> notesManager;
    public ItemsManager<Note, Note.NoteOptions> getNotesManager() { return notesManager; }

    @Override
    public void onCreate() {
        super.onCreate();
        RichCharacterStyle.DEFAULT_TEXT_SIZE = RichCharacterStyle.TextSize.SIZE_16;
        notesManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Note::create, Note.NoteOptions::fromJson);
        notesManager.loadItems();
    }

}

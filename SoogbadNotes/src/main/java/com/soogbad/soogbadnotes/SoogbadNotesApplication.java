package com.soogbad.soogbadnotes;

import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;
import com.soogbad.sharedmodule.richtext.RichCharacterStyle;

public class SoogbadNotesApplication extends ItemApplication<Note, Note.NoteOptions> {

    @Override
    public void onCreate() {
        super.onCreate();
        RichCharacterStyle.DEFAULT_TEXT_SIZE = RichCharacterStyle.TextSize.SIZE_16;
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Note::create, Note.NoteOptions::fromJson);
        itemsManager.loadItems();
        itemsManager.getItems().sort((a, b) -> Long.compare(b.Options.LastViewed, a.Options.LastViewed));
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public String getAppName() { return "SoogbadNotes"; }
            @Override public String getItemName() { return "Note"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return NoteActivity.class; }
            @Override public boolean hasConfigurableOptions() { return false; }
            @Override public void launchEditItemOptionsDialog() { }
            @Override public Note.NoteOptions launchCreateItemOptionsDialog() { return null; }
        };
    }

}

package com.soogbad.soogbadnotes;

import com.soogbad.sharedmodule.ItemActivity;
import com.soogbad.sharedmodule.ItemApplication;
import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.StorageManager;
import com.soogbad.sharedmodule.RichCharacterStyle;

public class SoogbadNotesApplication extends ItemApplication<Note, Note.NoteOptions> {

    @Override
    public void onCreate() {
        super.onCreate();
        RichCharacterStyle.DEFAULT_TEXT_SIZE = RichCharacterStyle.TextSize.SIZE_16;
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Note::create, Note.NoteOptions::fromJson);
        itemsManager.loadItems();
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public String getItemUuidPrefix() { return "NOTE-"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return NoteActivity.class; }
        };
    }

}

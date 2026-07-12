package com.soogbad.soogbadnotes;

import android.content.Context;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.scheduling.ItemScheduler;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;
import com.soogbad.sharedmodule.richtext.RichCharacterStyle;

import java.util.function.Consumer;

public class SoogbadNotesApplication extends ItemApplication<Note, Note.Options> {

    @Override
    public void onCreate() {
        super.onCreate();
        RichCharacterStyle.DEFAULT_TEXT_SIZE = RichCharacterStyle.TextSize.SIZE_16;
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Note::create, Note::parseOptionsFromJson);
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
            @Override public void createItemOptionsDialog(Context context, Item.Options initialOptions, Consumer<Item.Options> callback) { }
            @Override public void onItemOptionsChanged(Item<?> item) { }
            @Override public ItemScheduler getItemScheduler() { return null; }
        };
    }

}

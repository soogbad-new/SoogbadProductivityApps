package com.soogbad.soogbadcalendar;

import android.content.Context;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;
import com.soogbad.sharedmodule.scheduling.ItemScheduler;
import java.util.function.Consumer;

public class SoogbadCalendarApplication extends ItemApplication<Event, Event.Options> {

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Event::create, Event::parseOptionsFromJson);
        itemsManager.loadItems();
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public String getAppName() { return "SoogbadCalendar"; }
            @Override public String getItemName() { return "Event"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return EventActivity.class; }
            @Override public boolean hasConfigurableOptions() { return true; }
            @Override public void createItemOptionsDialog(Context context, Item.Options initialOptions, Consumer<Item.Options> callback) {

            }
            @Override public void onItemOptionsChanged(Item<?> item) {

            }
            @Override public ItemScheduler getItemScheduler() { return null; }
        };
    }

}

package com.soogbad.soogbadcalendar;

import android.content.Context;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;

import java.util.function.Consumer;

public class SoogbadCalendarApplication extends ItemApplication<Event, Event.EventOptions> {

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Event::create, Event.EventOptions::fromJson);
        itemsManager.loadItems();
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public String getAppName() { return "SoogbadCalendar"; }
            @Override public String getItemName() { return "Event"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return EventActivity.class; }
            @Override public boolean hasConfigurableOptions() { return true; }
            @Override public void launchEditItemOptionsDialog(Context context, Item<?> item) { }
            @Override public void launchCreateItemOptionsDialog(Context context, Consumer<Item.ItemOptions> callback) { }
        };
    }

}

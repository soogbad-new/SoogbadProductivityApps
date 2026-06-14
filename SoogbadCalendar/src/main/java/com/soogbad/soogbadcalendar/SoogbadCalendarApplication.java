package com.soogbad.soogbadcalendar;

import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;

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
            @Override public String getItemUuidPrefix() { return "EVENT-"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return EventActivity.class; }
        };
    }

}

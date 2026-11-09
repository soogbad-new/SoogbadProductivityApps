package com.soogbad.soogbadcalendar;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;
import com.soogbad.sharedmodule.scheduling.ItemScheduler;
import java.util.function.Consumer;

public class SoogbadCalendarApplication extends ItemApplication<Event, Event.Options> {

    // Sound channel was created without vibration and channels are immutable, so it was replaced; deleted on startup so it doesn't linger in settings
    private static final String LEGACY_SOUND_CHANNEL_ID = "soogbad_calendar_events_sound";

    private ItemScheduler itemScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Event::create, Event::parseOptionsFromJson);
        itemScheduler = new ItemScheduler(this, EventAlarmReceiver.class);
        itemsManager.setItemScheduler(itemScheduler);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.deleteNotificationChannel(LEGACY_SOUND_CHANNEL_ID);
        for(Event.Importance importance : Event.Importance.values()) {
            NotificationChannel channel = new NotificationChannel(importance.channelId, "Events (" + importance.displayName() + ")", importance.channelImportance);
            channel.enableVibration(importance == Event.Importance.SOUND);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onFirstActivityOpened() {
        if(!getSystemService(AlarmManager.class).canScheduleExactAlarms())
            startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        itemScheduler.scheduleAllItems();
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public String getAppName() { return "SoogbadCalendar"; }
            @Override public String getItemName() { return "Event"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return EventActivity.class; }
            @Override public boolean hasConfigurableOptions() { return true; }
            @Override public void createItemOptionsDialog(Context context, Item.Options initialOptions, Consumer<Item.Options> callback) {
                new EventOptionsDialog(context, (Event.Options)initialOptions, callback::accept).show();
            }
            @Override public void onItemOptionsChanged(Item<?> item) {
                getItemScheduler().scheduleItem((Event)item);
            }
            @Override public ItemScheduler getItemScheduler() { return itemScheduler; }
        };
    }

}

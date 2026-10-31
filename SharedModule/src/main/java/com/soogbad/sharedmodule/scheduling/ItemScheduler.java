package com.soogbad.sharedmodule.scheduling;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresPermission;

import java.util.Calendar;

public class ItemScheduler {

    private final Context context;
    private final AlarmManager alarmManager;
    private final Class<? extends BroadcastReceiver> receiverClass;

    public ItemScheduler(Context context, Class<? extends BroadcastReceiver> receiverClass) {
        this.context = context.getApplicationContext(); this.receiverClass = receiverClass;
        this.alarmManager = (AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    public void scheduleAllItems() {
        ItemsManager<?, ?> itemsManager = Utility.getItemsManager(context);
        if(!itemsManager.isListLoaded()) itemsManager.loadItems();
        for(Item<?> item : itemsManager.getItems()) {
            Calendar nextOccurrence = ((Item.SchedulableItem)item).getNextOccurrence();
            if(nextOccurrence != null) // Don't set an alarm whose time is in the past. No need to cancel here because any outdated alarm would have already been canceled in scheduleItem
                setAlarm(item.UUID, nextOccurrence);
        }
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    public <T extends Item<?> & Item.SchedulableItem> void scheduleItem(T item) {
        Calendar nextOccurrence = item.getNextOccurrence();
        if(nextOccurrence == null) // Don't set an alarm whose time is in the past
            cancelAlarm(item.UUID); // Clear any alarm still pending from before
        else
            setAlarm(item.UUID, nextOccurrence);
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void setAlarm(String itemUuid, Calendar time) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), buildPendingIntent(itemUuid));
    }
    
    public void cancelAlarm(String itemUuid) {
        alarmManager.cancel(buildPendingIntent(itemUuid));
    }

    private PendingIntent buildPendingIntent(String itemUuid) {
        Intent intent = new Intent(context, receiverClass).putExtra("uuid", itemUuid);
        return PendingIntent.getBroadcast(context, Math.abs(itemUuid.hashCode()), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

}

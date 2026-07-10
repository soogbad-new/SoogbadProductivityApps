package com.soogbad.sharedmodule.scheduling;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.Utility;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresPermission;

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
        for(Item<?> item : Utility.getItemsManager(context).getItems())
            scheduleItem((Item<?> & Item.SchedulableItem)item);
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    public <T extends Item<?> & Item.SchedulableItem> void scheduleItem(T item) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, item.getNextOccurrence().getTimeInMillis(), buildPendingIntent(item.UUID));
    }
    
    public void cancelItem(String itemUuid) {
        alarmManager.cancel(buildPendingIntent(itemUuid));
    }

    private PendingIntent buildPendingIntent(String itemUuid) {
        Intent intent = new Intent(context, receiverClass).putExtra("uuid", itemUuid);
        return PendingIntent.getBroadcast(context, Math.abs(itemUuid.hashCode()), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

}

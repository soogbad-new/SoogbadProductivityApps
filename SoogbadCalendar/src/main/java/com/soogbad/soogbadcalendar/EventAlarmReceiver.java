package com.soogbad.soogbadcalendar;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.soogbad.sharedmodule.scheduling.ItemAlarmReceiver;

public class EventAlarmReceiver extends ItemAlarmReceiver<Event> {

    @Override
    protected Event getItem(Context context, String uuid) { return ((SoogbadCalendarApplication)context.getApplicationContext()).getItemsManager().getItem(uuid); }

    @Override
    protected void onAlarm(Context context, Event event) {
        PendingIntent contentIntent = TaskStackBuilder.create(context).addNextIntentWithParentStack(new Intent(context, EventActivity.class).putExtra("item_uuid", event.UUID))
                .getPendingIntent(Math.abs(event.UUID.hashCode()), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(context, SoogbadCalendarApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(event.Title).setContentText("Event reminder")
                .setContentIntent(contentIntent).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH).build();
        NotificationManagerCompat.from(context).notify(event.UUID.hashCode(), notification);
    }

}

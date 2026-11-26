package com.soogbad.soogbadcalendar;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.soogbad.sharedmodule.scheduling.ItemAlarmReceiver;

import java.time.LocalDate;

public class EventAlarmReceiver extends ItemAlarmReceiver<Event> {

    @Override
    protected Event getItem(Context context, String uuid) { return ((SoogbadCalendarApplication)context.getApplicationContext()).getItemsManager().getItem(uuid); }

    @Override
    protected void onAlarm(Context context, Event event) { showNotification(context, event, event.Options.Importance); }

    @SuppressLint({"MissingPermission", "FullScreenIntentPolicy"})
    public static void showNotification(Context context, Event event, Event.Importance importance) {
        Intent dayIntent = new Intent(context, DayListActivity.class).putExtra("date", LocalDate.now().toString());
        PendingIntent contentIntent = TaskStackBuilder.create(context).addNextIntentWithParentStack(dayIntent).addNextIntent(new Intent(context, EventActivity.class).putExtra("item_uuid", event.UUID))
                .getPendingIntent(Math.abs(event.UUID.hashCode()), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder = new Notification.Builder(context, importance.channelId)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(event.Title).setContentIntent(contentIntent).setAutoCancel(true);
        if(importance == Event.Importance.ALARM) {
            PendingIntent alarmIntent = PendingIntent.getActivity(context, Math.abs(event.UUID.hashCode()), new Intent(context, AlarmActivity.class).putExtra("item_uuid", event.UUID), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            builder.setCategory(Notification.CATEGORY_ALARM).setFullScreenIntent(alarmIntent, true).setFlag(Notification.FLAG_INSISTENT, true);
        }
        NotificationManagerCompat.from(context).notify(event.UUID.hashCode(), builder.build());
    }

}

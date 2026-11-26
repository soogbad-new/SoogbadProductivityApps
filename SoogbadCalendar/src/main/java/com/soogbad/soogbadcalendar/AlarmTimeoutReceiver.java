package com.soogbad.soogbadcalendar;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;

public class AlarmTimeoutReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String uuid = intent.getStringExtra("uuid");
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        for(StatusBarNotification notification : notificationManager.getActiveNotifications()) {
            if(notification.getId() != uuid.hashCode() || !Event.Importance.ALARM.channelId.equals(notification.getNotification().getChannelId())) continue; // already dismissed or opened
            Event event = ((SoogbadCalendarApplication)context.getApplicationContext()).getItemsManager().getItem(uuid);
            EventAlarmReceiver.showNotification(context, event, Event.Importance.SILENT); // create new silent notification instead of the ringing alarm notification
        }
    }

}

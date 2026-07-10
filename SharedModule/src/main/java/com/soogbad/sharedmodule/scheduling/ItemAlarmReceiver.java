package com.soogbad.sharedmodule.scheduling;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.Utility;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresPermission;

public abstract class ItemAlarmReceiver<T extends Item<?> & Item.SchedulableItem> extends BroadcastReceiver {

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    @Override
    public final void onReceive(Context context, Intent intent) {
        T item = getItem(context, intent.getStringExtra("uuid"));
        if(item == null) return;
        onAlarm(context, item);
        Utility.getAppUtility(context).getItemScheduler().scheduleItem(item);
    }

    protected abstract T getItem(Context context, String uuid);
    protected abstract void onAlarm(Context context, T item);

}

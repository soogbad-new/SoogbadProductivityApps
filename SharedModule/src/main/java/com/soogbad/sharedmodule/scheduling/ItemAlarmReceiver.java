package com.soogbad.sharedmodule.scheduling;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class ItemAlarmReceiver<T extends Item<?> & Item.SchedulableItem> extends BroadcastReceiver {

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

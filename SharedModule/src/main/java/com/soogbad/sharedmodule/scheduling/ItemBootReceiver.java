package com.soogbad.sharedmodule.scheduling;

import com.soogbad.sharedmodule.core.Utility;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresPermission;

public class ItemBootReceiver extends BroadcastReceiver {

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
            Utility.getAppUtility(context).getItemScheduler().scheduleAllItems();
    }

}

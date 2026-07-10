package com.soogbad.sharedmodule.scheduling;

import com.soogbad.sharedmodule.core.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ItemBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
            Utility.getAppUtility(context).getItemScheduler().scheduleAllItems();
    }

}

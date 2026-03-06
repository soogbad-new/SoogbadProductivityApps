package com.soogbad.sharedmodule;

import android.graphics.Color;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class Utility {

    public static void setWindowProperties(AppCompatActivity activity, int activityID, int toolbarID) {
        activity.setContentView(activityID);
        activity.setSupportActionBar(activity.findViewById(toolbarID));
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().setNavigationBarColor(Color.BLACK);
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
    }

}

package com.soogbad.sharedmodule;

import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.UUID;

public class Utility {

    public static void setWindowProperties(AppCompatActivity activity, int activityID, int toolbarID) {
        activity.setContentView(activityID);
        activity.setSupportActionBar(activity.findViewById(toolbarID));
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().setNavigationBarColor(Color.BLACK);
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
    }

    public static <T extends Item<?>> String generateUniqueUUID(ArrayList<T> items) {
        String uuid = UUID.randomUUID().toString();
        if(items.stream().anyMatch((item) -> item.UUID.equals(uuid)))
            return generateUniqueUUID(items);
        else
            return uuid;
    }

}

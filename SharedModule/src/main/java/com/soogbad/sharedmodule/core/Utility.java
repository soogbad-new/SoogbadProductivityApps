package com.soogbad.sharedmodule.core;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Context;
import android.graphics.Rect;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.UUID;

public class Utility {

    public static Activity getActivity(Context context) {
        while(context instanceof ContextWrapper) {
            if(context instanceof Activity)
                return (Activity)context;
            context = ((ContextWrapper)context).getBaseContext();
        }
        throw new RuntimeException("Could not find Activity From Context");
    }

    public static boolean isMotionEventInsideView(MotionEvent motionEvent, View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect.contains((int)motionEvent.getRawX(), (int)motionEvent.getRawY());
    }

    public static ItemsManager<?, ?> getItemsManager(Context context) {
        return ((ItemApplication<?, ?>)context.getApplicationContext()).getItemsManager();
    }
    public static ItemApplication.AppUtility getAppUtility(Context context) {
        return ((ItemApplication<?, ?>)context.getApplicationContext()).getAppUtility();
    }

    public static void setWindowProperties(AppCompatActivity activity, int activityID, int toolbarID) {
        activity.setContentView(activityID);
        activity.setSupportActionBar(activity.findViewById(toolbarID));
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
    }

    public static <T extends Item<?>> String generateUniqueUUID(ArrayList<T> items) {
        String uuid = UUID.randomUUID().toString();
        if(items.stream().anyMatch((item) -> item.UUID.equals(uuid)))
            return generateUniqueUUID(items);
        else
            return uuid;
    }

    public static boolean isLinkUrlValid(String url) {
        return Patterns.WEB_URL.matcher(url).matches() || url.startsWith("EVENT-") || url.startsWith("NOTE-") || url.startsWith("REMINDER-");
    }

}

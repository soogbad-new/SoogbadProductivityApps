package com.soogbad.soogbadcalendar;

import android.app.Application;

public class SoogbadCalendarApplication extends Application {

    private static SoogbadCalendarApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static SoogbadCalendarApplication getAppContext() {
        return app;
    }

}

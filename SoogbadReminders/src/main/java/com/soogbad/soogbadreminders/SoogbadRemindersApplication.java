package com.soogbad.soogbadreminders;

import android.app.Application;

public class SoogbadRemindersApplication extends Application {

    private static SoogbadRemindersApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static SoogbadRemindersApplication getAppContext() {
        return app;
    }

}

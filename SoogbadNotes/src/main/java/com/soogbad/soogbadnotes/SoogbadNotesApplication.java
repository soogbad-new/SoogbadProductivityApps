package com.soogbad.soogbadnotes;

import android.app.Application;

public class SoogbadNotesApplication extends Application {

    private static SoogbadNotesApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static SoogbadNotesApplication getAppContext() {
        return app;
    }

}

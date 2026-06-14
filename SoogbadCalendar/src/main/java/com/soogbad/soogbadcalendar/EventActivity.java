package com.soogbad.soogbadcalendar;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.Utility;

public class EventActivity extends ItemActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_event, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}

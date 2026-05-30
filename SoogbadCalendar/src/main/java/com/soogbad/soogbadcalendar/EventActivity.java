package com.soogbad.soogbadcalendar;

import android.os.Bundle;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ItemActivity;
import com.soogbad.sharedmodule.Utility;

public class EventActivity extends ItemActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_event, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
    }

}

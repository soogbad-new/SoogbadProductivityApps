package com.soogbad.soogbadreminders;

import android.os.Bundle;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ItemActivity;
import com.soogbad.sharedmodule.Utility;

public class ReminderActivity extends ItemActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_reminder, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
    }

}

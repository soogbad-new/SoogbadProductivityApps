package com.soogbad.soogbadcalendar;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.soogbad.sharedmodule.Utility;

public class EventActivity extends AppCompatActivity {

    private EventLayout eventLayout;
    private ConstraintLayout eventToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_event, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        eventLayout = findViewById(R.id.eventLayout); eventToolbar = findViewById(R.id.eventToolbar);
        eventLayout.setEditText(findViewById(R.id.eventEditText));
    }

    public void onBoldButtonClick(View view) { eventLayout.onBoldButtonClick(); }
    public void onItalicButtonClick(View view) { eventLayout.onItalicButtonClick(); }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
        eventToolbar.setPadding(0, 0, 0, keyboard.bottom - systemBars.bottom);
        return insets;
    }

}

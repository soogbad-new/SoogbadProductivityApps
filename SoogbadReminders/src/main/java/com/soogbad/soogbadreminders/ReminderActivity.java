package com.soogbad.soogbadreminders;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.soogbad.sharedmodule.Utility;

public class ReminderActivity extends AppCompatActivity {

    private ReminderLayout reminderLayout;
    private ConstraintLayout reminderToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_reminder, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        reminderLayout = findViewById(R.id.reminderLayout); reminderToolbar = findViewById(R.id.reminderToolbar);
        reminderLayout.setEditText(findViewById(R.id.reminderEditText));
    }

    public void onBoldButtonClick(View view) { reminderLayout.onBoldButtonClick(); }
    public void onItalicButtonClick(View view) { reminderLayout.onItalicButtonClick(); }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
        reminderToolbar.setPadding(0, 0, 0, keyboard.bottom - systemBars.bottom);
        return insets;
    }

}

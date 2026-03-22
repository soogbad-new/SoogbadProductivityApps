package com.soogbad.soogbadnotes;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.ItemLayout;
import com.soogbad.sharedmodule.StorageManager;
import com.soogbad.sharedmodule.Utility;

public class NoteActivity extends AppCompatActivity {

    private ItemLayout noteLayout;
    private ConstraintLayout noteToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_note, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        noteLayout = findViewById(R.id.noteLayout); noteToolbar = findViewById(R.id.noteToolbar);
        Note note = StorageManager.getItem(getIntent().getStringExtra("item_uuid"));
        noteLayout.init(findViewById(R.id.noteEditText), note);
    }

    public void onBoldButtonClick(View view) { noteLayout.onBoldButtonClick(); }
    public void onItalicButtonClick(View view) { noteLayout.onItalicButtonClick(); }

    public void onDeleteButtonClick(View view) {
        noteLayout.deleteItem();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        noteLayout.saveItem();
    }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
        noteToolbar.setPadding(0, 0, 0, keyboard.bottom - systemBars.bottom);
        return insets;
    }

}

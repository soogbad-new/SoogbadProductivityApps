package com.soogbad.soogbadnotes;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.soogbad.commonmodule.Utility;

public class NoteActivity extends AppCompatActivity {

    private NoteLayout noteLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_note, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        noteLayout = findViewById(R.id.noteLayout);
        noteLayout.setEditText(findViewById(R.id.noteEditText));
        noteLayout.setExampleText();
    }

    public void onBoldButtonClick(View view) { noteLayout.onBoldButtonClick(); }
    public void onItalicButtonClick(View view) { noteLayout.onItalicButtonClick(); }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        return insets;
    }

}

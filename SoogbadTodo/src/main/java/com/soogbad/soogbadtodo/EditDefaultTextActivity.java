package com.soogbad.soogbadtodo;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.text.SpannedString;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.soogbad.sharedmodule.richtext.RichEditText;
import com.soogbad.sharedmodule.richtext.RichTextSerializer;
import com.soogbad.sharedmodule.ui.ItemLayout;
import com.soogbad.sharedmodule.core.Utility;

public class EditDefaultTextActivity extends AppCompatActivity {

    private ItemLayout itemLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.edit_default_text_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        itemLayout = findViewById(R.id.itemLayout);
        SpannedString initialDefaultText = RichTextSerializer.deserialize(getIntent().getStringExtra("initial_default_text"));
        itemLayout.getContentEditText().setTextSafely(initialDefaultText);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent result = new Intent();
        result.putExtra("default_text", RichTextSerializer.serialize(itemLayout.getContentEditText().getTextIncludingHiddenContent()));
        setResult(RESULT_OK, result);
    }

    private WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
        itemLayout.getFormattingToolbar().setPadding(0, 0, 0, keyboard.bottom - systemBars.bottom);
        if(keyboard.bottom == 0) {
            View focused = getCurrentFocus();
            if(focused != null) focused.clearFocus();
        }
        else {
            itemLayout.post(() -> itemLayout.getContentEditText().scrollToCursor());
        }
        return insets;
    }

}

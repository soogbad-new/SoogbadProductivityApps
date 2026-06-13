package com.soogbad.sharedmodule;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

public abstract class ItemActivity extends AppCompatActivity {

    protected ItemLayout itemLayout;
    protected ItemsManager<?, ?> itemsManager;

    private boolean previewMode = false;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        itemLayout = findViewById(R.id.itemLayout); ItemActionBar itemActionBar = findViewById(R.id.itemActionBar);
        itemsManager = Utility.getItemsManager(this);
        String uuid = getIntent().getStringExtra("item_uuid"); previewMode = getIntent().getBooleanExtra("preview_mode", false);
        Item<?> item = previewMode ? itemsManager.getRecycleBinItem(uuid) : itemsManager.getItem(uuid);
        if(item == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finishAndRemoveTask();
            return;
        }
        itemLayout.init(itemActionBar, itemsManager, item, previewMode);
        onItemLoaded(item);
    }
    protected void onItemLoaded(Item<?> item) {}

    @Override
    protected void onPause() {
        super.onPause();
        if(!previewMode)
            itemLayout.save();
    }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        getToolbar().setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        if(!previewMode) {
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            itemLayout.getFormattingToolbar().setPadding(0, 0, 0, keyboard.bottom - systemBars.bottom);
        }
        return insets;
    }
    protected abstract View getToolbar();

}

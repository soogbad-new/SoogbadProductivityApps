package com.soogbad.sharedmodule.ui;

import android.content.Context;
import android.text.SpannedString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.R;
import com.soogbad.sharedmodule.richtext.RichEditText;

public class ItemLayout extends ConstraintLayout {

    private final RichEditText contentEditText;
    private final FormattingToolbar formattingToolbar;
    public FormattingToolbar getFormattingToolbar() { return formattingToolbar; }

    private ItemActionBar itemActionBar;
    private ItemsManager<?, ?> itemsManager;
    private Item<?> item;

    private boolean itemDeletedGuard = false;
    private boolean contentTouched = false;
    private boolean optionsChanged = false;
    public void markOptionsChanged() { optionsChanged = true; } // call this when setting options

    public ItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_layout_content, this, true);
        contentEditText = findViewById(R.id.contentEditText); formattingToolbar = findViewById(R.id.formattingToolbar);
        formattingToolbar.init(contentEditText);
    }

    public void init(ItemActionBar itemActionBar, ItemsManager<?, ?> itemsManager, Item<?> item, boolean readOnly) {
        this.itemActionBar = itemActionBar; this.itemsManager = itemsManager; this.item = item;
        itemActionBar.init(this, item, readOnly);
        SpannedString content = itemsManager.loadItemContent(item);
        contentEditText.setIgnoreTextChanges(true); contentEditText.setText(content); contentEditText.setIgnoreTextChanges(false);
        contentEditText.setOnFocusChangeListener((view, hasFocus) -> { if(hasFocus) contentTouched = true; });
        if(readOnly) {
            contentEditText.setFocusable(false); contentEditText.setFocusableInTouchMode(false); contentEditText.setCursorVisible(false);
            formattingToolbar.setVisibility(GONE);
            itemDeletedGuard = true;
        }
    }

    public void disableReadOnly() {
        contentEditText.setFocusable(true); contentEditText.setFocusableInTouchMode(true); contentEditText.setCursorVisible(true);
        formattingToolbar.setVisibility(VISIBLE);
        itemDeletedGuard = false;
    }

    public void save() {
        if(itemDeletedGuard)
            return;
        String oldTitle = item.Title;
        item.Title = itemActionBar.getTitleEditText().getText().toString();
        if(item.Title.isEmpty()) item.Title = "Untitled";
        if(optionsChanged || !item.Title.equals(oldTitle))
            itemsManager.saveItemMetadata(item);
        if(contentTouched)
            itemsManager.saveItemContent(item, contentEditText.getTextIncludingHiddenContent());
        optionsChanged = false;
        contentTouched = contentEditText.hasFocus();
    }

    public void delete() {
        save();
        itemDeletedGuard = true;
        itemsManager.moveItemToRecycleBin(item.UUID);
    }

}

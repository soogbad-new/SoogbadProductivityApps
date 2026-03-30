package com.soogbad.sharedmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannedString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class ItemLayout extends ConstraintLayout {

    private ItemsManager<?, ?> itemsManager;
    private Item<?> item;
    private RichEditText contentEditText;
    private EditText titleEditText;
    private boolean itemDeleted = false;

    public ItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void init(ItemsManager<?, ?> itemsManager, Item<?> item, RichEditText contentEditText, EditText titleEditText) {
        this.itemsManager = itemsManager; this.item = item; this.contentEditText = contentEditText; this.titleEditText = titleEditText;
        titleEditText.setText(item.Title);
        itemsManager.loadItemContent(item);
        contentEditText.setText(item.Content);
    }

    public void save() {
        if(itemDeleted)
            return;
        itemsManager.saveItemTitle(item, titleEditText.getText().toString());
        itemsManager.saveItemContent(item, new SpannedString(contentEditText.getText()));
    }

    public void delete() {
        itemDeleted = true;
        itemsManager.deleteItem(item);
    }

    private boolean bold = false, italic = false, underline = false;
    public void onBoldButtonClick(View view) { bold = !bold; toggleButton(view, bold); contentEditText.toggleStyle(StyleSpan.class, Typeface.BOLD); }
    public void onItalicButtonClick(View view) { italic = !italic; toggleButton(view, italic); contentEditText.toggleStyle(StyleSpan.class, Typeface.ITALIC); }
    public void onUnderlineButtonClick(View view) { underline = !underline; toggleButton(view, underline); contentEditText.toggleStyle(UnderlineSpan.class, 0); }

    private void toggleButton(View button, boolean state) {
        if(state)
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.lightGreen));
        else
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
    }

}

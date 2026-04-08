package com.soogbad.sharedmodule;

import android.content.Context;
import android.text.SpannedString;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.HashSet;

public class ItemLayout extends ConstraintLayout implements RichEditText.StyleStateListener {

    private ItemsManager<?, ?> itemsManager;
    private Item<?> item;
    private RichEditText contentEditText;
    private EditText titleEditText;
    private boolean itemDeleted = false;
    private Button boldButton, italicButton, underlineButton, textSizeButton, textColorButton;

    public ItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void init(ItemsManager<?, ?> itemsManager, Item<?> item, RichEditText contentEditText, EditText titleEditText, Button boldButton, Button italicButton, Button underlineButton, Button textSizeButton, Button textColorButton) {
        this.itemsManager = itemsManager; this.item = item; this.contentEditText = contentEditText; this.titleEditText = titleEditText; this.boldButton = boldButton; this.italicButton = italicButton; this.underlineButton = underlineButton; this.textSizeButton = textSizeButton; this.textColorButton = textColorButton;
        contentEditText.setStyleStateListener(this);
        titleEditText.setText(item.Title);
        itemsManager.loadItemContent(item);
        contentEditText.setIgnoreTextChanges(true); contentEditText.setText(item.Content); contentEditText.setIgnoreTextChanges(false);
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

    public void onBoldButtonClick() { contentEditText.toggleStyle(RichTextStyle.BOLD); }
    public void onItalicButtonClick() { contentEditText.toggleStyle(RichTextStyle.ITALIC); }
    public void onUnderlineButtonClick() { contentEditText.toggleStyle(RichTextStyle.UNDERLINE); }
    public void onTextSizeSelected(RichTextStyle.TextSize size) { contentEditText.toggleStyle(RichTextStyle.TEXT_SIZE(size)); }
    public void onTextColorSelected(RichTextStyle.TextColor color) { contentEditText.toggleStyle(RichTextStyle.TEXT_COLOR(color)); }

    public void onTextSizeButtonClick() {
        new PopupMenu(getContext(), textSizeButton).show();
    }
    public void onTextColorButtonClick() {
        new PopupMenu(getContext(), textColorButton).show();
    }

    @Override
    public void onStyleStateChanged(HashSet<RichTextStyle<?>> activeStyles) {
        toggleButton(boldButton, activeStyles.contains(RichTextStyle.BOLD));
        toggleButton(italicButton, activeStyles.contains(RichTextStyle.ITALIC));
        toggleButton(underlineButton, activeStyles.contains(RichTextStyle.UNDERLINE));
    }

    private void toggleButton(Button button, boolean state) {
        if(state)
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.lightGreen));
        else
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
    }

}

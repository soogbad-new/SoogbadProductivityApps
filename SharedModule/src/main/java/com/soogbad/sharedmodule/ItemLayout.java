package com.soogbad.sharedmodule;

import android.content.Context;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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
    private HashSet<RichTextStyle<?>> activeStyles = new HashSet<>();

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

    @Override
    public void onStyleStateChanged(HashSet<RichTextStyle<?>> activeStyles) {
        this.activeStyles = activeStyles;
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

    public void onTextSizeButtonClick() {
        RichTextStyle.TextSize[] sizes = RichTextStyle.TextSize.values();
        int[] sizeValues = new int[sizes.length];
        for(int i = 0; i < sizes.length; i++) sizeValues[i] = sizes[i].size;
        int selectedSize = RichTextStyle.DEFAULT_TEXT_SIZE.size;
        for(RichTextStyle<?> style : activeStyles)
            if(style.spanClass == AbsoluteSizeSpan.class)
                selectedSize = style.value;
        showSelectionPopup(textSizeButton, sizeValues, selectedSize, i -> onTextSizeSelected(sizes[i]));
    }
    public void onTextColorButtonClick() {
        RichTextStyle.TextColor[] colors = RichTextStyle.TextColor.values();
        int[] colorValues = new int[colors.length];
        for(int i = 0; i < colors.length; i++) colorValues[i] = colors[i].color;
        int selectedColor = RichTextStyle.DEFAULT_TEXT_COLOR.color;
        for(RichTextStyle<?> style : activeStyles)
            if(style.spanClass == ForegroundColorSpan.class)
                selectedColor = style.value;
        showSelectionPopup(textColorButton, colorValues, selectedColor, i -> onTextColorSelected(colors[i]));
    }

    private void showSelectionPopup(Button popupAnchor, int[] options, int selectedOption, java.util.function.IntConsumer onSelect) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout popupLayout = (LinearLayout)inflater.inflate(R.layout.selection_popup, this, false);
        PopupWindow popup = new PopupWindow(popupLayout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popup.setOutsideTouchable(true);
        popup.setElevation(8);
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.WHITE));
        for(int i = 0; i < options.length; i++) {
            TextView textView = (TextView)inflater.inflate(R.layout.selection_popup_option, popupLayout, false);
            textView.setText(String.valueOf(options[i]));
            if(options[i] == selectedOption)
                textView.setBackgroundResource(R.drawable.selected_popup_option_border);
            final int index = i;
            textView.setOnClickListener(view -> { popup.dismiss(); onSelect.accept(index); });
            popupLayout.addView(textView);
        }
        popupLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        popup.showAsDropDown(popupAnchor, 0, -(popupAnchor.getHeight() + popupLayout.getMeasuredHeight()));
    }

}

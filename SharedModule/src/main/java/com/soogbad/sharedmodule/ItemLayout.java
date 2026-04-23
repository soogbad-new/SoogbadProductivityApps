package com.soogbad.sharedmodule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Layout;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.function.IntConsumer;

@SuppressLint("SetTextI18n")
@SuppressWarnings("FieldCanBeLocal")
public class ItemLayout extends ConstraintLayout implements RichEditText.StyleStateListener {

    private EditText titleEditText;
    private final RichEditText contentEditText;
    private final ConstraintLayout formattingToolbar;
    public ConstraintLayout getFormattingToolbar() { return formattingToolbar; }
    private final Button boldButton, italicButton, underlineButton, textSizeButton, textColorButton, bulletListButton, textAlignmentButton, hyperlinkButton;

    private ItemsManager<?, ?> itemsManager;
    private Item<?> item;
    private HashSet<RichCharacterStyle<?>> activeCharacterStyles = new HashSet<>();
    private HashSet<RichParagraphStyle<?>> activeParagraphStyles = new HashSet<>();
    private boolean itemDeleted = false;
    private boolean contentTouched = false;
    private ActionMode currentSelectionActionMode = null;

    public ItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_layout_content, this, true);
        contentEditText = findViewById(R.id.contentEditText); formattingToolbar = findViewById(R.id.formattingToolbar); boldButton = findViewById(R.id.boldButton); italicButton = findViewById(R.id.italicButton); underlineButton = findViewById(R.id.underlineButton); textSizeButton = findViewById(R.id.textSizeButton); textColorButton = findViewById(R.id.textColorButton); bulletListButton = findViewById(R.id.bulletListButton); textAlignmentButton = findViewById(R.id.textAlignmentButton); hyperlinkButton = findViewById(R.id.hyperlinkButton);
        boldButton.setOnClickListener(v -> onBoldButtonClick()); italicButton.setOnClickListener(v -> onItalicButtonClick()); underlineButton.setOnClickListener(v -> onUnderlineButtonClick()); textSizeButton.setOnClickListener(v -> onTextSizeButtonClick()); textColorButton.setOnClickListener(v -> onTextColorButtonClick()); bulletListButton.setOnClickListener(v -> onBulletListButtonClick()); textAlignmentButton.setOnClickListener(v -> onTextAlignmentButtonClick()); hyperlinkButton.setOnClickListener(v -> onHyperlinkButtonClick());
        contentEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { currentSelectionActionMode = actionMode; return true; }
            @Override public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override public void onDestroyActionMode(ActionMode actionMode) { currentSelectionActionMode = null; }
        });
    }

    public void init(ItemsManager<?, ?> itemsManager, Item<?> item, EditText titleEditText) {
        this.itemsManager = itemsManager; this.item = item; this.titleEditText = titleEditText;
        contentEditText.setStyleStateListener(this);
        titleEditText.setText(item.Title);
        itemsManager.loadItemContent(item);
        contentEditText.setIgnoreTextChanges(true); contentEditText.setText(item.Content); contentEditText.setIgnoreTextChanges(false);
        contentEditText.setOnFocusChangeListener((v, hasFocus) -> { if(hasFocus) contentTouched = true; });
    }

    public void save() {
        if(itemDeleted)
            return;
        String oldTitle = item.Title;
        item.Title = titleEditText.getText().toString();
        item.Content = new SpannedString(contentEditText.getText());
        if(!item.Title.equals(oldTitle))
            itemsManager.saveItemTitle(item);
        if(contentTouched)
            itemsManager.saveItemContent(item);
    }

    public void delete() {
        itemDeleted = true;
        itemsManager.deleteItem(item);
    }

    public void onBoldButtonClick() { contentEditText.toggleCharacterStyle(RichCharacterStyle.BOLD); }
    public void onItalicButtonClick() { contentEditText.toggleCharacterStyle(RichCharacterStyle.ITALIC); }
    public void onUnderlineButtonClick() { contentEditText.toggleCharacterStyle(RichCharacterStyle.UNDERLINE); }
    public void onTextSizeButtonClick() {
        RichCharacterStyle.TextSize[] sizes = RichCharacterStyle.TextSize.values();
        int[] sizeValues = new int[sizes.length];
        for(int i = 0; i < sizes.length; i++) sizeValues[i] = sizes[i].size;
        int selectedSize = RichCharacterStyle.DEFAULT_TEXT_SIZE.size;
        for(RichCharacterStyle<?> style : activeCharacterStyles)
            if(style.spanClass == AbsoluteSizeSpan.class)
                selectedSize = style.value;
        showSelectionPopup(textSizeButton, sizeValues, selectedSize, AbsoluteSizeSpan.class, i -> onTextSizeSelected(sizes[i]));
    }
    public void onTextSizeSelected(RichCharacterStyle.TextSize size) { contentEditText.toggleCharacterStyle(RichCharacterStyle.TEXT_SIZE(size)); }
    public void onTextColorButtonClick() {
        RichCharacterStyle.TextColor[] colors = RichCharacterStyle.TextColor.values();
        int[] colorValues = new int[colors.length];
        for(int i = 0; i < colors.length; i++) colorValues[i] = colors[i].color;
        int selectedColor = RichCharacterStyle.DEFAULT_TEXT_COLOR.color;
        for(RichCharacterStyle<?> style : activeCharacterStyles)
            if(style.spanClass == ForegroundColorSpan.class)
                selectedColor = style.value;
        showSelectionPopup(textColorButton, colorValues, selectedColor, ForegroundColorSpan.class, i -> onTextColorSelected(colors[i]));
    }
    public void onTextColorSelected(RichCharacterStyle.TextColor color) { contentEditText.toggleCharacterStyle(RichCharacterStyle.TEXT_COLOR(color)); }
    public void onBulletListButtonClick() { contentEditText.toggleParagraphStyle(RichParagraphStyle.BULLET); }
    public void onTextAlignmentButtonClick() {
        int[] alignmentValues = new int[]{ RichParagraphStyle.ALIGN_LEFT.value, RichParagraphStyle.ALIGN_CENTER.value, RichParagraphStyle.ALIGN_RIGHT.value };
        int selectedAlignment = RichParagraphStyle.DEFAULT_TEXT_ALIGNMENT.value;
        for(RichParagraphStyle<?> style : activeParagraphStyles)
            if(style.spanClass == AlignmentSpan.Standard.class)
                selectedAlignment = style.value;
        showSelectionPopup(textAlignmentButton, alignmentValues, selectedAlignment, AlignmentSpan.Standard.class, i -> onTextAlignmentSelected(Layout.Alignment.values()[alignmentValues[i]]));
    }
    public void onTextAlignmentSelected(Layout.Alignment alignment) { contentEditText.toggleParagraphStyle(RichParagraphStyle.TEXT_ALIGNMENT(alignment)); }
    public void onHyperlinkButtonClick() {
        int selectionStart = contentEditText.getSelectionStart(); int selectionEnd = contentEditText.getSelectionEnd();
        if(selectionStart == selectionEnd)
            showHyperlinkDialog(false, false);
        else {
            if(contentEditText.getText() == null) return;
            String selectedText = contentEditText.getText().subSequence(selectionStart, selectionEnd).toString();
            boolean selectionIsUrl = selectedText.startsWith("http://") || selectedText.startsWith("https://");
            showHyperlinkDialog(true, selectionIsUrl);
        }
    }

    @Override
    public void onStyleStateChanged(HashSet<RichCharacterStyle<?>> activeCharacterStyles, HashSet<RichParagraphStyle<?>> activeParagraphStyles) {
        this.activeCharacterStyles = activeCharacterStyles;
        this.activeParagraphStyles = activeParagraphStyles;
        boolean boldActive = false, italicActive = false, underlineActive = false;
        int activeTextSize = RichCharacterStyle.DEFAULT_TEXT_SIZE.size; int activeTextColor = RichCharacterStyle.DEFAULT_TEXT_COLOR.color;
        for(RichCharacterStyle<?> style : activeCharacterStyles) {
            if(style.equals(RichCharacterStyle.BOLD)) boldActive = true;
            else if(style.equals(RichCharacterStyle.ITALIC)) italicActive = true;
            else if(style.equals(RichCharacterStyle.UNDERLINE)) underlineActive = true;
            else if(style.spanClass == AbsoluteSizeSpan.class) activeTextSize = style.value;
            else if(style.spanClass == ForegroundColorSpan.class) activeTextColor = style.value;
        }
        toggleButton(boldButton, boldActive); toggleButton(italicButton, italicActive); toggleButton(underlineButton, underlineActive);
        textSizeButton.setText(String.valueOf(activeTextSize)); textColorButton.setTextColor(activeTextColor);
        boolean bulletListActive = false;
        int activeAlignment = RichParagraphStyle.DEFAULT_TEXT_ALIGNMENT.value;
        for(RichParagraphStyle<?> style : activeParagraphStyles) {
            if(style.equals(RichParagraphStyle.BULLET)) bulletListActive = true;
            else if(style.spanClass == AlignmentSpan.Standard.class) activeAlignment = style.value;
        }
        toggleButton(bulletListButton, bulletListActive);
        if(activeAlignment == RichParagraphStyle.ALIGN_AUTO.value) textAlignmentButton.setText("A");
        else if(activeAlignment == RichParagraphStyle.ALIGN_LEFT.value) textAlignmentButton.setText("L");
        else if(activeAlignment == RichParagraphStyle.ALIGN_CENTER.value) textAlignmentButton.setText("C");
        else if(activeAlignment == RichParagraphStyle.ALIGN_RIGHT.value) textAlignmentButton.setText("R");
    }

    private void toggleButton(Button button, boolean state) {
        if(state)
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.lightGreen));
        else
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
    }

    private void showSelectionPopup(Button popupAnchor, int[] options, int selectedOption, Class<?> styleType, IntConsumer onSelect) {
        if(currentSelectionActionMode != null)
            currentSelectionActionMode.hide(Long.MAX_VALUE);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout popupLayout = (LinearLayout)inflater.inflate(R.layout.selection_popup, this, false);
        PopupWindow popup = new PopupWindow(popupLayout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popup.setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        popup.setElevation(8);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        for(int i = 0; i < options.length; i++) {
            TextView textView = (TextView)inflater.inflate(styleType == ForegroundColorSpan.class ? R.layout.selection_popup_color_option : R.layout.selection_popup_option, popupLayout, false);
            adjustOptionTextView(textView, options[i], options[i] == selectedOption, styleType);
            final int index = i;
            textView.setOnClickListener(view -> { popup.dismiss(); onSelect.accept(index); });
            popupLayout.addView(textView);
        }
        popupLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        popup.showAsDropDown(popupAnchor, 0, -(popupAnchor.getHeight() + popupLayout.getMeasuredHeight()));
    }
    private void adjustOptionTextView(TextView textView, int value, boolean selected, Class<?> styleType) {
        if(styleType == AbsoluteSizeSpan.class) {
            textView.setText(String.valueOf(value));
            if(selected)
                textView.setBackgroundResource(R.drawable.selected_popup_option_border);
        }
        else if(styleType == ForegroundColorSpan.class) {
           textView.setBackgroundColor(value);
            if(selected) {
                Drawable colorDrawable = new ColorDrawable(value);
                Drawable borderDrawable = ContextCompat.getDrawable(getContext(), R.drawable.selected_popup_option_border);
                textView.setBackground(new LayerDrawable(new Drawable[]{colorDrawable, borderDrawable}));
            } 
        }
        else if(styleType == AlignmentSpan.Standard.class) {
            if(value == RichParagraphStyle.ALIGN_LEFT.value) textView.setText("Left");
            else if(value == RichParagraphStyle.ALIGN_CENTER.value) textView.setText("Center");
            else if(value == RichParagraphStyle.ALIGN_RIGHT.value) textView.setText("Right");
            if(selected)
                textView.setBackgroundResource(R.drawable.selected_popup_option_border);
        }
    }

    private void showHyperlinkDialog(boolean selection, boolean selectionIsUrl) {
        android.view.View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.hyperlink_dialog, null);
        EditText urlEditText = dialogView.findViewById(R.id.urlEditText); EditText textEditText = dialogView.findViewById(R.id.textEditText);
        if(selection) {
            if(selectionIsUrl) {
                dialogView.findViewById(R.id.urlLabel).setVisibility(GONE); urlEditText.setVisibility(GONE);
            }
            else {
                dialogView.findViewById(R.id.textLabel).setVisibility(GONE); textEditText.setVisibility(GONE);
                String existingUrl = contentEditText.getHyperlinkUrlAtSelection();
                    if(existingUrl != null) urlEditText.setText(existingUrl);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle("Hyperlink").setView(dialogView).setNeutralButton("Cancel", null);
        if(!selection)
            builder.setPositiveButton("Insert", (dialog, which) -> contentEditText.insertHyperlinkAtCursor(urlEditText.getText().toString().trim(), textEditText.getText().toString()));
        else {
            if(selectionIsUrl)
                builder.setPositiveButton("Apply", (dialog, which) -> contentEditText.replaceSelectedURLWithHyperlink(textEditText.getText().toString()));
            else
                builder.setPositiveButton("Apply", (dialog, which) -> contentEditText.createHyperlinkOnSelectedText(urlEditText.getText().toString().trim()));
        }
        builder.show();
    }

}

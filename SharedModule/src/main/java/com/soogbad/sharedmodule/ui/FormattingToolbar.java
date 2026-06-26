package com.soogbad.sharedmodule.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Layout;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.soogbad.sharedmodule.R;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.richtext.RichCharacterStyle;
import com.soogbad.sharedmodule.richtext.RichEditText;
import com.soogbad.sharedmodule.richtext.RichParagraphStyle;

import java.util.HashSet;
import java.util.function.IntConsumer;

@SuppressLint("SetTextI18n")
@SuppressWarnings("FieldCanBeLocal")
public class FormattingToolbar extends ConstraintLayout implements RichEditText.StyleStateListener {

    private RichEditText contentEditText;
    private final MaterialButton boldButton, italicButton, underlineButton, textSizeButton, textColorButton, bulletListButton, textAlignmentButton, hyperlinkButton, regionButton, undoButton, redoButton;

    private HashSet<RichCharacterStyle<?>> activeCharacterStyles = new HashSet<>();
    private HashSet<RichParagraphStyle<?>> activeParagraphStyles = new HashSet<>();
    private ActionMode currentSelectionActionMode = null;
    private boolean suppressNextPopup = false;

    public FormattingToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.formatting_toolbar_content, this, true);
        boldButton = findViewById(R.id.boldButton); italicButton = findViewById(R.id.italicButton); underlineButton = findViewById(R.id.underlineButton); textSizeButton = findViewById(R.id.textSizeButton); textColorButton = findViewById(R.id.textColorButton); bulletListButton = findViewById(R.id.bulletListButton); textAlignmentButton = findViewById(R.id.textAlignmentButton); hyperlinkButton = findViewById(R.id.hyperlinkButton); regionButton = findViewById(R.id.regionButton); undoButton = findViewById(R.id.undoButton); redoButton = findViewById(R.id.redoButton);
        boldButton.setOnClickListener(v -> onBoldButtonClick()); italicButton.setOnClickListener(v -> onItalicButtonClick()); underlineButton.setOnClickListener(v -> onUnderlineButtonClick()); textSizeButton.setOnClickListener(v -> onTextSizeButtonClick()); textColorButton.setOnClickListener(v -> onTextColorButtonClick()); bulletListButton.setOnClickListener(v -> onBulletListButtonClick()); textAlignmentButton.setOnClickListener(v -> onTextAlignmentButtonClick()); hyperlinkButton.setOnClickListener(v -> onHyperlinkButtonClick()); regionButton.setOnClickListener(v -> onRegionButtonClick()); undoButton.setOnClickListener(v -> onUndoButtonClick()); redoButton.setOnClickListener(v -> onRedoButtonClick());
    }

    public void init(RichEditText contentEditText) {
        this.contentEditText = contentEditText;
        contentEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, RichCharacterStyle.DEFAULT_TEXT_SIZE.size);
        contentEditText.setLineSpacing(0.0f, RichParagraphStyle.DEFAULT_LINE_SPACING_MULTIPLIER);
        contentEditText.setStyleStateListener(this);
        contentEditText.getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
            if(oldFocus == contentEditText && newFocus != contentEditText) {
                textSizeButton.setText(""); textSizeButton.setIconResource(R.drawable.text_size_unset);
                textColorButton.setIconTint(ColorStateList.valueOf(RichCharacterStyle.DEFAULT_TEXT_COLOR.color));
                textAlignmentButton.setIconResource(R.drawable.alignment_unset);
            }
        });
        contentEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { currentSelectionActionMode = actionMode; return true; }
            @Override public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override public void onDestroyActionMode(ActionMode actionMode) { currentSelectionActionMode = null; }
        });
    }

    private void onBoldButtonClick() { contentEditText.toggleCharacterStyle(RichCharacterStyle.BOLD); }
    private void onItalicButtonClick() { contentEditText.toggleCharacterStyle(RichCharacterStyle.ITALIC); }
    private void onUnderlineButtonClick() { contentEditText.toggleCharacterStyle(RichCharacterStyle.UNDERLINE); }
    private void onTextSizeButtonClick() {
        RichCharacterStyle.TextSize[] sizes = RichCharacterStyle.TextSize.values();
        int[] sizeValues = new int[sizes.length];
        for(int i = 0; i < sizes.length; i++) sizeValues[i] = sizes[i].size;
        int selectedSize = RichCharacterStyle.DEFAULT_TEXT_SIZE.size;
        for(RichCharacterStyle<?> style : activeCharacterStyles)
            if(style.spanClass == AbsoluteSizeSpan.class)
                selectedSize = style.value;
        showSelectionPopup(textSizeButton, sizeValues, selectedSize, AbsoluteSizeSpan.class, i -> onTextSizeSelected(sizes[i]));
    }
    private void onTextSizeSelected(RichCharacterStyle.TextSize size) { contentEditText.toggleCharacterStyle(RichCharacterStyle.TEXT_SIZE(size)); }
    private void onTextColorButtonClick() {
        RichCharacterStyle.TextColor[] colors = RichCharacterStyle.TextColor.values();
        int[] colorValues = new int[colors.length];
        for(int i = 0; i < colors.length; i++) colorValues[i] = colors[i].color;
        int selectedColor = RichCharacterStyle.DEFAULT_TEXT_COLOR.color;
        for(RichCharacterStyle<?> style : activeCharacterStyles)
            if(style.spanClass == ForegroundColorSpan.class)
                selectedColor = style.value;
        showSelectionPopup(textColorButton, colorValues, selectedColor, ForegroundColorSpan.class, i -> onTextColorSelected(colors[i]));
    }
    private void onTextColorSelected(RichCharacterStyle.TextColor color) { contentEditText.toggleCharacterStyle(RichCharacterStyle.TEXT_COLOR(color)); }
    private void onBulletListButtonClick() { contentEditText.toggleParagraphStyle(RichParagraphStyle.BULLET); }
    private void onTextAlignmentButtonClick() {
        int[] alignmentValues = new int[]{ RichParagraphStyle.ALIGN_LEFT.value, RichParagraphStyle.ALIGN_CENTER.value, RichParagraphStyle.ALIGN_RIGHT.value };
        int selectedAlignment = RichParagraphStyle.DEFAULT_TEXT_ALIGNMENT.value;
        for(RichParagraphStyle<?> style : activeParagraphStyles)
            if(style.spanClass == AlignmentSpan.Standard.class)
                selectedAlignment = style.value;
        showSelectionPopup(textAlignmentButton, alignmentValues, selectedAlignment, AlignmentSpan.Standard.class, i -> onTextAlignmentSelected(Layout.Alignment.values()[alignmentValues[i]]));
    }
    private void onTextAlignmentSelected(Layout.Alignment alignment) { contentEditText.toggleParagraphStyle(RichParagraphStyle.TEXT_ALIGNMENT(alignment)); }
    private void onHyperlinkButtonClick() {
        int selectionStart = contentEditText.getSelectionStart(); int selectionEnd = contentEditText.getSelectionEnd();
        if(selectionStart == selectionEnd)
            showHyperlinkDialog(false, false);
        else {
            if(contentEditText.getText() == null) return;
            String selectedText = contentEditText.getText().subSequence(selectionStart, selectionEnd).toString();
            showHyperlinkDialog(true, Utility.isLinkUrlValid(selectedText));
        }
    }
    private void onRegionButtonClick() { contentEditText.toggleCollapsibleRegion(); }
    private void onUndoButtonClick() { contentEditText.onTextContextMenuItem(android.R.id.undo); }
    private void onRedoButtonClick() { contentEditText.onTextContextMenuItem(android.R.id.redo); }

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
        textSizeButton.setIcon(null); textSizeButton.setText(String.valueOf(activeTextSize));
        textColorButton.setIconTint(ColorStateList.valueOf(activeTextColor));
        boolean bulletListActive = false;
        int activeAlignment = RichParagraphStyle.DEFAULT_TEXT_ALIGNMENT.value;
        for(RichParagraphStyle<?> style : activeParagraphStyles) {
            if(style.equals(RichParagraphStyle.BULLET)) bulletListActive = true;
            else if(style.spanClass == AlignmentSpan.Standard.class) activeAlignment = style.value;
        }
        toggleButton(bulletListButton, bulletListActive);
        if(activeAlignment == RichParagraphStyle.ALIGN_LEFT.value) textAlignmentButton.setIconResource(R.drawable.alignment_left);
        else if(activeAlignment == RichParagraphStyle.ALIGN_CENTER.value) textAlignmentButton.setIconResource(R.drawable.alignment_center);
        else if(activeAlignment == RichParagraphStyle.ALIGN_RIGHT.value) textAlignmentButton.setIconResource(R.drawable.alignment_right);
    }

    private void toggleButton(MaterialButton button, boolean state) {
        if(state)
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.lightGreen));
        else
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showSelectionPopup(MaterialButton popupAnchor, int[] options, int selectedOption, Class<?> styleType, IntConsumer onSelect) {
        if(suppressNextPopup) {
            suppressNextPopup = false;
            return;
        }
        if(currentSelectionActionMode != null)
            currentSelectionActionMode.hide(Long.MAX_VALUE);
        Context themedContext = new ContextThemeWrapper(getContext(), R.style.AppTheme_PopupOverlay);
        LayoutInflater inflater = LayoutInflater.from(themedContext);
        LinearLayout popupLayout = (LinearLayout)inflater.inflate(R.layout.formatting_toolbar_popup, this, false);
        PopupWindow popup = new PopupWindow(popupLayout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popup.setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        popup.setElevation(8);
        popup.setOutsideTouchable(true);
        TypedValue typedValue = new TypedValue();
        themedContext.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        popup.setBackgroundDrawable(new ColorDrawable(typedValue.data));
        popup.setTouchInterceptor((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE && Utility.isMotionEventInsideView(motionEvent, popupAnchor))
                suppressNextPopup = true;
            return false;
        });
        for(int i = 0; i < options.length; i++) {
            TextView textView = (TextView)inflater.inflate(styleType == ForegroundColorSpan.class ? R.layout.formatting_toolbar_popup_option_color : R.layout.formatting_toolbar_popup_option, popupLayout, false);
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
            builder.setPositiveButton("Insert", null);
        else
            builder.setPositiveButton("Apply", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        if(!selection) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String url = urlEditText.getText().toString().trim();
                if(Utility.isLinkUrlValid(url)) {
                    contentEditText.insertHyperlinkAtCursor(url, textEditText.getText().toString());
                    dialog.dismiss();
                }
                else
                    urlEditText.setError("URL is not valid");
            });
        }
        else {
            if(selectionIsUrl) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view-> {
                    String displayText = textEditText.getText().toString();
                    if(!displayText.isEmpty()) {
                        contentEditText.replaceSelectedURLWithHyperlink(displayText);
                        dialog.dismiss();
                    }
                    else
                        textEditText.setError("Display text is required");
                });
            }
            else {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                    String url = urlEditText.getText().toString().trim();
                    if(Utility.isLinkUrlValid(url)) {
                        contentEditText.createHyperlinkOnSelectedText(url);
                        dialog.dismiss();
                    }
                    else
                        urlEditText.setError("URL is not valid");
                });
            }
        }
    }

}

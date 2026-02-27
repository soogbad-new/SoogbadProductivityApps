package com.soogbad.commonmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseItemLayout extends ConstraintLayout {

    protected EditText editText;
    public void setEditText(EditText editText) { this.editText = editText; editText.addTextChangedListener(textWatcher); }

    protected int currentCursorStyles = 0;

    protected BaseItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void setExampleText() {
        Spannable text = new SpannableString("Hello World");
        text.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(text);
    }

    protected void applyStyle(int style) {
        if(editText.getSelectionStart() != editText.getSelectionEnd()) {
            applyStyleToSelection(style);
            currentCursorStyles = 0;
        }
        else {
            currentCursorStyles ^= style;
            applyStyleToCursor(style);
        }
    }

    protected void applyStyleToCursor(int style) {
        int cursorPosition = editText.getSelectionEnd();
        int newStyles = removeAndRetrieveCurrentStyles(cursorPosition, cursorPosition, true);
        newStyles ^= style;
        if(newStyles != 0)
            editText.getText().setSpan(new StyleSpan(newStyles), cursorPosition, cursorPosition, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    protected void applyStyleToSelection(int style) {
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        int newStyles = removeAndRetrieveCurrentStyles(selectionStart, selectionEnd, false);
        newStyles ^= style;
        if(newStyles != 0)
            editText.getText().setSpan(new StyleSpan(newStyles), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    private int removeAndRetrieveCurrentStyles(int start, int end, boolean onlyZeroLengthSpans) {
        StyleSpan[] currentStyles = editText.getText().getSpans(start, end, StyleSpan.class);
        int ret = 0;
        for(StyleSpan styleSpan : currentStyles)
            if(!onlyZeroLengthSpans || editText.getText().getSpanStart(styleSpan) == editText.getText().getSpanEnd(styleSpan)) {
                ret |= styleSpan.getStyle();
                editText.getText().removeSpan(styleSpan);
            }
        return ret;
    }
    private final TextWatcher textWatcher = new TextWatcher() {
        int insertStart, insertCount;
        @Override
        public void beforeTextChanged(CharSequence seq, int start, int count, int after) { insertStart = start; insertCount = after; }
        @Override
        public void onTextChanged(CharSequence seq, int start, int before, int count) { insertStart = start; insertCount = count; }
        @Override
        public void afterTextChanged(Editable editable) {
            if(insertCount <= 0)
                return;
            StyleSpan[] currentStyles = editable.getSpans(insertStart, insertStart, StyleSpan.class);
             StyleSpan existingMatchingSpan = null;
             for(StyleSpan styleSpan : currentStyles) {
                 if(styleSpan.getStyle() == currentCursorStyles && editable.getSpanEnd(styleSpan) == insertStart) {
                     editable.removeSpan(styleSpan);
                     existingMatchingSpan = styleSpan;
                 }
            }
            if(currentCursorStyles != 0)
                editable.setSpan(new StyleSpan(currentCursorStyles), existingMatchingSpan == null ? insertStart : editable.getSpanStart(existingMatchingSpan), insertStart + insertCount, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
    };

    public void onBoldButtonClick() { applyStyle(Typeface.BOLD); }
    public void onItalicButtonClick() { applyStyle(Typeface.ITALIC); }

}

package com.soogbad.commonmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseItemLayout extends ConstraintLayout {

    protected EditText editText;
    public void setEditText(EditText editText) { this.editText = editText; }

    protected BaseItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void setExampleText() {
        Spannable text = new SpannableString("Hello World");
        text.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(text);
    }

    protected void applyStyle(int style) {
        if(editText.getSelectionStart() != editText.getSelectionEnd())
            applyStyleToSelection(style);
        else
            applyStyleToCursor(style);
    }
    protected void applyStyleToCursor(int style) {
        int cursorPosition = editText.getSelectionEnd();
        int newStyles = removeAndRetrieveCurrentStyles(cursorPosition, cursorPosition);
        newStyles ^= style;
        if(newStyles != 0)
            editText.getText().setSpan(new StyleSpan(newStyles), cursorPosition, cursorPosition, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }
    protected void applyStyleToSelection(int style) {
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        int newStyles = removeAndRetrieveCurrentStyles(selectionStart, selectionEnd);
        newStyles ^= style;
        if(newStyles != 0)
            editText.getText().setSpan(new StyleSpan(newStyles), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    private int removeAndRetrieveCurrentStyles(int start, int end) {
        StyleSpan[] currentStyles = editText.getText().getSpans(start, end, StyleSpan.class);
        int ret = 0;
        for(StyleSpan styleSpan : currentStyles) {
            ret |= styleSpan.getStyle();
            editText.getText().removeSpan(styleSpan);
        }
        return ret;
    }

    public void onBoldButtonClick() { applyStyle(Typeface.BOLD); }
    public void onItalicButtonClick() { applyStyle(Typeface.ITALIC); }

}

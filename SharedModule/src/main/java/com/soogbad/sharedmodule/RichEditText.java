package com.soogbad.sharedmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

@SuppressWarnings("RedundantIfStatement")
public class RichEditText extends AppCompatEditText {

    private boolean ignoreTextChanges = false;
    public void setIgnoreTextChanges(boolean ignoreTextChanges) { this.ignoreTextChanges = ignoreTextChanges; }
    private boolean bold, italic, underline;
    private boolean textChanging;
    private int changeStart, changeCount;

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(textChangedListener);
        setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus) {
                bold = false; italic = false; underline = false;
                notifyListener();
            }
        });
    }

    @Override
    protected void onSelectionChanged(int selectionStart, int selectionEnd) {
        super.onSelectionChanged(selectionStart, selectionEnd);
        if(textChanging) {
            textChanging = false;
            return;
        }
        if(!hasFocus())
            return;
        updateCurrentActiveStyles(selectionStart, selectionEnd);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final TextWatcher textChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(ignoreTextChanges) return;
            changeStart = start; changeCount = count;
        }
        @Override
        public void afterTextChanged(Editable editable) {
            if(ignoreTextChanges) return;
            if(changeCount == 1)
                applyActiveStyles(editable, changeStart);
            textChanging = true;
        }
    };
    private void applyActiveStyles(Editable editable, int position) {
        applyActiveStyle(editable, position, StyleSpan.class, Typeface.BOLD, bold);
        applyActiveStyle(editable, position, StyleSpan.class, Typeface.ITALIC, italic);
        applyActiveStyle(editable, position, UnderlineSpan.class, 0, underline);
    }
    private static <T extends CharacterStyle> void applyActiveStyle(Editable editable, int position, Class<T> spanType, int value, boolean isActive) {
        if(isActive)
            editable.setSpan(instantiateSpan(spanType, value), position, position + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        else
            removeSpansInRange(editable, position, position + 1, spanType, value);
    }

    public <T extends CharacterStyle> void toggleStyle(Class<T> spanType, int value) {
        Editable editable = getText();
        if(editable == null) return;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        if(selectionStart != selectionEnd) {
            applyStyleToSelection(editable, selectionStart, selectionEnd, spanType, value);
            updateCurrentActiveStyles(selectionStart, selectionEnd);
        }
        else {
            toggleActiveStyleFlag(spanType, value);
            notifyListener();
        }
    }
    private static <T extends CharacterStyle> void applyStyleToSelection(Editable editable, int selectionStart, int selectionEnd, Class<T> spanType, int value) {
        boolean wasCovered = isEntireRangeCovered(editable, selectionStart, selectionEnd, spanType, value);
        removeSpansInRange(editable, selectionStart, selectionEnd, spanType, value);
        if(!wasCovered)
            editable.setSpan(instantiateSpan(spanType, value), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    private void toggleActiveStyleFlag(Class<? extends CharacterStyle> spanType, int value) {
        if(spanType.equals(StyleSpan.class)) {
            if(value == Typeface.BOLD) bold = !bold;
            else if(value == Typeface.ITALIC) italic = !italic;
        }
        else if(spanType.equals(UnderlineSpan.class))
            underline = !underline;
    }

    private void updateCurrentActiveStyles(int selectionStart, int selectionEnd) {
        Editable editable = getText();
        if(editable == null) return;
        if(selectionStart != selectionEnd) {
            bold = isEntireRangeCovered(editable, selectionStart, selectionEnd, StyleSpan.class, Typeface.BOLD);
            italic = isEntireRangeCovered(editable, selectionStart, selectionEnd, StyleSpan.class, Typeface.ITALIC);
            underline = isEntireRangeCovered(editable, selectionStart, selectionEnd, UnderlineSpan.class, 0);
        }
        else {
            bold = isStyleActiveAtCursor(editable, selectionStart, StyleSpan.class, Typeface.BOLD);
            italic = isStyleActiveAtCursor(editable, selectionStart, StyleSpan.class, Typeface.ITALIC);
            underline = isStyleActiveAtCursor(editable, selectionStart, UnderlineSpan.class, 0);
        }
        notifyListener();
    }

    private static <T extends CharacterStyle> void removeSpansInRange(Editable editable, int start, int end, Class<T> spanType, int value) {
        T[] spans = editable.getSpans(start, end, spanType);
        for(T span : spans) {
            if(!compareSpansValue(span, value))
                continue;
            int spanStart = editable.getSpanStart(span); int spanEnd = editable.getSpanEnd(span);
            editable.removeSpan(span);
            if(spanStart < start)
                editable.setSpan(instantiateSpan(spanType, value), spanStart, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(spanEnd > end)
                editable.setSpan(instantiateSpan(spanType, value), end, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static <T extends CharacterStyle> boolean isStyleActiveAtCursor(Editable editable, int cursor, Class<T> spanType, int value) {
        if(editable.toString().isEmpty()) return false;
        if(cursor > 0 && editable.charAt(cursor - 1) != '\n')
            return hasStyleAt(editable, cursor - 1, spanType, value);
        else if(cursor < editable.length())
            return hasStyleAt(editable, cursor, spanType, value);
        return false;
    }
    private static <T extends CharacterStyle> boolean hasStyleAt(Editable editable, int position, Class<T> spanType, int value) {
        T[] spans = editable.getSpans(position, position + 1, spanType);
        for(T span : spans) {
            if(compareSpansValue(span, value)) {
                int spanStart = editable.getSpanStart(span); int spanEnd = editable.getSpanEnd(span);
                if(spanStart <= position && spanEnd > position)
                    return true;
            }
        }
        return false;
    }
    private static <T extends CharacterStyle> boolean isEntireRangeCovered(Editable editable, int start, int end, Class<T> spanType, int value) {
        if(start >= end) return false;
        for(int i = start; i < end; i++)
            if(!hasStyleAt(editable, i, spanType, value))
                return false;
        return true;
    }

    public static <T extends CharacterStyle> T instantiateSpan(Class<T> spanType, int value) {
        if(spanType.equals(StyleSpan.class)) return spanType.cast(new StyleSpan(value));
        else if(spanType.equals(UnderlineSpan.class)) return spanType.cast(new UnderlineSpan());
        return null;
    }
    public static <T extends CharacterStyle> boolean compareSpansValue(T span, int value) {
        if(span == null) return false;
        if(span instanceof StyleSpan) return ((StyleSpan)span).getStyle() == value;
        else if(span instanceof UnderlineSpan) return true;
        return false;
    }

    public interface StyleStateListener {
        void onStyleStateChanged(boolean bold, boolean italic, boolean underline);
    }

    private StyleStateListener styleStateListener;
    public void setStyleStateListener(StyleStateListener listener) { this.styleStateListener = listener; }

    private void notifyListener() {
        if(styleStateListener != null)
            styleStateListener.onStyleStateChanged(bold, italic, underline);
    }

}

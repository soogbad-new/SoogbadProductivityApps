package com.soogbad.sharedmodule;

import android.content.Context;
import android.text.Editable;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class RichEditText extends AppCompatEditText {

    public RichEditText(Context context, AttributeSet attrs) { super(context, attrs); }

    public <T extends CharacterStyle> void toggleStyle(Class<T> spanType, int val) {
        Editable editable = getText();
        if(editable == null) return;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        T[] currentSpans = editable.getSpans(selectionStart, selectionEnd, spanType);
        if(selectionStart != selectionEnd) {
            int min = selectionEnd, max = selectionStart;
            for(T span : currentSpans) {
                if(compareSpansValue(span, val)) {
                    int spanStart = editable.getSpanStart(span); int spanEnd = editable.getSpanEnd(span);
                    editable.removeSpan(span); // remove the current spans of the same type and value
                    if(spanStart < selectionStart) // if it stretches beyond the selection, create a new one just for that part
                        editable.setSpan(instantiateSpan(spanType, val), spanStart, selectionStart, Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(spanEnd > selectionEnd) // if it stretches beyond the selection, create a new one just for that part
                        editable.setSpan(instantiateSpan(spanType, val), selectionEnd, spanEnd, Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(spanStart < min)
                        min = spanStart;
                    if(spanEnd > max)
                        max = spanEnd;
                }
            }
            if(!(min <= selectionStart && max >= selectionEnd)) // if the entire selection isn't covered by spans of the same type and value, then create a span that does (toggling the style on for the entire selection). however, if the entire selection was already covered, then the style needs to be turned off, and as the current spans were already removed, nothing needs to be done.
                editable.setSpan(instantiateSpan(spanType, val), selectionStart, selectionEnd, Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    private <T extends CharacterStyle> boolean compareSpansValue(T span, int val) {
        if(span == null) return false;
        if(span instanceof StyleSpan) return ((StyleSpan)span).getStyle() == val;
        return false;
    }
    private <T extends CharacterStyle> T instantiateSpan(Class<T> spanType, int val) {
        if(spanType.equals(StyleSpan.class)) return spanType.cast(new StyleSpan(val));
        return null;
    }

}

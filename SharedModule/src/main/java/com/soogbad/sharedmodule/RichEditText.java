package com.soogbad.sharedmodule;

import android.content.Context;
import android.text.Editable;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

@SuppressWarnings("RedundantIfStatement")
public class RichEditText extends AppCompatEditText {

    public RichEditText(Context context, AttributeSet attrs) { super(context, attrs); }

    public <T extends CharacterStyle> void toggleStyle(Class<T> spanType, int val) {
        Editable editable = getText();
        if(editable == null) return;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        T[] currentSpans = editable.getSpans(selectionStart, selectionEnd, spanType);
        if(selectionStart != selectionEnd) {

        }
        else {

        }
    }

    private <T extends CharacterStyle> T instantiateSpan(Class<T> spanType, int val) {
        if(spanType.equals(StyleSpan.class)) return spanType.cast(new StyleSpan(val));
        else if(spanType.equals(UnderlineSpan.class)) return spanType.cast(new UnderlineSpan());
        return null;
    }
    private <T extends CharacterStyle> boolean compareSpansValue(T span, int val) {
        if(span == null) return false;
        if(span instanceof StyleSpan) return ((StyleSpan)span).getStyle() == val;
        else if(span instanceof UnderlineSpan) return true;
        return false;
    }

}

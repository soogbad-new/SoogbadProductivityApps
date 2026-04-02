package com.soogbad.sharedmodule;

import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

public enum RichTextStyle {

    BOLD(StyleSpan.class, Typeface.BOLD),
    ITALIC(StyleSpan.class, Typeface.ITALIC),
    UNDERLINE(UnderlineSpan.class, 0);

    RichTextStyle(Class<? extends CharacterStyle> spanClass, int value) {
        this.spanClass = spanClass; this.value = value;
    }

    public final Class<? extends CharacterStyle> spanClass;
    public final int value;

    public CharacterStyle createSpan() {
        if(this == BOLD) return new StyleSpan(Typeface.BOLD);
        else if(this == ITALIC) return new StyleSpan(Typeface.ITALIC);
        else if(this == UNDERLINE) return new UnderlineSpan();
        return null;
    }

    public boolean matchesSpan(CharacterStyle span) {
        if(this == BOLD) return span instanceof StyleSpan && ((StyleSpan)span).getStyle() == Typeface.BOLD;
        else if(this == ITALIC) return span instanceof StyleSpan && ((StyleSpan)span).getStyle() == Typeface.ITALIC;
        else if(this == UNDERLINE) return span instanceof UnderlineSpan;
        return false;
    }

}

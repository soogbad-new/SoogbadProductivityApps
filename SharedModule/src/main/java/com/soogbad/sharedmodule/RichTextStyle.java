package com.soogbad.sharedmodule;

import android.graphics.Typeface;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

public class RichTextStyle<T extends CharacterStyle> {

    RichTextStyle(Class<T> spanClass, int value) {
        this.spanClass = spanClass; this.value = value;
    }

    public final Class<T> spanClass;
    public final int value;

    // TODO: this can be simplified using T
    public CharacterStyle createSpan() {
        if(spanClass == StyleSpan.class) return new StyleSpan(value);
        else if(spanClass == UnderlineSpan.class) return new UnderlineSpan();
        else if(spanClass == AbsoluteSizeSpan.class) return new AbsoluteSizeSpan(value);
        else if(spanClass == ForegroundColorSpan.class) return new ForegroundColorSpan(value);
        return null;
    }

    // TODO: this can be simplified using method reference
    public boolean matchesSpan(CharacterStyle span) {
        if(span instanceof StyleSpan) return ((StyleSpan)span).getStyle() == value;
        else if(span instanceof UnderlineSpan) return true;
        else if(span instanceof AbsoluteSizeSpan) return ((AbsoluteSizeSpan)span).getSize() == value;
        else if(span instanceof ForegroundColorSpan) return ((ForegroundColorSpan)span).getForegroundColor() == value;
        return false;
    }

    public final static RichTextStyle<StyleSpan> BOLD = new RichTextStyle<>(StyleSpan.class, Typeface.BOLD);
    public final static RichTextStyle<StyleSpan> ITALIC = new RichTextStyle<>(StyleSpan.class, Typeface.ITALIC);
    public final static RichTextStyle<UnderlineSpan> UNDERLINE = new RichTextStyle<>(UnderlineSpan.class, 0);

}

package com.soogbad.sharedmodule;

import android.graphics.Typeface;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class RichTextStyle<T extends CharacterStyle> {

    private RichTextStyle(Class<T> spanClass, int value) {
        this.spanClass = spanClass; this.value = value;
    }

    public final Class<T> spanClass;
    public final int value;

    public CharacterStyle createSpan() {
        if(spanClass == StyleSpan.class) return new StyleSpan(value);
        else if(spanClass == UnderlineSpan.class) return new UnderlineSpan();
        else if(spanClass == AbsoluteSizeSpan.class) return new AbsoluteSizeSpan(value);
        else if(spanClass == ForegroundColorSpan.class) return new ForegroundColorSpan(value);
        return null;
    }

    public boolean matchesSpan(CharacterStyle span) {
        if(span instanceof StyleSpan) return ((StyleSpan)span).getStyle() == value;
        else if(span instanceof UnderlineSpan) return true;
        else if(span instanceof AbsoluteSizeSpan) return ((AbsoluteSizeSpan)span).getSize() == value;
        else if(span instanceof ForegroundColorSpan) return ((ForegroundColorSpan)span).getForegroundColor() == value;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        RichTextStyle<?> that = (RichTextStyle<?>)o;
        return value == that.value && spanClass.equals(that.spanClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spanClass, value);
    }

    public static RichTextStyle<?>[] values() {
        ArrayList<RichTextStyle<?>> list = new ArrayList<>(Arrays.asList(BOLD, ITALIC, UNDERLINE));
        for(TextSize size : TextSize.values()) list.add(TEXT_SIZE(size));
        for(TextColor color : TextColor.values()) list.add(TEXT_COLOR(color));
        return list.toArray(new RichTextStyle<?>[0]);
    }

    public static final RichTextStyle<StyleSpan> BOLD = new RichTextStyle<>(StyleSpan.class, Typeface.BOLD);
    public static final RichTextStyle<StyleSpan> ITALIC = new RichTextStyle<>(StyleSpan.class, Typeface.ITALIC);
    public static final RichTextStyle<UnderlineSpan> UNDERLINE = new RichTextStyle<>(UnderlineSpan.class, 0);
    public static RichTextStyle<AbsoluteSizeSpan> TEXT_SIZE(TextSize size) { return new RichTextStyle<>(AbsoluteSizeSpan.class, size.size); }
    public static RichTextStyle<ForegroundColorSpan> TEXT_COLOR(TextColor color) { return new RichTextStyle<>(ForegroundColorSpan.class, color.color); }

    public enum TextSize {
        SIZE_12(12), SIZE_14(14), SIZE_16(16), SIZE_18(18), SIZE_20(20), SIZE_24(24), SIZE_28(28), SIZE_32(32);

        TextSize(int size) { this.size = size; }
        public final int size;
    }

    public enum TextColor {
        BLACK(0xFF000000), RED(0xFFFF0000), GREEN(0xFF00FF00), BLUE(0xFF0000FF), YELLOW(0xFFFFFF00), CYAN(0xFF00FFFF), MAGENTA(0xFFFF00FF);

        TextColor(int color) { this.color = color; }
        public final int color;
    }

}

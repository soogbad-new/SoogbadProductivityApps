package com.soogbad.sharedmodule;

import android.graphics.Typeface;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class RichCharacterStyle<T extends CharacterStyle> {

    private RichCharacterStyle(Class<T> spanClass, int value) {
        this.spanClass = spanClass; this.value = value;
    }

    public final Class<T> spanClass;
    public final int value;

    public boolean isFlagStyle() {
        return spanClass == StyleSpan.class || spanClass == UnderlineSpan.class;
    }

    public CharacterStyle createSpan() {
        if(spanClass == StyleSpan.class) return new StyleSpan(value);
        else if(spanClass == UnderlineSpan.class) return new UnderlineSpan();
        else if(spanClass == AbsoluteSizeSpan.class) return new AbsoluteSizeSpan(value, true);
        else if(spanClass == ForegroundColorSpan.class) return new ForegroundColorSpan(value);
        return null;
    }
    public static CharacterStyle cloneSpan(CharacterStyle span) {
        if(span instanceof StyleSpan) return new StyleSpan(((StyleSpan)span).getStyle());
        else if(span instanceof UnderlineSpan) return new UnderlineSpan();
        else if(span instanceof AbsoluteSizeSpan) return new AbsoluteSizeSpan(((AbsoluteSizeSpan)span).getSize(), true);
        else if(span instanceof ForegroundColorSpan) return new ForegroundColorSpan(((ForegroundColorSpan)span).getForegroundColor());
        return null;
    }
    public boolean matchesSpanValue(CharacterStyle span) {
        if(span instanceof StyleSpan) return ((StyleSpan)span).getStyle() == value;
        else if(span instanceof UnderlineSpan) return true;
        else if(span instanceof AbsoluteSizeSpan) return ((AbsoluteSizeSpan)span).getSize() == value;
        else if(span instanceof ForegroundColorSpan) return ((ForegroundColorSpan)span).getForegroundColor() == value;
        return false;
    }

    @Override public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        RichCharacterStyle<?> that = (RichCharacterStyle<?>)obj;
        return value == that.value && spanClass == that.spanClass;
    }
    @Override public int hashCode() {
        return Objects.hash(spanClass, value);
    }

    public static RichCharacterStyle<?>[] values() {
        ArrayList<RichCharacterStyle<?>> list = new ArrayList<>(Arrays.asList(BOLD, ITALIC, UNDERLINE));
        for(TextSize size : TextSize.values()) list.add(TEXT_SIZE(size));
        for(TextColor color : TextColor.values()) list.add(TEXT_COLOR(color));
        return list.toArray(new RichCharacterStyle<?>[0]);
    }
    
    public static final RichCharacterStyle<StyleSpan> BOLD = new RichCharacterStyle<>(StyleSpan.class, Typeface.BOLD);
    public static final RichCharacterStyle<StyleSpan> ITALIC = new RichCharacterStyle<>(StyleSpan.class, Typeface.ITALIC);
    public static final RichCharacterStyle<UnderlineSpan> UNDERLINE = new RichCharacterStyle<>(UnderlineSpan.class, 0);

    public static RichCharacterStyle<AbsoluteSizeSpan> TEXT_SIZE(TextSize size) { return new RichCharacterStyle<>(AbsoluteSizeSpan.class, size.size); }
    public static RichCharacterStyle<ForegroundColorSpan> TEXT_COLOR(TextColor color) { return new RichCharacterStyle<>(ForegroundColorSpan.class, color.color); }

    public static TextSize DEFAULT_TEXT_SIZE = TextSize.SIZE_20;
    public static final TextColor DEFAULT_TEXT_COLOR = TextColor.WHITE;

    public enum TextSize {
        SIZE_12(12), SIZE_14(14), SIZE_16(16), SIZE_18(18), SIZE_20(20), SIZE_22(22), SIZE_24(24), SIZE_26(26), SIZE_28(28);
        TextSize(int size) { this.size = size; }
        public final int size;
    }
    public enum TextColor {
        WHITE(0xFFFFFFFF), RED(0xFFFF0000), GREEN(0xFF00FF00), BLUE(0xFF1F1FFF), CYAN(0xFF00FFFF), YELLOW(0xFFFFFF00), ORANGE(0xFFFFA500), MAGENTA(0xFFFF00FF);
        TextColor(int color) { this.color = color; }
        public final int color;
    }

}

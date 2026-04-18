package com.soogbad.sharedmodule;

import android.text.style.BulletSpan;
import android.text.style.ParagraphStyle;

import androidx.annotation.Nullable;

import java.util.Objects;

public class RichParagraphStyle<T extends ParagraphStyle> {

    private RichParagraphStyle(Class<T> spanClass, int value) {
        this.spanClass = spanClass; this.value = value;
    }

    public final Class<T> spanClass;
    public final int value;

    public boolean isFlagStyle() {
        return spanClass == BulletSpan.class;
    }

    public ParagraphStyle createSpan() {
        if(spanClass == BulletSpan.class) return new BulletSpan(BULLET_GAP_WIDTH, BULLET_COLOR);
        return null;
    }
    public static ParagraphStyle cloneSpan(ParagraphStyle span) {
        if(span instanceof BulletSpan) return new BulletSpan(BULLET_GAP_WIDTH, BULLET_COLOR);
        return null;
    }
    public boolean matchesSpanValue(ParagraphStyle span) {
        if(span instanceof BulletSpan) return true;
        return false;
    }

    @Override public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        RichParagraphStyle<?> that = (RichParagraphStyle<?>)obj;
        return value == that.value && spanClass == that.spanClass;
    }
    @Override public int hashCode() { return Objects.hash(spanClass, value); }

    public static RichParagraphStyle<?>[] values() {
        return new RichParagraphStyle<?>[] { BULLET };
    }
    public static final RichParagraphStyle<BulletSpan> BULLET = new RichParagraphStyle<>(BulletSpan.class, 0);

    private static final int BULLET_GAP_WIDTH = 20;
    private static final int BULLET_COLOR = 0xFFFFFFFF;

}

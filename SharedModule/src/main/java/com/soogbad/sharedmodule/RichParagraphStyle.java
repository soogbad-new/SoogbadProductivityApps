package com.soogbad.sharedmodule;

import android.text.Layout;
import android.text.style.BulletSpan;
import android.text.style.AlignmentSpan;
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
        else if(spanClass == AlignmentSpan.Standard.class) return new AlignmentSpan.Standard(Layout.Alignment.values()[value]);
        return null;
    }
    public static ParagraphStyle cloneSpan(ParagraphStyle span) {
        if(span instanceof BulletSpan) return new BulletSpan(BULLET_GAP_WIDTH, BULLET_COLOR);
        else if(span instanceof AlignmentSpan.Standard) return new AlignmentSpan.Standard(((AlignmentSpan.Standard)span).getAlignment());
        return null;
    }
    public boolean matchesSpanValue(ParagraphStyle span) {
        if(span instanceof BulletSpan) return true;
        else if(span instanceof AlignmentSpan.Standard) return ((AlignmentSpan.Standard)span).getAlignment().ordinal() == value;
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
        return new RichParagraphStyle<?>[] { BULLET, ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT };
    }
    
    public static final RichParagraphStyle<BulletSpan> BULLET = new RichParagraphStyle<>(BulletSpan.class, 0);
    public static final RichParagraphStyle<AlignmentSpan.Standard> ALIGN_LEFT = TEXT_ALIGNMENT(Layout.Alignment.ALIGN_NORMAL);
    public static final RichParagraphStyle<AlignmentSpan.Standard> ALIGN_CENTER = TEXT_ALIGNMENT(Layout.Alignment.ALIGN_CENTER);
    public static final RichParagraphStyle<AlignmentSpan.Standard> ALIGN_RIGHT = TEXT_ALIGNMENT(Layout.Alignment.ALIGN_OPPOSITE);

    public static RichParagraphStyle<AlignmentSpan.Standard> TEXT_ALIGNMENT(Layout.Alignment alignment) { return new RichParagraphStyle<>(AlignmentSpan.Standard.class, alignment.ordinal()); }

    public static final RichParagraphStyle<AlignmentSpan.Standard> DEFAULT_TEXT_ALIGNMENT = ALIGN_LEFT;

    private static final int BULLET_GAP_WIDTH = 20;
    private static final int BULLET_COLOR = 0xFFFFFFFF;

}

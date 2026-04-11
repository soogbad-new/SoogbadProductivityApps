package com.soogbad.sharedmodule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

public class RichEditText extends AppCompatEditText {

    private boolean ignoreTextChanges = false;
    public void setIgnoreTextChanges(boolean ignoreTextChanges) { this.ignoreTextChanges = ignoreTextChanges; }
    private final HashSet<RichTextStyle<?>> activeStyles = new HashSet<>();
    private boolean bulletListActive = false;
    private boolean textChanging;
    private int changeStart, changeCount;

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(textChangedListener);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(!focused) {
            activeStyles.clear();
            bulletListActive = false;
            notifyListener();
        }
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
            if(changeCount > 0) {
                for(RichTextStyle<?> style : RichTextStyle.values())
                    applyActiveStyle(editable, changeStart, changeCount, style, activeStyles.contains(style));
                handleBulletListNewLine(editable, changeStart, changeCount);
            }
            textChanging = true;
        }
    };
    private static void applyActiveStyle(Editable editable, int changeStart, int changeCount, RichTextStyle<?> style, boolean isActive) {
        if(isActive) {
            if(!isEntireRangeCovered(editable, changeStart, changeStart + changeCount, style)) {
                removeSpansInRange(editable, changeStart, changeStart + changeCount, style);
                editable.setSpan(style.createSpan(), changeStart, changeStart + changeCount, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        else if(style.isFlagStyle())
            removeSpansInRange(editable, changeStart, changeStart + changeCount, style);
    }

    public void toggleStyle(RichTextStyle<?> style) {
        Editable editable = getText();
        if(editable == null) return;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        if(selectionStart != selectionEnd) {
            applyStyleToSelection(editable, selectionStart, selectionEnd, style);
            updateCurrentActiveStyles(selectionStart, selectionEnd);
        }
        else {
            if(style.isFlagStyle())
                toggleActiveStyleFlag(style);
            else
                setActiveStyle(style);
            notifyListener();
        }
    }
    private static void applyStyleToSelection(Editable editable, int selectionStart, int selectionEnd, RichTextStyle<?> style) {
        boolean wasCovered = isEntireRangeCovered(editable, selectionStart, selectionEnd, style);
        removeSpansInRange(editable, selectionStart, selectionEnd, style);
        if(!wasCovered)
            editable.setSpan(style.createSpan(), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    private void toggleActiveStyleFlag(RichTextStyle<?> style) {
        if(activeStyles.contains(style))
            activeStyles.remove(style);
        else
            activeStyles.add(style);
    }
    private void setActiveStyle(RichTextStyle<?> style) {
        activeStyles.removeIf(activeStyle -> activeStyle.spanClass == style.spanClass);
        activeStyles.add(style);
    }

    private void updateCurrentActiveStyles(int selectionStart, int selectionEnd) {
        Editable editable = getText();
        if(editable == null) return;
        boolean hasTextSize = false, hasTextColor = false;
        for(RichTextStyle<?> style : RichTextStyle.values()) {
            boolean active = (selectionStart != selectionEnd)
                    ? isEntireRangeCovered(editable, selectionStart, selectionEnd, style)
                    : isStyleActiveAtCursorPosition(editable, selectionStart, style);
            if(active) {
                activeStyles.add(style);
                if(style.spanClass == AbsoluteSizeSpan.class) hasTextSize = true;
                if(style.spanClass == ForegroundColorSpan.class) hasTextColor = true;
            }
            else
                activeStyles.remove(style);
        }
        if(!hasTextSize)
            activeStyles.add(RichTextStyle.TEXT_SIZE(RichTextStyle.DEFAULT_TEXT_SIZE));
        if(!hasTextColor)
            activeStyles.add(RichTextStyle.TEXT_COLOR(RichTextStyle.DEFAULT_TEXT_COLOR));
        bulletListActive = isCursorInBulletedParagraph();
        notifyListener();
    }

    private static boolean isStyleActiveAtCursorPosition(Editable editable, int cursorPosition, RichTextStyle<?> style) {
        if(editable.toString().isEmpty()) return false;
        if(cursorPosition > 0 && editable.charAt(cursorPosition - 1) != '\n')
            return hasStyleAt(editable, cursorPosition - 1, style);
        else if(cursorPosition < editable.length())
            return hasStyleAt(editable, cursorPosition, style);
        return false;
    }
    private static boolean hasStyleAt(Editable editable, int position, RichTextStyle<?> style) {
        CharacterStyle[] spans = editable.getSpans(position, position + 1, style.spanClass);
        for(CharacterStyle span : spans) {
            if(style.matchesSpanValue(span)) {
                int spanStart = editable.getSpanStart(span); int spanEnd = editable.getSpanEnd(span);
                if(spanStart <= position && spanEnd > position)
                    return true;
            }
        }
        return false;
    }

    private static void removeSpansInRange(Editable editable, int rangeStart, int rangeEnd, RichTextStyle<?> style) {
        CharacterStyle[] spans = editable.getSpans(rangeStart, rangeEnd, style.spanClass);
        for(CharacterStyle span : spans) {
            if(!style.isFlagStyle() || style.matchesSpanValue(span)) {
                int spanStart = editable.getSpanStart(span); int spanEnd = editable.getSpanEnd(span);
                editable.removeSpan(span);
                if(spanStart < rangeStart)
                    editable.setSpan(RichTextStyle.cloneSpan(span), spanStart, rangeStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(spanEnd > rangeEnd)
                    editable.setSpan(RichTextStyle.cloneSpan(span), rangeEnd, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static boolean isEntireRangeCovered(Editable editable, int rangeStart, int rangeEnd, RichTextStyle<?> style) {
        if(rangeStart >= rangeEnd) return false;
        CharacterStyle[] spans = editable.getSpans(rangeStart, rangeEnd, style.spanClass);
        Arrays.sort(spans, Comparator.comparingInt(editable::getSpanStart));
        int coveredUntil = rangeStart;
        for(CharacterStyle span : spans) {
            if(style.matchesSpanValue(span)) {
                int spanStart = editable.getSpanStart(span); int spanEnd = editable.getSpanEnd(span);
                if(spanStart > coveredUntil)
                    return false;
                coveredUntil = Math.max(coveredUntil, spanEnd);
                if(coveredUntil >= rangeEnd)
                    return true;
            }
        }
        return false;
    }

    public interface StyleStateListener {
        void onStyleStateChanged(HashSet<RichTextStyle<?>> activeStyles, boolean bulletListActive);
    }
    private StyleStateListener styleStateListener;
    public void setStyleStateListener(StyleStateListener listener) { this.styleStateListener = listener; }
    private void notifyListener() {
        if(styleStateListener != null)
            styleStateListener.onStyleStateChanged(activeStyles, bulletListActive);
    }

    public void toggleBulletList() {
        Editable editable = getText();
        if(editable == null) return;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        int paragraphStart = getParagraphStart(editable.toString(), selectionStart); int paragraphEnd = getParagraphEnd(editable.toString(), selectionEnd);
        if(areAllParagraphsBulleted(editable, paragraphStart, paragraphEnd))
            removeBulletsFromParagraphs(editable, paragraphStart, paragraphEnd);
        else
            addBulletsToParagraphs(editable, paragraphStart, paragraphEnd);
        bulletListActive = isCursorInBulletedParagraph();
        notifyListener();
    }
    private static boolean areAllParagraphsBulleted(Editable editable, int start, int end) {
        int position = start;
        while(position <= end) {
            int paragraphEnd = getParagraphEnd(editable.toString(), position);
            if(paragraphEnd > end)
                paragraphEnd = end;
            BulletSpan[] spans = getBulletSpansInParagraph(editable, position, paragraphEnd);
            if(spans.length == 0)
                return false;
            position = paragraphEnd + 1;
        }
        return true;
    }
    private static void removeBulletsFromParagraphs(Editable editable, int start, int end) {
        BulletSpan[] spans = getBulletSpansInParagraph(editable, start, end);
        for(BulletSpan span : spans)
            editable.removeSpan(span);
    }
    private static void addBulletsToParagraphs(Editable editable, int start, int end) {
        String text = editable.toString();
        int position = start;
        while(position <= end) {
            int paragraphEnd = getParagraphEnd(text, position);
            if(paragraphEnd > end)
                paragraphEnd = end;
            BulletSpan[] spans = getBulletSpansInParagraph(editable, position, paragraphEnd);
            if(spans.length == 0) {
                int spanEnd = paragraphEnd < text.length() ? paragraphEnd + 1 : paragraphEnd;
                editable.setSpan(RichTextStyle.createBulletSpan(), position, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            position = paragraphEnd + 1;
        }
    }
    private void handleBulletListNewLine(Editable editable, int changeStart, int changeCount) {
        if(changeCount == 1 && editable.charAt(changeStart) == '\n' && changeStart < editable.length()) {
            int previousLineStart = getParagraphStart(editable.toString(), changeStart);
            BulletSpan[] spans = getBulletSpansInParagraph(editable, previousLineStart, changeStart);
            if(spans.length == 0) return;
            String lineText = editable.subSequence(previousLineStart, changeStart).toString().trim();
            if(lineText.isEmpty()) {
                removeBulletIfLineEmpty(editable, spans);
                return;
            }
            endBulletSpanBeforeNewLine(editable, spans, changeStart);
            addBulletAfterNewLine(editable, changeStart + 1);
        }
    }
    private void removeBulletIfLineEmpty(Editable editable, BulletSpan[] spans) { 
        for(BulletSpan span : spans)
            editable.removeSpan(span);
        bulletListActive = false;
        notifyListener();
    }
    private static void endBulletSpanBeforeNewLine(Editable editable, BulletSpan[] spans, int newLinePosition) {
        for(BulletSpan span : spans) {
            int spanStart = editable.getSpanStart(span);
            editable.removeSpan(span);
            editable.setSpan(RichTextStyle.createBulletSpan(), spanStart, newLinePosition + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }
    private static void addBulletAfterNewLine(Editable editable, int newLineStart) {
        int newLineEnd = getParagraphEnd(editable.toString(), newLineStart);
        int spanEnd = newLineEnd < editable.length() ? newLineEnd + 1 : newLineEnd;
        if(spanEnd < newLineStart)
            spanEnd = newLineStart;
        editable.setSpan(RichTextStyle.createBulletSpan(), newLineStart, Math.max(spanEnd, newLineStart + 1), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }
    private boolean isCursorInBulletedParagraph() {
        Editable editable = getText();
        if(editable == null) return false;
        int position = getSelectionStart();
        int paragraphStart = getParagraphStart(editable.toString(), position); int paragraphEnd = getParagraphEnd(editable.toString(), position);
        BulletSpan[] spans = getBulletSpansInParagraph(editable, paragraphStart, paragraphEnd);
        return spans.length > 0;
    }
    private static BulletSpan[] getBulletSpansInParagraph(Editable editable, int paragraphStart, int paragraphEnd) {
        BulletSpan[] spans = editable.getSpans(paragraphStart, paragraphEnd, BulletSpan.class);
        ArrayList<BulletSpan> result = new ArrayList<>();
        for(BulletSpan span : allSpans)
            if(editable.getSpanStart(span) >= paragraphStart)
                result.add(span);
        return result.toArray(new BulletSpan[0]);
    }
    private static int getParagraphStart(String text, int position) {
        int start = text.lastIndexOf('\n', position - 1);
        return start == -1 ? 0 : start + 1;
    }
    private static int getParagraphEnd(String text, int position) {
        int end = text.indexOf('\n', position);
        return end == -1 ? text.length() : end;
    }

    public void insertHyperlinkAtCursor(String url, String displayText) {
        Editable editable = getText();
        if(editable == null || url.isEmpty() || displayText.isEmpty()) return;
        int cursorPosition = getSelectionStart();
        editable.insert(cursorPosition, displayText);
        editable.setSpan(new URLSpan(url), cursorPosition, cursorPosition + displayText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public void createHyperlinkOnSelectedText(String url) {
        Editable editable = getText();
        if(editable == null || url.isEmpty()) return;
        removeHyperlinksFromSelection();
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        if(selectionStart != selectionEnd)
            editable.setSpan(new URLSpan(url), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public void replaceSelectedURLWithHyperlink(String displayText) {
        Editable editable = getText();
        if(editable == null || displayText.isEmpty()) return;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        String selectedURL = editable.subSequence(selectionStart, selectionEnd).toString();
        removeHyperlinksFromSelection();
        editable.replace(selectionStart, selectionEnd, displayText);
        editable.setSpan(new URLSpan(selectedURL), selectionStart, selectionStart + displayText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    private void removeHyperlinksFromSelection() {
        Editable editable = getText();
        if(editable == null) return;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        URLSpan[] spans = editable.getSpans(selectionStart, selectionEnd, URLSpan.class);
        for(URLSpan span : spans)
            editable.removeSpan(span);
    }
    public String getHyperlinkUrlAtSelection() {
        Editable editable = getText();
        if(editable == null) return null;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        int position = (selectionStart == selectionEnd && selectionStart > 0) ? selectionStart - 1 : selectionStart;
        URLSpan[] spans = editable.getSpans(position, selectionEnd, URLSpan.class);
        return spans.length > 0 ? spans[0].getURL() : null;
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP && getSelectionStart() == getSelectionEnd()) {
            boolean handled = handleHyperlinkClick(event);
            if(handled) return true;
        }
        return super.onTouchEvent(event);
    }
    private boolean handleHyperlinkClick(MotionEvent event) {
        Editable editable = getText();
        if(editable == null) return false;
        int x = (int)event.getX() - getTotalPaddingLeft() + getScrollX(); int y = (int)event.getY() - getTotalPaddingTop() + getScrollY();
        int line = getLayout().getLineForVertical(y); int offset = getLayout().getOffsetForHorizontal(line, x);
        URLSpan[] spans = editable.getSpans(offset, offset, URLSpan.class);
        if(spans.length > 0) {
            String url = spans[0].getURL();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            return true;
        }
        return false;
    }

}

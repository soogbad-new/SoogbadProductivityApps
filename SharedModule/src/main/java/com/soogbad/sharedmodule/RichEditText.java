package com.soogbad.sharedmodule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Editable;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ParagraphStyle;
import android.text.style.StyleSpan;
import android.text.style.AlignmentSpan;
import android.text.style.UnderlineSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

public class RichEditText extends AppCompatEditText {

    private boolean ignoreTextChanges = false;
    public void setIgnoreTextChanges(boolean ignoreTextChanges) { this.ignoreTextChanges = ignoreTextChanges; }
    private final HashSet<RichCharacterStyle<?>> activeCharacterStyles = new HashSet<>();
    private final HashSet<RichParagraphStyle<?>> activeParagraphStyles = new HashSet<>();
    private boolean textChanging;
    private int changeStart, changeCount;
    private final Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float arrowSize;

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(textChangedListener);
        arrowPaint.setColor(Color.WHITE);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
    }

    public interface StyleStateListener {
        void onStyleStateChanged(HashSet<RichCharacterStyle<?>> activeCharacterStyles, HashSet<RichParagraphStyle<?>> activeParagraphStyles);
    }
    private StyleStateListener styleStateListener;
    public void setStyleStateListener(StyleStateListener listener) { this.styleStateListener = listener; }
    private void notifyListener() {
        if(styleStateListener != null)
            styleStateListener.onStyleStateChanged(activeCharacterStyles, activeParagraphStyles);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(!focused) {
            activeCharacterStyles.clear(); activeParagraphStyles.clear();
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
        updateCurrentActiveCharacterStyles(selectionStart, selectionEnd);
        updateCurrentActiveParagraphStyles();
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
                for(RichCharacterStyle<?> style : RichCharacterStyle.values())
                    applyActiveCharacterStyle(editable, changeStart, changeCount, style, activeCharacterStyles.contains(style));
                handleParagraphStyleNewLine(editable, changeStart, changeCount);
                autoDetectLinks(editable, changeStart, changeCount);
            }
            textChanging = true;
        }
    };

    // ===== Character Styles =====

    private static void applyActiveCharacterStyle(Editable editable, int changeStart, int changeCount, RichCharacterStyle<?> style, boolean isActive) {
        if(isActive) {
            if(!isEntireRangeCovered(editable, changeStart, changeStart + changeCount, style)) {
                removeSpansInRange(editable, changeStart, changeStart + changeCount, style);
                editable.setSpan(style.createSpan(), changeStart, changeStart + changeCount, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        else if(style.isFlagStyle())
            removeSpansInRange(editable, changeStart, changeStart + changeCount, style);
    }
    private void updateCurrentActiveCharacterStyles(int selectionStart, int selectionEnd) {
        Editable editable = getText();
        if(editable == null) return;
        boolean hasTextSize = false, hasTextColor = false;
        for(RichCharacterStyle<?> style : RichCharacterStyle.values()) {
            boolean active = (selectionStart != selectionEnd)
                    ? isEntireRangeCovered(editable, selectionStart, selectionEnd, style)
                    : isStyleActiveAtCursorPosition(editable, selectionStart, style);
            if(active) {
                activeCharacterStyles.add(style);
                if(style.spanClass == AbsoluteSizeSpan.class) hasTextSize = true;
                if(style.spanClass == ForegroundColorSpan.class) hasTextColor = true;
            }
            else
                activeCharacterStyles.remove(style);
        }
        if(!hasTextSize)
            activeCharacterStyles.add(RichCharacterStyle.TEXT_SIZE(RichCharacterStyle.DEFAULT_TEXT_SIZE));
        if(!hasTextColor)
            activeCharacterStyles.add(RichCharacterStyle.TEXT_COLOR(RichCharacterStyle.DEFAULT_TEXT_COLOR));
        notifyListener();
    }

    public void toggleCharacterStyle(RichCharacterStyle<?> style) {
        Editable editable = getText();
        if(editable == null) return;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        if(selectionStart != selectionEnd) {
            applyStyleToSelection(editable, selectionStart, selectionEnd, style);
            updateCurrentActiveCharacterStyles(selectionStart, selectionEnd);
        }
        else {
            if(style.isFlagStyle())
                toggleActiveCharacterStyleFlag(style);
            else
                setActiveCharacterStyle(style);
            notifyListener();
        }
    }
    private static void applyStyleToSelection(Editable editable, int selectionStart, int selectionEnd, RichCharacterStyle<?> style) {
        boolean wasCovered = isEntireRangeCovered(editable, selectionStart, selectionEnd, style);
        removeSpansInRange(editable, selectionStart, selectionEnd, style);
        if(!wasCovered)
            editable.setSpan(style.createSpan(), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    private void toggleActiveCharacterStyleFlag(RichCharacterStyle<?> style) {
        if(activeCharacterStyles.contains(style))
            activeCharacterStyles.remove(style);
        else
            activeCharacterStyles.add(style);
    }
    private void setActiveCharacterStyle(RichCharacterStyle<?> style) {
        activeCharacterStyles.removeIf(activeCharacterStyle -> activeCharacterStyle.spanClass == style.spanClass);
        activeCharacterStyles.add(style);
    }

    private static boolean isStyleActiveAtCursorPosition(Editable editable, int cursorPosition, RichCharacterStyle<?> style) {
        if(editable.toString().isEmpty()) return false;
        if(cursorPosition > 0 && editable.charAt(cursorPosition - 1) != '\n')
            return hasStyleAtCharacter(editable, cursorPosition - 1, style);
        else if(cursorPosition < editable.length())
            return hasStyleAtCharacter(editable, cursorPosition, style);
        return false;
    }
    private static boolean hasStyleAtCharacter(Editable editable, int position, RichCharacterStyle<?> style) {
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
    private static void removeSpansInRange(Editable editable, int rangeStart, int rangeEnd, RichCharacterStyle<?> style) {
        CharacterStyle[] spans = editable.getSpans(rangeStart, rangeEnd, style.spanClass);
        for(CharacterStyle span : spans) {
            if(!style.isFlagStyle() || style.matchesSpanValue(span)) {
                int spanStart = editable.getSpanStart(span); int spanEnd = editable.getSpanEnd(span);
                editable.removeSpan(span);
                if(spanStart < rangeStart)
                    editable.setSpan(RichCharacterStyle.cloneSpan(span), spanStart, rangeStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(spanEnd > rangeEnd)
                    editable.setSpan(RichCharacterStyle.cloneSpan(span), rangeEnd, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
    private static boolean isEntireRangeCovered(Editable editable, int rangeStart, int rangeEnd, RichCharacterStyle<?> style) {
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

    // ===== Paragraph Styles =====

    private void updateCurrentActiveParagraphStyles() {
        Editable editable = getText();
        if(editable == null) return;
        activeParagraphStyles.clear();
        int position = getSelectionStart();
        int paragraphStart = getParagraphStart(editable.toString(), position); int paragraphEnd = getParagraphEnd(editable.toString(), position);
        boolean isRtl = isParagraphRtl(editable, paragraphStart, paragraphEnd);
        RichParagraphStyle<?> activeAlignment = null;
        for(RichParagraphStyle<?> style : RichParagraphStyle.values())
            if(hasStyleAtParagraph(editable, paragraphStart, paragraphEnd, style)) {
                if(style.spanClass == AlignmentSpan.Standard.class)
                    activeAlignment = reverseAlignmentAccordingToDirection(style, isRtl);
                else
                    activeParagraphStyles.add(style);
            }
        if(activeAlignment == null)
            activeAlignment = isRtl ? RichParagraphStyle.ALIGN_RIGHT : RichParagraphStyle.ALIGN_LEFT;
        activeParagraphStyles.add(activeAlignment);
        notifyListener();
    }

    public void toggleParagraphStyle(RichParagraphStyle<?> style) {
        Editable editable = getText();
        if(editable == null) return;
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        int paragraphStart = getParagraphStart(editable.toString(), selectionStart); int paragraphEnd = getParagraphEnd(editable.toString(), selectionEnd);
        boolean wasCovered = areAllParagraphsStyled(editable, paragraphStart, paragraphEnd, style);
        removeParagraphSpans(editable, paragraphStart, paragraphEnd, style);
        if(!wasCovered || style.spanClass == AlignmentSpan.Standard.class)
            addStyleToParagraphs(editable, paragraphStart, paragraphEnd, style);
        updateCurrentActiveParagraphStyles();
    }
    private static void addStyleToParagraphs(Editable editable, int start, int end, RichParagraphStyle<?> style) {
        int position = start;
        while(position <= end) {
            int paragraphEnd = getParagraphEnd(editable.toString(), position);
            if(paragraphEnd > end)
                paragraphEnd = end;
            if(style.spanClass == AlignmentSpan.Standard.class)
                style = reverseAlignmentAccordingToDirection(style, isParagraphRtl(editable, position, paragraphEnd));
            if(!hasStyleAtParagraph(editable, position, paragraphEnd, style)) {
                int spanEnd = paragraphEnd < editable.length() ? paragraphEnd + 1 : paragraphEnd;
                editable.setSpan(style.createSpan(), position, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            position = paragraphEnd + 1;
        }
    }

    private static boolean hasStyleAtParagraph(Editable editable, int paragraphStart, int paragraphEnd, RichParagraphStyle<?> style) {
        int paragraphEndExclusive = paragraphEnd < editable.length() ? paragraphEnd + 1 : paragraphEnd;
        ParagraphStyle[] spans = editable.getSpans(paragraphStart, paragraphEndExclusive, style.spanClass);
        for(ParagraphStyle span : spans)
            if(style.matchesSpanValue(span))
                return true;
        return false;
    }
    private static void removeParagraphSpans(Editable editable, int start, int end, RichParagraphStyle<?> style) {
        int endExclusive = end < editable.length() ? end + 1 : end;
        ParagraphStyle[] spans = editable.getSpans(start, endExclusive, style.spanClass);
        for(ParagraphStyle span : spans) {
            if(!style.isFlagStyle() || style.matchesSpanValue(span)) {
                int spanStart = editable.getSpanStart(span); int spanEnd = editable.getSpanEnd(span);
                editable.removeSpan(span);
                if(spanStart < start)
                    editable.setSpan(RichParagraphStyle.cloneSpan(span), spanStart, start, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                if(spanEnd > endExclusive)
                    editable.setSpan(RichParagraphStyle.cloneSpan(span), endExclusive, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
    }
    private static boolean areAllParagraphsStyled(Editable editable, int start, int end, RichParagraphStyle<?> style) {
        int position = start;
        while(position <= end) {
            int paragraphEnd = getParagraphEnd(editable.toString(), position);
            if(paragraphEnd > end)
                paragraphEnd = end;
            if(style.spanClass == AlignmentSpan.Standard.class)
                style = reverseAlignmentAccordingToDirection(style, isParagraphRtl(editable, position, paragraphEnd));
            if(!hasStyleAtParagraph(editable, position, paragraphEnd, style))
                return false;
            position = paragraphEnd + 1;
        }
        return true;
    }

    private void handleParagraphStyleNewLine(Editable editable, int changeStart, int changeCount) {
        if(changeCount != 1 || editable.charAt(changeStart) != '\n' || changeStart >= editable.length())
            return;
        int previousLineStart = getParagraphStart(editable.toString(), changeStart);
        for(RichParagraphStyle<?> style : RichParagraphStyle.values()) {
            ParagraphStyle[] spans = getParagraphSpans(editable, previousLineStart, changeStart, style);
            if(spans.length == 0) continue;
            endParagraphSpanBeforeNewLine(editable, spans, changeStart);
            addParagraphSpanAfterNewLine(editable, spans, changeStart);
        }
    }
    private static void endParagraphSpanBeforeNewLine(Editable editable, ParagraphStyle[] spans, int newLinePosition) {
        for(ParagraphStyle span : spans) {
            int spanStart = editable.getSpanStart(span);
            editable.removeSpan(span);
            editable.setSpan(RichParagraphStyle.cloneSpan(span), spanStart, newLinePosition + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }
    private static void addParagraphSpanAfterNewLine(Editable editable, ParagraphStyle[] spans, int newLinePosition) {
        int newLineEnd = getParagraphEnd(editable.toString(), newLinePosition + 1);
        int spanStart = newLinePosition + 1;
        int spanEnd = newLineEnd < editable.length() ? newLineEnd + 1 : newLineEnd;
        editable.setSpan(RichParagraphStyle.cloneSpan(spans[0]), spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private static RichParagraphStyle<?> reverseAlignmentAccordingToDirection(RichParagraphStyle<?> style, boolean isDirectionRtl) {
        if(style.value == RichParagraphStyle.ALIGN_CENTER.value)
            return style;
        else if(isDirectionRtl)
            return style.value == RichParagraphStyle.ALIGN_LEFT.value ? RichParagraphStyle.ALIGN_RIGHT : RichParagraphStyle.ALIGN_LEFT;
        else
            return style;
    }
    private static ParagraphStyle[] getParagraphSpans(Editable editable, int paragraphStart, int paragraphEnd, RichParagraphStyle<?> style) {
        ParagraphStyle[] spans = editable.getSpans(paragraphStart, paragraphEnd, style.spanClass);
        ArrayList<ParagraphStyle> result = new ArrayList<>();
        for(ParagraphStyle span : spans)
            if(style.matchesSpanValue(span))
                result.add(span);
        return result.toArray(new ParagraphStyle[0]);
    }
    private static boolean isParagraphRtl(Editable editable, int start, int end) {
        for(int i = start; i < end; i++) {
            byte directionality = Character.getDirectionality(editable.charAt(i));
            if(directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT)
                return true;
            if(directionality == Character.DIRECTIONALITY_LEFT_TO_RIGHT)
                return false;
        }
        return false;
    }
    private static int getParagraphStart(String text, int position) {
        int start = text.lastIndexOf('\n', position - 1);
        return start == -1 ? 0 : start + 1;
    }
    private static int getParagraphEnd(String text, int position) {
        int end = text.indexOf('\n', position);
        return end == -1 ? text.length() : end;
    }

    // ===== Hyperlinks =====

    @SuppressWarnings("UnnecessaryLocalVariable")
    private static void autoDetectLinks(Editable editable, int changeStart, int changeCount) {
        if(changeCount != 1) return;
        char typed = editable.charAt(changeStart);
        if(typed != ' ' && typed != '\n') return;
        int wordEnd = changeStart;
        int wordStart = wordEnd;
        while(wordStart > 0 && editable.charAt(wordStart - 1) != ' ' && editable.charAt(wordStart - 1) != '\n')
            wordStart--;
        if(wordStart >= wordEnd) return;
        URLSpan[] existingSpans = editable.getSpans(wordStart, wordEnd, URLSpan.class);
        if(existingSpans.length > 0) return;
        String url = editable.toString().substring(wordStart, wordEnd);
        if(Utility.isLinkUrlValid(url))
            editable.setSpan(new URLSpan(url), wordStart, wordEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            if(url.startsWith("http://") || url.startsWith("https://"))
                openWebLink(url);
            if(url.startsWith("EVENT-"))
                openItemLink(url, "com.soogbad.soogbadcalendar", "EventActivity", url.substring("EVENT-".length()));
            else if(url.startsWith("NOTE-"))
                openItemLink(url, "com.soogbad.soogbadnotes", "NoteActivity", url.substring("NOTE-".length()));
            else if(url.startsWith("REMINDER-"))
                openItemLink(url, "com.soogbad.soogbadreminders", "ReminderActivity", url.substring("REMINDER-".length()));
            else
                return false;
            return true;
        }
        return false;
    }
    private void openWebLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
    private void openItemLink(String url, String targetPackage, String targetActivity, String itemUuid) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(targetPackage, targetPackage + "." + targetActivity));
        intent.putExtra("item_uuid", itemUuid);
        if(!targetPackage.equals(getContext().getPackageName()))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    // ===== Collapsible Regions =====

    public void toggleCollapsibleRegion() {
    }

}

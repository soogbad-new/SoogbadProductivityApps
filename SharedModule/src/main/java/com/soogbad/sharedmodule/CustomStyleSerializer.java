package com.soogbad.sharedmodule;

import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BulletSpan;

import org.xml.sax.XMLReader;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;

public class CustomStyleSerializer {

    static String serializeCustomTags(Spanned spannedText, String html) {
        html = insertParagraphTags(spannedText, html);
        return html;
    }
    static String deserializeCustomTags(String html) {
        html = replaceTextSizeTags(html);
        return html;
    }

    static final Html.TagHandler TAG_HANDLER = (boolean opening, String tag, Editable output, XMLReader xmlReader) -> {
        if(tag.startsWith("mysize")) {
            if(opening) {
                int size = Integer.parseInt(tag.substring(6));
                output.setSpan(new MySizeMark(size), output.length(), output.length(), Spanned.SPAN_MARK_MARK);
            }
            else {
                MySizeMark[] marks = output.getSpans(0, output.length(), MySizeMark.class);
                if(marks.length > 0) {
                    MySizeMark mark = marks[marks.length - 1];
                    int start = output.getSpanStart(mark); int end = output.length();
                    output.removeSpan(mark);
                    output.setSpan(new AbsoluteSizeSpan(mark.size, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        else if(tag.equals("mybullet")) {
            if(opening)
                output.setSpan(new MyBulletMark(), output.length(), output.length(), Spanned.SPAN_MARK_MARK);
            else {
                MyBulletMark[] marks = output.getSpans(0, output.length(), MyBulletMark.class);
                if(marks.length > 0) {
                    MyBulletMark mark = marks[marks.length - 1];
                    int start = output.getSpanStart(mark); int end = output.length();
                    output.removeSpan(mark);
                    output.setSpan(RichParagraphStyle.BULLET.createSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }
        else if(tag.startsWith("myalign")) {
            if(opening) {
                int value = Integer.parseInt(tag.substring(7));
                output.setSpan(new MyAlignMark(value), output.length(), output.length(), Spanned.SPAN_MARK_MARK);
            }
            else {
                MyAlignMark[] marks = output.getSpans(0, output.length(), MyAlignMark.class);
                if(marks.length > 0) {
                    MyAlignMark mark = marks[marks.length - 1];
                    int start = output.getSpanStart(mark); int end = output.length();
                    output.removeSpan(mark);
                    output.setSpan(new AlignmentSpan.Standard(Layout.Alignment.values()[mark.value]), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }
    };

    private record MySizeMark(int size) { }
    private record MyBulletMark() { }
    private record MyAlignMark(int value) { }

    private static String replaceTextSizeTags(String html) {
        StringBuilder htmlBuilder = new StringBuilder();
        ArrayDeque<String> stack = new ArrayDeque<>();
        int i = 0;
        while(i < html.length()) {
            if(html.startsWith("<span style=\"font-size:", i)) {
                int start = i + "<span style=\"font-size:".length(); int end = html.indexOf("px", start); int close = html.indexOf(">", end);
                String size = html.substring(start, end);
                htmlBuilder.append("<mysize").append(size).append(">");
                stack.push(size);
                i = close + 1;
            }
            else if(html.startsWith("<span ", i) || html.startsWith("<span>", i)) {
                int close = html.indexOf(">", i);
                htmlBuilder.append(html, i, close + 1);
                stack.push("");
                i = close + 1;
            }
            else if(html.startsWith("</span>", i)) {
                String size = !stack.isEmpty() ? stack.pop() : "";
                if(!size.isEmpty())
                    htmlBuilder.append("</mysize").append(size).append(">");
                else
                    htmlBuilder.append("</span>");
                i += "</span>".length();
            }
            else {
                htmlBuilder.append(html.charAt(i));
                i++;
            }
        }
        return htmlBuilder.toString();
    }

    private static String insertParagraphTags(Spanned spannedText, String html) {
        String text = spannedText.toString();
        BulletSpan[] bulletSpans = spannedText.getSpans(0, spannedText.length(), BulletSpan.class);
        HashSet<Integer> bulletLines = new HashSet<>();
        for(BulletSpan span : bulletSpans) {
            int spanStart = spannedText.getSpanStart(span); int spanEnd = spannedText.getSpanEnd(span);
            int line = 0;
            for(int c = 0; c < spanStart; c++)
                if(text.charAt(c) == '\n')
                    line++;
            bulletLines.add(line);
            for(int c = spanStart; c < spanEnd; c++)
                if(text.charAt(c) == '\n' && c + 1 < spanEnd) {
                    line++;
                    bulletLines.add(line);
                }
        }
        AlignmentSpan.Standard[] alignSpans = spannedText.getSpans(0, spannedText.length(), AlignmentSpan.Standard.class);
        HashMap<Integer, Integer> alignmentLines = new HashMap<>();
        for(AlignmentSpan.Standard span : alignSpans) {
            int spanStart = spannedText.getSpanStart(span); int spanEnd = spannedText.getSpanEnd(span);
            int line = 0;
            for(int c = 0; c < spanStart; c++)
                if(text.charAt(c) == '\n')
                    line++;
            alignmentLines.put(line, span.getAlignment().ordinal());
            for(int c = spanStart; c < spanEnd; c++)
                if(text.charAt(c) == '\n' && c + 1 < spanEnd) {
                    line++;
                    alignmentLines.put(line, span.getAlignment().ordinal());
                }
        }
        if(bulletLines.isEmpty() && alignmentLines.isEmpty())
            return html;
        return getHtmlWithParagraphTags(html, bulletLines, alignmentLines);
    }
    private static String getHtmlWithParagraphTags(String html, HashSet<Integer> bulletLines, HashMap<Integer, Integer> alignmentLines) {
        StringBuilder result = new StringBuilder();
        int lineIndex = 0;
        int i = 0;
        while(i < html.length()) {
            if(html.startsWith("<p ", i) || html.startsWith("<p>", i)) {
                int closeStart = html.indexOf("</p>", i);
                boolean isBulleted = bulletLines.contains(lineIndex);
                Integer alignment = alignmentLines.get(lineIndex);
                if(alignment != null) result.append("<myalign").append(alignment).append(">");
                if(isBulleted) result.append("<mybullet>");
                result.append(html, i, closeStart + 4);
                if(isBulleted) result.append("</mybullet>");
                if(alignment != null) result.append("</myalign").append(alignment).append(">");
                lineIndex++;
                i = closeStart + 4;
            }
            else {
                result.append(html.charAt(i));
                i++;
            }
        }
        return result.toString();
    }

}

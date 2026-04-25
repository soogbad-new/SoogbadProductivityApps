package com.soogbad.sharedmodule;

import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RichTextSerializer {

    public static String serialize(Spanned spanned) {
        try {
            JSONObject doc = new JSONObject();
            doc.put("text", spanned.toString());
            doc.put("spans", serializeSpans(spanned));
            return doc.toString();
        } catch(JSONException e) { throw new RuntimeException(e); }
    }

    public static SpannedString deserialize(String json) {
        try {
            JSONObject doc = new JSONObject(json);
            String text = doc.getString("text");
            SpannableString ss = new SpannableString(text);
            JSONArray spans = doc.getJSONArray("spans");
            for(int i = 0; i < spans.length(); i++) {
                JSONObject obj = spans.getJSONObject(i);
                Object span = createSpan(obj);
                if(span != null)
                    ss.setSpan(span, obj.getInt("start"), obj.getInt("end"), obj.getInt("flags"));
            }
            return new SpannedString(ss);
        } catch(JSONException e) { throw new RuntimeException(e); }
    }

    private static JSONArray serializeSpans(Spanned spanned) throws JSONException {
        List<JSONObject> spanList = new ArrayList<>();

        for(Object span : spanned.getSpans(0, spanned.length(), Object.class)) {
            JSONObject obj = spanToJson(spanned, span);
            if(obj != null) spanList.add(obj);
        }

        spanList = mergeSpans(spanList);

        JSONArray array = new JSONArray();
        for(JSONObject obj : spanList) array.put(obj);
        return array;
    }

    private static JSONObject spanToJson(Spanned spanned, Object span) throws JSONException {
        int start = spanned.getSpanStart(span);
        int end = spanned.getSpanEnd(span);
        int flags = spanned.getSpanFlags(span);

        JSONObject obj = new JSONObject();
        obj.put("start", start);
        obj.put("end", end);
        obj.put("flags", flags);

        if(span instanceof StyleSpan) {
            int style = ((StyleSpan) span).getStyle();
            obj.put("type", style == Typeface.BOLD ? "bold" : "italic");
        }
        else if(span instanceof UnderlineSpan) {
            obj.put("type", "underline");
        }
        else if(span instanceof AbsoluteSizeSpan) {
            obj.put("type", "size");
            obj.put("size", ((AbsoluteSizeSpan) span).getSize());
        }
        else if(span instanceof ForegroundColorSpan) {
            obj.put("type", "color");
            obj.put("color", ((ForegroundColorSpan) span).getForegroundColor());
        }
        else if(span instanceof URLSpan) {
            obj.put("type", "url");
            obj.put("url", ((URLSpan) span).getURL());
        }
        else if(span instanceof BulletSpan) {
            obj.put("type", "bullet");
        }
        else if(span instanceof AlignmentSpan.Standard) {
            obj.put("type", "align");
            obj.put("align", ((AlignmentSpan.Standard) span).getAlignment().ordinal());
        }
        else {
            return null;
        }

        return obj;
    }

    private static Object createSpan(JSONObject obj) throws JSONException {
        String type = obj.getString("type");
        return switch(type) {
            case "bold" -> new StyleSpan(Typeface.BOLD);
            case "italic" -> new StyleSpan(Typeface.ITALIC);
            case "underline" -> new UnderlineSpan();
            case "size" -> new AbsoluteSizeSpan(obj.getInt("size"), true);
            case "color" -> new ForegroundColorSpan(obj.getInt("color"));
            case "url" -> new URLSpan(obj.getString("url"));
            case "bullet" -> RichParagraphStyle.BULLET.createSpan();
            case "align" -> new AlignmentSpan.Standard(Layout.Alignment.values()[obj.getInt("align")]);
            default -> null;
        };
    }

    /**
     * Merges spans of the same type and value that overlap or touch each other.
     * This consolidates spans that were created separately (e.g. exclusive spans from
     * toggling selected text and inclusive spans from typing with a style active).
     */
    private static List<JSONObject> mergeSpans(List<JSONObject> spans) throws JSONException {
        if(spans.size() <= 1) return spans;

        List<JSONObject> result = new ArrayList<>();
        List<JSONObject> remaining = new ArrayList<>(spans);

        while(!remaining.isEmpty()) {
            JSONObject current = remaining.remove(0);
            String mergeKey = getMergeKey(current);

            List<JSONObject> sameType = new ArrayList<>();
            sameType.add(current);
            List<JSONObject> different = new ArrayList<>();

            for(JSONObject other : remaining) {
                if(getMergeKey(other).equals(mergeKey))
                    sameType.add(other);
                else
                    different.add(other);
            }

            remaining = different;

            sameType.sort((a, b) -> {
                try { return Integer.compare(a.getInt("start"), b.getInt("start")); }
                catch(JSONException e) { throw new RuntimeException(e); }
            });

            JSONObject merged = sameType.get(0);
            for(int i = 1; i < sameType.size(); i++) {
                JSONObject next = sameType.get(i);
                int mergedEnd = merged.getInt("end");
                int nextStart = next.getInt("start");
                if(nextStart <= mergedEnd) {
                    merged.put("end", Math.max(mergedEnd, next.getInt("end")));
                    merged.put("flags", Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    result.add(merged);
                    merged = next;
                }
            }
            result.add(merged);
        }

        return result;
    }

    /** Returns a key that identifies spans that should be merged together (same type + same value). */
    private static String getMergeKey(JSONObject obj) throws JSONException {
        String type = obj.getString("type");
        return switch(type) {
            case "size" -> "size:" + obj.getInt("size");
            case "color" -> "color:" + obj.getInt("color");
            case "align" -> "align:" + obj.getInt("align");
            case "url" -> "url:" + obj.getString("url");
            default -> type;
        };
    }
}

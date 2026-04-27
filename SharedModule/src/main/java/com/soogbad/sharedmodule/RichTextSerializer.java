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

@SuppressWarnings("IfCanBeSwitch")
public class RichTextSerializer {

    public static String serialize(Spanned spanned) {
        try {
            JSONObject document = new JSONObject();
            document.put("text", spanned.toString());
            document.put("spans", serializeSpans(spanned));
            return document.toString();
        } catch(JSONException e) { throw new RuntimeException(e); }
    }
    public static SpannedString deserialize(String json) {
        try {
            JSONObject document = new JSONObject(json);
            String text = document.getString("text");
            JSONArray spans = document.getJSONArray("spans");
            return deserializeSpans(text, spans);
        } catch(JSONException e) { throw new RuntimeException(e); }
    }

    private static JSONArray serializeSpans(Spanned spanned) throws JSONException {
        JSONArray array = new JSONArray();
        for(Object span : spanned.getSpans(0, spanned.length(), Object.class)) {
            JSONObject spanJson = serializeSpan(spanned, span);
            if(spanJson != null)
                array.put(spanJson);
        }
        return array;
    }
    private static SpannedString deserializeSpans(String text, JSONArray spans) throws JSONException {
        List<JSONObject> spanList = mergeOverlappingSpans(spans);
        SpannableString spannableString = new SpannableString(text);
        for(JSONObject spanJson : spanList) {
            Object span = deserializeSpan(spanJson);
            if(span != null)
                spannableString.setSpan(span, spanJson.getInt("start"), spanJson.getInt("end"), spanJson.getInt("flags"));
        }
        return new SpannedString(spannableString);
    }

    private static JSONObject serializeSpan(Spanned spanned, Object span) throws JSONException {
        int start = spanned.getSpanStart(span); int end = spanned.getSpanEnd(span); int flags = spanned.getSpanFlags(span);
        JSONObject spanJson = new JSONObject();
        spanJson.put("start", start); spanJson.put("end", end); spanJson.put("flags", flags);
        if(span instanceof StyleSpan) spanJson.put("type", ((StyleSpan)span).getStyle() == Typeface.BOLD ? "bold" : "italic");
        else if(span instanceof UnderlineSpan) spanJson.put("type", "underline");
        else if(span instanceof AbsoluteSizeSpan) { spanJson.put("type", "size"); spanJson.put("size", ((AbsoluteSizeSpan)span).getSize()); }
        else if(span instanceof ForegroundColorSpan) { spanJson.put("type", "color"); spanJson.put("color", ((ForegroundColorSpan)span).getForegroundColor()); }
        else if(span instanceof BulletSpan) spanJson.put("type", "bullet");
        else if(span instanceof AlignmentSpan.Standard) { spanJson.put("type", "align"); spanJson.put("align", ((AlignmentSpan.Standard)span).getAlignment().ordinal()); }
        else if(span instanceof URLSpan) { spanJson.put("type", "url"); spanJson.put("url", ((URLSpan)span).getURL()); }
        else if(span instanceof CollapsibleRegionSpan) spanJson.put("type", "region");
        else return null;
        return spanJson;
    }
    private static Object deserializeSpan(JSONObject spanJson) throws JSONException {
        String type = spanJson.getString("type");
        if(type.equals("bold")) return RichCharacterStyle.BOLD.createSpan();
        else if(type.equals("italic")) return RichCharacterStyle.ITALIC.createSpan();
        else if(type.equals("underline")) return RichCharacterStyle.UNDERLINE.createSpan();
        else if(type.equals("size")) return new AbsoluteSizeSpan(spanJson.getInt("size"), true);
        else if(type.equals("color")) return new ForegroundColorSpan(spanJson.getInt("color"));
        else if(type.equals("bullet")) return RichParagraphStyle.BULLET.createSpan();
        else if(type.equals("align")) return RichParagraphStyle.TEXT_ALIGNMENT(Layout.Alignment.values()[spanJson.getInt("align")]).createSpan();
        else if(type.equals("url")) return new URLSpan(spanJson.getString("url"));
        else if(type.equals("region")) return new CollapsibleRegionSpan();
        return null;
    }

    private static List<JSONObject> mergeOverlappingSpans(JSONArray spans) throws JSONException {
        List<JSONObject> spanList = new ArrayList<>();
        for(int i = 0; i < spans.length(); i++) spanList.add(spans.getJSONObject(i));
        if(spans.length() <= 1) return spanList;
        List<JSONObject> result = new ArrayList<>();
        List<JSONObject> remaining = new ArrayList<>(spanList);
        while(!remaining.isEmpty()) {
            JSONObject current = remaining.remove(0);
            List<JSONObject> matchingSpans = new ArrayList<>();
            matchingSpans.add(current);
            List<JSONObject> nonMatchingSpans = new ArrayList<>();
            for(JSONObject other : remaining) {
                if(compareSpans(current, other))
                    matchingSpans.add(other);
                else
                    nonMatchingSpans.add(other);
            }
            remaining = nonMatchingSpans;
            matchingSpans.sort((a, b) -> {
                try { return Integer.compare(a.getInt("start"), b.getInt("start")); }
                catch(JSONException e) { throw new RuntimeException(e); }
            });
            JSONObject mergedSpan = matchingSpans.get(0);
            for(int i = 1; i < matchingSpans.size(); i++) {
                JSONObject nextSpan = matchingSpans.get(i);
                int mergedEnd = mergedSpan.getInt("end"); int nextStart = nextSpan.getInt("start");
                if(nextStart < mergedEnd) {
                    mergedSpan.put("end", Math.max(mergedEnd, nextSpan.getInt("end")));
                    mergedSpan.put("flags", Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else {
                    result.add(mergedSpan);
                    mergedSpan = nextSpan;
                }
            }
            result.add(mergedSpan);
        }
        return result;
    }
    private static boolean compareSpans(JSONObject a, JSONObject b) throws JSONException {
        String type = a.getString("type");
        if(!type.equals(b.getString("type"))) return false;
        else if(type.equals("size")) return a.getInt("size") == b.getInt("size");
        else if(type.equals("color")) return a.getInt("color") == b.getInt("color");
        else if(type.equals("align")) return a.getInt("align") == b.getInt("align");
        else if(type.equals("url")) return a.getString("url").equals(b.getString("url"));
        else return true;
    }

}

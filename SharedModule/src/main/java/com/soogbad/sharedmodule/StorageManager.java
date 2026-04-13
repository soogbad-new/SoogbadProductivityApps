package com.soogbad.sharedmodule;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BulletSpan;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings("ReadWriteStringCanBeUsed")
public class StorageManager {

    private final Path directory;

    public StorageManager(Path directory) {
        this.directory = directory;
        if(!Files.exists(directory)) {
            try { Files.createDirectory(directory); }
            catch(IOException e) { throw new RuntimeException(e); }
        }
    }

    public ArrayList<String> loadItemUUIDs() {
        ArrayList<String> uuids = new ArrayList<>();
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.html")) {
            stream.forEach(path -> uuids.add(path.getFileName().toString().replace(".html", "")));
        } catch(IOException e) { throw new RuntimeException(e); }
        return uuids;
    }

    public void saveContentToHtmlFile(String uuid, Spanned spannedText) {
        String html = Html.toHtml(spannedText, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        html = insertHtmlBulletTags(spannedText, html);
        try { Files.write(directory.resolve(uuid + ".html"), html.getBytes(StandardCharsets.UTF_16)); }
        catch(IOException e) { throw new RuntimeException(e); }
    }

    public SpannedString loadContentFromHtmlFile(String uuid) {
        String html;
        try { html = new String(Files.readAllBytes(directory.resolve(uuid + ".html")), StandardCharsets.UTF_16); }
        catch(IOException e) { throw new RuntimeException(e); }
        html = replaceHtmlTextSizeTags(html);
        return new SpannedString(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT, null, TAG_HANDLER));
    }

    public void saveMetadataToJsonFile(String uuid, String title, Item.ItemOptions options) {
        try {
            JSONObject metadata = new JSONObject();
            metadata.put("title", title);
            metadata.put("options", options.toJson());
            Files.write(directory.resolve(uuid + ".json"), metadata.toString().getBytes(StandardCharsets.UTF_16));
        } catch(JSONException | IOException e) { throw new RuntimeException(e); }
    }

    public JSONObject loadMetadataFromJsonFile(String uuid) {
        try {
            String json = new String(Files.readAllBytes(directory.resolve(uuid + ".json")), StandardCharsets.UTF_16);
            return new JSONObject(json);
        } catch(JSONException | IOException e) { throw new RuntimeException(e); }
    }

    public void deleteItemFiles(String uuid) {
        try {
            Files.delete(directory.resolve(uuid + ".html"));
            Files.delete(directory.resolve(uuid + ".json"));
        } catch(IOException e) { throw new RuntimeException(e); }
    }

    private static String replaceHtmlTextSizeTags(String html) {
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

    private static String insertHtmlBulletTags(Spanned spannedText, String html) {
        BulletSpan[] spans = spannedText.getSpans(0, spannedText.length(), BulletSpan.class);
        if(spans.length == 0) return html;
        HashSet<Integer> bulletLines = new HashSet<>();
        for(BulletSpan span : spans) {
            int line = 0;
            for(int c = 0; c < spannedText.getSpanStart(span); c++)
                if(spannedText.toString().charAt(c) == '\n')
                    line++;
            bulletLines.add(line);
        }
        StringBuilder result = new StringBuilder();
        int lineIndex = 0;
        int i = 0;
        while(i < html.length()) {
            if(html.startsWith("<p ", i) || html.startsWith("<p>", i)) {
                int openEnd = html.indexOf(">", i); int closeStart = html.indexOf("</p>", openEnd);
                boolean isBulleted = bulletLines.contains(lineIndex);
                result.append(html, i, openEnd + 1);
                if(isBulleted) {
                    result.append("<mybullet>"); result.append(html, openEnd + 1, closeStart); result.append("</mybullet>");
                }
                else
                    result.append(html, openEnd + 1, closeStart);
                result.append("</p>");
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

    private record MySizeMark(int size) { }
    private record MyBulletMark() { }
    private static final Html.TagHandler TAG_HANDLER = (boolean opening, String tag, Editable output, XMLReader xmlReader) -> {
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
                    output.setSpan(RichTextStyle.createBulletSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }
    };

}

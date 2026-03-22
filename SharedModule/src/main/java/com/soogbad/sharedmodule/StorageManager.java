package com.soogbad.sharedmodule;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@SuppressWarnings("ReadWriteStringCanBeUsed")
public class StorageManager<T extends Item> {

    // TODO: json file to store additional data of each note like title

    private static Path directory;
    private static final ArrayList<T> items = new ArrayList<>();

    public static ArrayList<T> getItems() { return items; }

    public static <T extends Item> T getItem(String uuid) {
        for(T item : items)
            if(item.UUID.equals(uuid))
                return item;
        return null;
    }

    public static void setDirectory(Path directory) {
        StorageManager.directory = directory;
        if(!Files.exists(directory)) {
            try { Files.createDirectory(directory); }
            catch(IOException e) { throw new RuntimeException(e); }
        }
    }

    public static void loadItems() {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.html")) {
            stream.forEach(path -> items.add(new T(path.getFileName().toString().replace(".html", ""), "Example Title")));
        } catch(IOException e) { throw new RuntimeException(e); }
    }

    public static String createItem() {
        String uuid = Utility.generateUniqueUUID(items);
        items.add(new T(uuid, "Example Title"));
        saveTextToHtmlFile(new SpannableString(""), directory.resolve(uuid + ".html"));
        return uuid;
    }

    public static <T extends Item> void deleteItem(T item) {
        items.remove(item);
        try { Files.delete(directory.resolve(item.UUID + ".html")); }
        catch(IOException e) { throw new RuntimeException(e); }
    }

    public static <T extends Item> void saveItem(T item) {
        saveTextToHtmlFile(item.Content, directory.resolve(item.UUID + ".html"));
    }
    private static void saveTextToHtmlFile(Spanned spannedText, Path filePath) {
        String html = Html.toHtml(spannedText, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        try { Files.write(filePath, html.getBytes(StandardCharsets.UTF_16)); }
        catch(IOException e) { throw new RuntimeException(e); }
    }

    public static SpannedString getItemContent(String itemUUID) { return loadTextFromHtmlFile(directory.resolve(itemUUID + ".html")); }
    private static SpannedString loadTextFromHtmlFile(Path filePath) {
        String html;
        try { html = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_16); }
        catch(IOException e) { throw new RuntimeException(e); }
        return new SpannedString(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
    }

}

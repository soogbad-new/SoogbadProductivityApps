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
public class StorageManager {

    // TODO: json file to store additional data of each item like title and options

    private static Path directory;
    private static final ArrayList<Item<?>> items = new ArrayList<>();

    public static ArrayList<Item<?>> getItems() { return items; }

    public static Item<?> getItem(String uuid) {
        for(Item<?> item : items)
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

    public static <T extends Item<O>, O extends Item.ItemOptions> void loadItems(Item.Creator<T, O> itemCreator) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.html")) {
            stream.forEach(path ->
                    items.add(itemCreator.create(path.getFileName().toString().replace(".html", ""), "Example Title", null)));
        } catch(IOException e) { throw new RuntimeException(e); }
    }

    public static <T extends Item<O>, O extends Item.ItemOptions> String createItem(Item.Creator<T, O> itemCreator) {
        String uuid = Utility.generateUniqueUUID(items);
        items.add(itemCreator.create(uuid, "Example Title", null));
        saveTextToHtmlFile(new SpannableString(""), directory.resolve(uuid + ".html"));
        return uuid;
    }

    public static void deleteItem(Item<?> item) {
        items.remove(item);
        try { Files.delete(directory.resolve(item.UUID + ".html")); }
        catch(IOException e) { throw new RuntimeException(e); }
    }

    public static void saveItem(Item<?> item) {
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

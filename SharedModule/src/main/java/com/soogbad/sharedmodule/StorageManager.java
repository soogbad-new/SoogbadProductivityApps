package com.soogbad.sharedmodule;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@SuppressWarnings("ReadWriteStringCanBeUsed")
public class StorageManager {

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

    public static <T extends Item<O>, O extends Item.ItemOptions> void loadItems(Item.Creator<T, O> itemCreator, Item.OptionsParser<O> optionsParser) {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory, "*.html")) {
            for(Path path : dirStream) {
                String uuid = path.getFileName().toString().replace(".html", "");
                JSONObject metadata = loadMetadata(uuid);
                String title = metadata.getString("title");
                O options = optionsParser.parse(metadata.getJSONObject("options"));
                items.add(itemCreator.create(uuid, title, options));
            }
        } catch(JSONException | IOException e) { throw new RuntimeException(e); }
    }

    public static <T extends Item<O>, O extends Item.ItemOptions> String createItem(Item.Creator<T, O> itemCreator, O defaultOptions) {
        String uuid = Utility.generateUniqueUUID(items);
        items.add(itemCreator.create(uuid, "Example Title", defaultOptions));
        saveTextToHtmlFile(new SpannableString(""), directory.resolve(uuid + ".html"));
        saveMetadata(uuid, "Example Title", defaultOptions);
        return uuid;
    }

    public static void deleteItem(Item<?> item) {
        items.remove(item);
        try {
            Files.delete(directory.resolve(item.UUID + ".html"));
            Files.delete(directory.resolve(item.UUID + ".json"));
        } catch(IOException e) { throw new RuntimeException(e); }
    }

    public static void saveItem(Item<?> item) {
        saveTextToHtmlFile(item.Content, directory.resolve(item.UUID + ".html"));
        saveMetadata(item.UUID, item.Title, item.Options);
    }

    public static SpannedString getItemContent(String itemUUID) {
        return loadTextFromHtmlFile(directory.resolve(itemUUID + ".html"));
    }

    private static void saveTextToHtmlFile(Spanned spannedText, Path filePath) {
        String html = Html.toHtml(spannedText, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        try { Files.write(filePath, html.getBytes(StandardCharsets.UTF_16)); }
        catch(IOException e) { throw new RuntimeException(e); }
    }

    private static SpannedString loadTextFromHtmlFile(Path filePath) {
        String html;
        try { html = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_16); }
        catch(IOException e) { throw new RuntimeException(e); }
        return new SpannedString(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
    }

    private static void saveMetadata(String uuid, String title, Item.ItemOptions options) {
        try {
            JSONObject metadata = new JSONObject();
            metadata.put("title", title);
            metadata.put("options", options.toJson());
            Files.write(directory.resolve(uuid + ".json"), metadata.toString().getBytes(StandardCharsets.UTF_16));
        } catch(JSONException | IOException e) { throw new RuntimeException(e); }
    }

    private static JSONObject loadMetadata(String uuid) {
        try {
            String json = new String(Files.readAllBytes(directory.resolve(uuid + ".json")), StandardCharsets.UTF_16);
            return new JSONObject(json);
        } catch(JSONException | IOException e) { throw new RuntimeException(e); }
    }

}

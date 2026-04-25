package com.soogbad.sharedmodule;

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
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.metadata.json")) {
            stream.forEach(path -> uuids.add(path.getFileName().toString().replace(".metadata.json", "")));
        } catch(IOException e) { throw new RuntimeException(e); }
        return uuids;
    }

    public void saveContent(String uuid, Spanned spannedText) {
        String json = RichTextSerializer.serialize(spannedText);
        try { Files.write(directory.resolve(uuid + ".content.json"), json.getBytes(StandardCharsets.UTF_16)); }
        catch(IOException e) { throw new RuntimeException(e); }
    }

    public SpannedString loadContent(String uuid) {
        String json;
        try { json = new String(Files.readAllBytes(directory.resolve(uuid + ".content.json")), StandardCharsets.UTF_16); }
        catch(IOException e) { throw new RuntimeException(e); }
        return RichTextSerializer.deserialize(json);
    }

    public void saveMetadata(String uuid, String title, Item.ItemOptions options) {
        try {
            JSONObject metadata = new JSONObject();
            metadata.put("title", title);
            metadata.put("options", options.toJson());
            Files.write(directory.resolve(uuid + ".metadata.json"), metadata.toString().getBytes(StandardCharsets.UTF_16));
        } catch(JSONException | IOException e) { throw new RuntimeException(e); }
    }

    public JSONObject loadMetadata(String uuid) {
        try {
            String json = new String(Files.readAllBytes(directory.resolve(uuid + ".metadata.json")), StandardCharsets.UTF_16);
            return new JSONObject(json);
        } catch(JSONException | IOException e) { throw new RuntimeException(e); }
    }

    public void deleteItemFiles(String uuid) {
        try {
            Files.delete(directory.resolve(uuid + ".content.json"));
            Files.delete(directory.resolve(uuid + ".metadata.json"));
        } catch(IOException e) { throw new RuntimeException(e); }
    }

}

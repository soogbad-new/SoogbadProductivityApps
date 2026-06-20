package com.soogbad.sharedmodule.core;

import android.text.Spanned;
import android.text.SpannedString;

import com.soogbad.sharedmodule.richtext.RichTextSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
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

    public ArrayList<String> loadItemUUIDs(boolean recycleBin) {
        ArrayList<String> uuids = new ArrayList<>();
        String glob = recycleBin ? "deleted_*.metadata.json" : "*.metadata.json";
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory, glob)) {
            stream.forEach(path -> {
                String filename = path.getFileName().toString();
                if(!recycleBin && filename.startsWith("deleted_")) return;
                String uuid = filename.replace(".metadata.json", "");
                uuids.add(recycleBin ? uuid.replace("deleted_", "") : uuid);
            });
        } catch(IOException e) { throw new RuntimeException(e); }
        return uuids;
    }

    public void saveContent(String uuid, Spanned spannedText) {
        String json = RichTextSerializer.serialize(spannedText);
        try { Files.write(directory.resolve(uuid + ".content.json"), json.getBytes(StandardCharsets.UTF_16)); }
        catch(IOException e) { throw new RuntimeException(e); }
    }

    public SpannedString loadContent(String uuid, boolean isInRecycleBin) {
        String json;
        try { json = new String(Files.readAllBytes(directory.resolve((isInRecycleBin ? "deleted_" : "") + uuid + ".content.json")), StandardCharsets.UTF_16); }
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

    public JSONObject loadMetadata(String uuid, boolean isInRecycleBin) {
        try {
            String json = new String(Files.readAllBytes(directory.resolve((isInRecycleBin ? "deleted_" : "") + uuid + ".metadata.json")), StandardCharsets.UTF_16);
            return new JSONObject(json);
        } catch(JSONException | IOException e) { throw new RuntimeException(e); }
    }

    // ===== Recycle Bin =====

    public void moveToRecycleBin(String uuid) {
        try {
            Files.move(directory.resolve(uuid + ".content.json"), directory.resolve("deleted_" + uuid + ".content.json"));
            Files.move(directory.resolve(uuid + ".metadata.json"), directory.resolve("deleted_" + uuid + ".metadata.json"));
            Files.setLastModifiedTime(directory.resolve("deleted_" + uuid + ".metadata.json"), FileTime.fromMillis(System.currentTimeMillis()));
        } catch(IOException e) { throw new RuntimeException(e); }
    }

    public void restoreFromRecycleBin(String uuid) {
        try {
            Files.move(directory.resolve("deleted_" + uuid + ".content.json"), directory.resolve(uuid + ".content.json"));
            Files.move(directory.resolve("deleted_" + uuid + ".metadata.json"), directory.resolve(uuid + ".metadata.json"));
        } catch(IOException e) { throw new RuntimeException(e); }
    }

    public void permanentlyDeleteFromRecycleBin(String uuid) {
        try {
            Files.deleteIfExists(directory.resolve("deleted_" + uuid + ".content.json"));
            Files.deleteIfExists(directory.resolve("deleted_" + uuid + ".metadata.json"));
        } catch(IOException e) { throw new RuntimeException(e); }
    }

    public void emptyRecycleBin() {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "deleted_*")) {
            for(Path path : stream)
                Files.delete(path);
        } catch(IOException e) { throw new RuntimeException(e); }
    }
    
    public long getRecycleBinItemDeletionTime(String uuid) {
        try { return Files.getLastModifiedTime(directory.resolve("deleted_" + uuid + ".metadata.json")).toMillis(); }
        catch(IOException e) { throw new RuntimeException(e); }
    }

}

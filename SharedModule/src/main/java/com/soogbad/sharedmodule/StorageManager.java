package com.soogbad.sharedmodule;

import android.text.Html;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

public class StorageManager {

    private final Path directory;
    private final ArrayList<String> files = new ArrayList<>();

    public StorageManager(Path directory) {
        this.directory = directory;
        if(!Files.exists(directory)) {
            try { Files.createDirectory(directory); }
            catch(IOException e) { throw new RuntimeException(e); }
        }
        loadItems();
    }
    private void loadItems() {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.html")) {
            stream.forEach(path -> files.add(path.getFileName().toString()));
        } catch(IOException e) { throw new RuntimeException(e); }
    }

    public void createItem() {
        String filename = null;
        while(filename == null || files.contains(filename))
            filename = UUID.randomUUID().toString() + ".html";
        try { Files.createFile(directory.resolve(filename)); }
        catch(IOException e) { throw new RuntimeException(e); }
        files.add(filename);
    }

    private void test() {
        //html = Html.fromHtml(spannedText, Html.FROM_HTML_MODE_COMPACT);
        //spannedText = Html.toHtml(html, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
    }

}

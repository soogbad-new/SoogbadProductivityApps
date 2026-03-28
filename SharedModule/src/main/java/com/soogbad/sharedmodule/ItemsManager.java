package com.soogbad.sharedmodule;

import android.text.SpannableString;
import android.text.SpannedString;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemsManager<T extends Item<O>, O extends Item.ItemOptions> {

    private final ArrayList<T> items = new ArrayList<>();
    public ArrayList<T> getItems() { return items; }

    private final StorageManager storageManager;
    private final Item.Creator<T, O> itemCreator;
    private final Item.OptionsParser<O> optionsParser;

    private static ItemsManager<?, ?> instance;
    public static ItemsManager<?, ?> getInstance() { return instance; }

    public ItemsManager(StorageManager storageManager, Item.Creator<T, O> itemCreator, Item.OptionsParser<O> optionsParser) {
        this.storageManager = storageManager; this.itemCreator = itemCreator; this.optionsParser = optionsParser;
        instance = this;
    }

    public void loadItems() {
        for(String uuid : storageManager.loadItemUUIDs()) {
            try {
                JSONObject metadata = storageManager.loadMetadataFromJsonFile(uuid);
                String title = metadata.getString("title");
                O options = optionsParser.parse(metadata.getJSONObject("options"));
                items.add(itemCreator.create(uuid, title, options));
            } catch(JSONException e) { throw new RuntimeException(e); }
        }
    }

    public void loadItemContent(Item<?> item) {
        item.Content = storageManager.loadContentFromHtmlFile(item.UUID);
    }

    public void saveItemContent(Item<?> item, SpannedString content) {
        item.Content = content;
        storageManager.saveContentToHtmlFile(item.UUID, item.Content);
    }

    public void saveItemMetadata(T item, String title, O options) {
        item.Title = title; item.Options = options;
        storageManager.saveMetadataToJsonFile(item.UUID, item.Title, item.Options);
    }

    public String createItem(O defaultOptions) {
        String uuid = Utility.generateUniqueUUID(items);
        items.add(itemCreator.create(uuid, "Example Title", defaultOptions));
        storageManager.saveContentToHtmlFile(uuid, new SpannableString(""));
        storageManager.saveMetadataToJsonFile(uuid, "Example Title", defaultOptions);
        return uuid;
    }

    public void deleteItem(Item<?> item) {
        items.removeIf((i) -> i.UUID.equals(item.UUID));
        storageManager.deleteItemFiles(item.UUID);
    }

}

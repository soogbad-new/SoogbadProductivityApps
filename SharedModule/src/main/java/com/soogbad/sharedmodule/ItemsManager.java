package com.soogbad.sharedmodule;

import android.text.SpannableString;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemsManager<T extends Item<O>, O extends Item.ItemOptions> {

    private final ArrayList<T> items = new ArrayList<>();
    public ArrayList<T> getItems() { return items; }

    private final StorageManager storageManager;
    private final Item.Creator<T, O> itemCreator;
    private final Item.OptionsParser<O> optionsParser;

    public ItemsManager(StorageManager storageManager, Item.Creator<T, O> itemCreator, Item.OptionsParser<O> optionsParser) {
        this.storageManager = storageManager; this.itemCreator = itemCreator; this.optionsParser = optionsParser;
    }

    public void loadItems() {
        for(String uuid : storageManager.loadItemUUIDs()) {
            try {
                JSONObject metadata = storageManager.loadMetadata(uuid);
                String title = metadata.getString("title");
                O options = optionsParser.parse(metadata.getJSONObject("options"));
                items.add(itemCreator.create(uuid, title, options));
            } catch(JSONException e) { throw new RuntimeException(e); }
        }
    }

    public void loadItemContent(Item<?> item) { item.Content = storageManager.loadContent(item.UUID); }
    public void saveItemContent(Item<?> item) { storageManager.saveContent(item.UUID, item.Content); }
    public void saveItemTitle(Item<?> item) { storageManager.saveMetadata(item.UUID, item.Title, item.Options); }
    public void saveItemOptions(T item) { storageManager.saveMetadata(item.UUID, item.Title, item.Options); }

    public String createItem(O defaultOptions) {
        String uuid = Utility.generateUniqueUUID(items);
        items.add(itemCreator.create(uuid, "", defaultOptions));
        storageManager.saveContent(uuid, new SpannableString(""));
        storageManager.saveMetadata(uuid, "", defaultOptions);
        return uuid;
    }

    public void deleteItem(Item<?> item) {
        items.removeIf(i -> i.UUID.equals(item.UUID));
        storageManager.deleteItemFiles(item.UUID);
    }

    public T getItem(String uuid) {
        for(T item : items)
            if(item.UUID.equals(uuid))
                return item;
        return null;
    }

}

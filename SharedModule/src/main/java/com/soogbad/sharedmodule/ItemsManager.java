package com.soogbad.sharedmodule;

import android.text.SpannableString;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemsManager<T extends Item<O>, O extends Item.ItemOptions> {

    private final ArrayList<T> items = new ArrayList<>();
    public ArrayList<T> getItems() { return items; }
    public T getItem(String uuid) { return items.stream().filter(i -> i.UUID.equals(uuid)).findFirst().orElse(null); }

    private final ArrayList<T> recycleBinItems = new ArrayList<>();
    public ArrayList<T> getRecycleBinItems() { return recycleBinItems; }
    public T getRecycleBinItem(String uuid) { return recycleBinItems.stream().filter(i -> i.UUID.equals(uuid)).findFirst().orElse(null); }

    private final StorageManager storageManager;
    private final Item.Creator<T, O> itemCreator;
    private final Item.OptionsParser<O> optionsParser;

    public ItemsManager(StorageManager storageManager, Item.Creator<T, O> itemCreator, Item.OptionsParser<O> optionsParser) {
        this.storageManager = storageManager; this.itemCreator = itemCreator; this.optionsParser = optionsParser;
    }

    private T loadItemData(String uuid, boolean deleted) {
        try {
            JSONObject metadata = storageManager.loadMetadata(deleted ? "deleted_" + uuid : uuid);
            String title = metadata.getString("title");
            O options = optionsParser.parse(metadata.getJSONObject("options"));
            T item = itemCreator.create(uuid, title, options);
            if(deleted) item.DeletedAt = storageManager.getRecycleBinItemDeletionTime(uuid);
            return item;
        } catch(JSONException e) { throw new RuntimeException(e); }
    }

    public void loadItems() {
        for(String uuid : storageManager.loadItemUUIDs(false))
            items.add(loadItemData(uuid, false));
    }
    public void loadRecycleBinItems() {
        storageManager.cleanExpiredRecycleBinItems();
        for(String uuid : storageManager.loadItemUUIDs(true))
            recycleBinItems.add(loadItemData(uuid, true));
        recycleBinItems.sort((a, b) -> Long.compare(b.DeletedAt, a.DeletedAt));
    }

    public void loadItemContent(Item<?> item) { item.Content = storageManager.loadContent(item.UUID); }
    public void saveItemContent(Item<?> item) { storageManager.saveContent(item.UUID, item.Content); }
    public void saveItemMetadata(Item<?> item) { storageManager.saveMetadata(item.UUID, item.Title, item.Options); }

    public String createItem(O defaultOptions) {
        String uuid = Utility.generateUniqueUUID(items);
        items.add(itemCreator.create(uuid, "", defaultOptions));
        storageManager.saveContent(uuid, new SpannableString(""));
        storageManager.saveMetadata(uuid, "", defaultOptions);
        return uuid;
    }

    public void moveItemToRecycleBin(String uuid) {
        T item = getItem(uuid);
        items.removeIf(i -> i.UUID.equals(uuid));
        storageManager.moveToRecycleBin(uuid);
        item.DeletedAt = System.currentTimeMillis();
        recycleBinItems.add(0, item);
    }
    public void restoreRecycleBinItem(String uuid) {
        T item = getRecycleBinItem(uuid);
        recycleBinItems.removeIf(i -> i.UUID.equals(uuid));
        storageManager.restoreFromRecycleBin(uuid);
        item.DeletedAt = 0;
        items.add(0, item);
    }

    public void permanentlyDeleteRecycleBinItem(String uuid) {
        storageManager.permanentlyDeleteFromRecycleBin(uuid);
        recycleBinItems.removeIf(item -> item.UUID.equals(uuid));
    }
    public void emptyRecycleBin() {
        storageManager.emptyRecycleBin();
        recycleBinItems.clear();
    }

}

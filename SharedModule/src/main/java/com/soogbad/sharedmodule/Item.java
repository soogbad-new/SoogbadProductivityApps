package com.soogbad.sharedmodule;

import android.text.SpannedString;

public class Item {

    public Item(String UUID, String Title) {
        this.UUID = UUID;
        this.Title = Title;
    }

     public String UUID;
     public String Title;
     public SpannedString Content;
     
     public void loadContent() { Content = StorageManager.getItemContent(UUID); }

     public void save() {
         StorageManager.saveItem(this);
     }

     public void delete() { StorageManager.deleteItem(this); }

}

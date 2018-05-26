package com.lonchi.andrej.sfm;

import android.support.annotation.NonNull;

/**
 * Created by andre on 26.5.2018.
 */

public class ListItem implements Comparable<ListItem>{
    private String itemName;
    private String itemSize;
    private String itemType;

    public ListItem (String itemName, String itemSize, String itemType){
        this.itemName = itemName;
        this.itemSize = itemSize;
        this.itemType = itemType;
    }

    public String getItemName(){
        return itemName;
    }

    public String getItemSize(){
        return itemSize;
    }

    public String getItemType() {
        return itemType;
    }

    @Override
    public int compareTo(@NonNull ListItem listItem) {
        if(this.itemName != null)
            return this.itemName.toLowerCase().compareTo(listItem.getItemName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}

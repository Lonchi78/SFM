package com.lonchi.andrej.sfm;

/**
 * Created by andre on 26.5.2018.
 */

public class ListItem {
    private String itemName;
    private String itemSize;

    public ListItem (String itemName, String itemSize){
        this.itemName = itemName;
        this.itemSize = itemSize;
    }

    public String getItemName(){
        return itemName;
    }

    public String getItemSize(){
        return itemSize;
    }
}

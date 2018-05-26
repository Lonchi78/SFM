package com.lonchi.andrej.sfm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andre on 26.5.2018.
 */

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;

    //  Constructor
    public AdapterRecyclerView(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public AdapterRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //  Get custom view of Card (list_item.xml)
        View viewAdapter = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(viewAdapter);
    }

    @Override
    public void onBindViewHolder(AdapterRecyclerView.ViewHolder holder, int position) {
        //  Get specific ListItem from all ListItems
        ListItem listItem = listItems.get(position);

        //  Set values for specific ListItem
        holder.textviewName.setText(listItem.getItemName());
        holder.textViewSize.setText(listItem.getItemSize());
    }

    @Override
    public int getItemCount() {
        //  Get number of items
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //  Declaration of used UI elements in Card (list_item.xml)
        public TextView textviewName;
        public TextView textViewSize;


        public ViewHolder(View itemView) {
            super(itemView);

            //  Assigning UI elements
            textviewName = (TextView) itemView.findViewById(R.id.item_tv_name);
            textViewSize = (TextView) itemView.findViewById(R.id.item_tv_size);

        }

    }
}

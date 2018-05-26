package com.lonchi.andrej.sfm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        //  Declaration of used UI elements in Card (list_item.xml)
        public TextView textviewName;
        public TextView textViewSize;
        public CardView cardView;

        //  Handle selected items (onLongClicked)
        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        public ViewHolder(View itemView) {
            super(itemView);

            //  Assigning UI elements
            textviewName = (TextView) itemView.findViewById(R.id.item_tv_name);
            textViewSize = (TextView) itemView.findViewById(R.id.item_tv_size);
            cardView = (CardView) itemView.findViewById(R.id.card_item);

            //  Set click listeners
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "onClick", Toast.LENGTH_LONG).show();

        }

        @Override
        public boolean onLongClick(View view) {
            if (selectedItems.get(getAdapterPosition(), false)) {
                //  Item is already selected -> unselect item
                selectedItems.delete(getAdapterPosition());
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_bg));
            }
            else {
                //  Item is not selected -> select item
                selectedItems.put(getAdapterPosition(), true);
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_bg_highlight));
            }
            return true;
        }
    }
}

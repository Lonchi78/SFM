package com.lonchi.andrej.sfm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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
    private FragmentBrowser fragmentBrowser;

    //  Constructor
    public AdapterRecyclerView(List<ListItem> listItems, Context context, FragmentBrowser fragmentBrowser) {
        this.listItems = listItems;
        this.context = context;
        this.fragmentBrowser = fragmentBrowser;
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

        //  Get values
        String itemName = listItem.getItemName();
        String itemSize = listItem.getItemSize();
        String itemType = listItem.getItemType();

        //  Set UI with values of specific Item
        holder.textViewName.setText( itemName );
        if( itemType.equals("DIR") ){
            if( itemSize.equals("0") ){
                holder.textViewSize.setText( itemSize + " item" );
                holder.imageViewType.setImageResource(R.drawable.ic_folder_empty);
            } else if( itemSize.equals("1") ){
                holder.textViewSize.setText( itemSize + " item" );
                holder.imageViewType.setImageResource(R.drawable.ic_folder_fill);
            }else{
                holder.textViewSize.setText( itemSize + " items" );
                holder.imageViewType.setImageResource(R.drawable.ic_folder_fill);
            }
        }
        if( itemType.equals("FILE") ){
            if( itemSize.equals("0") || itemSize.equals("1") ){
                holder.textViewSize.setText( itemSize + " Byte" );
                holder.imageViewType.setImageResource(R.drawable.ic_file);
            }else{
                holder.textViewSize.setText( itemSize + " Bytes" );
                holder.imageViewType.setImageResource(R.drawable.ic_file);
            }
        }
    }

    @Override
    public int getItemCount() {
        //  Get number of items
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        //  Declaration of used UI elements in Card (list_item.xml)
        public TextView textViewName;
        public TextView textViewSize;
        public ImageView imageViewType;
        public CardView cardView;

        //  Handle selected items (onLongClicked)
        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        public ViewHolder(View itemView) {
            super(itemView);

            //  Assigning UI elements
            textViewName = (TextView) itemView.findViewById(R.id.item_tv_name);
            textViewSize = (TextView) itemView.findViewById(R.id.item_tv_size);
            imageViewType = (ImageView) itemView.findViewById(R.id.item_iv_type);
            cardView = (CardView) itemView.findViewById(R.id.card_item);

            //  Set click listeners
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //  Get info of selected item
            int position = getAdapterPosition();
            String fileType = listItems.get(position).getItemType();
            String fileName = listItems.get(position).getItemName();

            if( fileType.equals("DIR") ){
                fragmentBrowser.openDir( fileName );
            }else if ( fileType.equals("FILE") ){
                fragmentBrowser.openFile( fileName );
            }else{
                Toast.makeText(context, R.string.unknow_file, Toast.LENGTH_LONG).show();
            }
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

            //  TRUE -> without onClick
            //  FALSE -> accept also onClick
            return true;
        }
    }
}

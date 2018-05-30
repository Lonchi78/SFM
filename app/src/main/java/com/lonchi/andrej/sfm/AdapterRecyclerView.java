package com.lonchi.andrej.sfm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andre on 26.5.2018.
 */

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    //  CAB variables
    private ActionMode mActionMode;
    private boolean multiSelect = false;
    private ArrayList<ListItem> selectedItems = new ArrayList<>();

    //  RecyclerViewAdapter variables
    private List<ListItem> listItems;
    private Context context;
    private FragmentBrowser fragmentBrowser;

    //  CAB override methods
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //  Open CAB -> load menu
            multiSelect = true;
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //  Handle action buttons click
            if( item.getItemId() == R.id.cabDelete ){
                for (ListItem intItem : selectedItems) {
                    listItems.remove(intItem);
                }
                mode.finish();
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //  Close CAB -> clear and update
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

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

        //  Update item selected state
        holder.update(listItems.get(position));
    }

    @Override
    public int getItemCount() {
        //  Get number of items
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        //  Declaration of used UI elements in Card (list_item.xml)
        public TextView textViewName;
        public TextView textViewSize;
        public ImageView imageViewType;
        public CardView cardView;


        public ViewHolder(View itemView) {
            super(itemView);

            //  Assigning UI elements
            textViewName = (TextView) itemView.findViewById(R.id.item_tv_name);
            textViewSize = (TextView) itemView.findViewById(R.id.item_tv_size);
            imageViewType = (ImageView) itemView.findViewById(R.id.item_iv_type);
            cardView = (CardView) itemView.findViewById(R.id.card_item);
        }

        void selectItem(ListItem item) {
            // Click on Item while is CAB open

            if (selectedItems.contains(item)) {
                //  Item is selected yet -> unselect item

                if( selectedItems.size() == 1 ){
                    //  This was the last selected item -> close CAB
                    selectedItems.remove(item);
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_bg));
                    if( mActionMode != null ){
                        mActionMode.finish();
                    }

                }else{
                    //  CAB stays open
                    selectedItems.remove(item);
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_bg));
                }

            } else {
                //  Item is not selected yet -> select item
                selectedItems.add(item);
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_bg_highlight));
            }
        }

        void update(final ListItem value) {
            //  Correct Item highlight
            if (selectedItems.contains(value)) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_bg_highlight));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_bg));
            }

            //  Long click on Item
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //  Unable to select Parent Folder
                    if( listItems.get( getAdapterPosition() ).getItemType().equals("DIR") &&
                            listItems.get( getAdapterPosition() ).getItemName().equals("..") ){
                        return true;
                    }

                    //  Is CAB already open?
                    if( !multiSelect ){
                        //  First selected item -> open CAB
                        mActionMode = ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                    }
                    //  ELSE => CAB is already open -> just add item
                    selectItem(value);

                    return true;
                }
            });

            //  Normal click on Item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  Is CAB on?
                    if( multiSelect ){
                        //  Unable to select Parent Folder
                        if( listItems.get( getAdapterPosition() ).getItemType().equals("DIR") &&
                                listItems.get( getAdapterPosition() ).getItemName().equals("..") ){
                            return;
                        }

                        //  CAB on -> Let's select items
                        selectItem(value);

                    }else{
                        //  CAB of -> Open file / folder

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
                }
            });
        }
    }
}

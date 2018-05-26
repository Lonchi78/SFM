package com.lonchi.andrej.sfm;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andre on 26.5.2018.
 */

public class FragmentBrowser extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<ListItem> listItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View viewBrowser = inflater.inflate(R.layout.layout_browser ,null);

        recyclerView = viewBrowser.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listItems = new ArrayList<>();
        for (int i=0; i<10; i++){
            ListItem newItem = new ListItem(
                    "Filename" + (i+1),
                    ((i+1)*5) + "MB"
            );

            listItems.add(newItem);
        }

        adapter = new AdapterRecyclerView(listItems, getContext());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new ClickListenerRecyclerView(getContext(), recyclerView ,new ClickListenerRecyclerView.OnItemClickListener() {
                    public void onItemClick(View view, int position) {
                        // do whatever
                        Toast.makeText(getContext(), "onClick " + position, Toast.LENGTH_LONG).show();
                    }

                    public void onLongItemClick(View view, int position) {
                        // do whatever
                        Toast.makeText(getContext(), "onLongClick " + position, Toast.LENGTH_LONG).show();
                    }
                })
        );

        return viewBrowser;
    }
}
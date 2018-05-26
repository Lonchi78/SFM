package com.lonchi.andrej.sfm;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by andre on 26.5.2018.
 */

public class FragmentBrowser extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<ListItem> listItems;
    private List<ListItem> dirList = new ArrayList<>();
    private List<ListItem> fileList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View viewBrowser = inflater.inflate(R.layout.layout_browser ,null);

        //  Set Recyclerview
        recyclerView = viewBrowser.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*
        //  Fill Recyclerview with items
        listItems = new ArrayList<>();
        for (int i=0; i<10; i++){
            ListItem newItem = new ListItem(
                    "Filename" + (i+1),
                    ((i+1)*5) + "MB"
            );

            listItems.add(newItem);
        }
        */

        dirList = new ArrayList<>();
        fileList = new ArrayList<>();
        File currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        fill(currentDir);

        //  Set Adapter for Recyclerview
        adapter = new AdapterRecyclerView(dirList, getContext());
        recyclerView.setAdapter(adapter);

        return viewBrowser;
    }

    private void fill(File f) {
        //  Load all containing files
        File[] subFiles = f.listFiles();

        try{
            //  Subfile by subfile loop
            for(File ff: subFiles) {
                //  Subfile is directory (subfolder)
                if(ff.isDirectory()){
                    //  Count how many files containing this subfolder
                    int subFolderItemsCnt = 0;
                    File[] subFolderFiles = ff.listFiles();
                    if( subFolderFiles != null ){
                        subFolderItemsCnt = subFolderFiles.length;
                    }else{
                        subFolderItemsCnt = 0;
                    }

                    //  Create a new Item, type Directory (Folder)
                    dirList.add( new ListItem( ff.getName(), String.valueOf(subFolderItemsCnt), "DIR" ) );
                }
                //  Subfile is file
                else {
                    //  Create a new Item, type File
                    fileList.add( new ListItem(ff.getName(), String.valueOf( ff.length() ), "FILE" ) );
                }
            }
        }catch(Exception e) {
            //  Exception e
        }

        //  Sorting ArrayLists
        Collections.sort(dirList);
        Collections.sort(fileList);

        //  Connext ArrayLists to one
        dirList.addAll(fileList);
        if(!f.getName().equalsIgnoreCase("sdcard")){
            dirList.add(0,new ListItem("..","Parent Directory", "DIR" ) );
        }
    }

    /*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Item o = adapter.getItem(position);
        if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
            currentDir = new File(o.getPath());
            fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }
    private void onFileClick(Item o)
    {
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
    */
}

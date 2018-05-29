package com.lonchi.andrej.sfm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by andre on 26.5.2018.
 */

public class FragmentBrowser extends Fragment{

    private String currentDirPath;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private File currentDirFile;
    public boolean needRefresh;

    public List<ListItem> dirList = new ArrayList<>();
    public List<ListItem> fileList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View viewBrowser = inflater.inflate(R.layout.layout_browser ,null);
        //  Protects double filling of arrays at first startup
        needRefresh = false;

        //  Get arguments
        currentDirPath = Environment.getExternalStorageDirectory().getPath();
        Bundle bundleImport = this.getArguments();
        if (bundleImport != null) {
            currentDirPath = bundleImport.getString("dirName", currentDirPath);
        }

        //  Set Recyclerview
        recyclerView = viewBrowser.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //  Declare ArrayLists
        dirList = new ArrayList<>();
        fileList = new ArrayList<>();

        return viewBrowser;
    }

    @Override
    public void onResume() {
        super.onResume();
        //  Update current dir path
        ((MainActivity)getActivity()).updateCurrentDirPath(currentDirPath);
        //  Update list of items
        updateList();
    }

    @Override
    public void onPause() {
        super.onPause();
        needRefresh = true;
    }

    private void fillList() {
        //  Load all containing files
        File[] subFiles = currentDirFile.listFiles();

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

        //  Connect ArrayLists to one
        dirList.addAll(fileList);

        //  Add parent directory
        if(!currentDirFile.getName().equalsIgnoreCase("sdcard")){
            dirList.add(0,new ListItem("..","Parent Directory", "DIR" ) );
        }
    }

    public void updateList(){
        if( needRefresh ){
            //  Refresh list ( fragment was paused or action button was pressed )

            //  Clear and Fill arrays again
            dirList.clear();
            fileList.clear();
            fillList();
            adapter.notifyDataSetChanged();

            Toast.makeText(getContext(), "updateList / if", Toast.LENGTH_SHORT).show();
        }else{
            //  First startup of fragment
            currentDirFile = new File( currentDirPath );
            fillList();

            //  Set Adapter for Recyclerview
            adapter = new AdapterRecyclerView(dirList, getContext(), FragmentBrowser.this);
            recyclerView.setAdapter(adapter);

            Toast.makeText(getContext(), "updateList / else", Toast.LENGTH_SHORT).show();
        }

        //  Run animation
        animateList();
    }

    private void animateList(){
        Context contextRecyclerView = recyclerView.getContext();
        LayoutAnimationController controllerAnim = null;

        controllerAnim = AnimationUtils.loadLayoutAnimation(contextRecyclerView, R.anim.layout_anim_fall_down );
        recyclerView.setLayoutAnimation(controllerAnim);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public void openDir(String dirName){
        //  Move to another directory
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentBrowser fragmentBrowser = new FragmentBrowser();
        Bundle bundleExport = new Bundle();

        //  Make dirPath
        String nextDirPath;
        if( dirName.equals("..") ){
            //  Move UP in tree

            File tmp = new File(currentDirPath);
            nextDirPath = tmp.getParent();
            bundleExport.putString("dirName", nextDirPath);
            fragmentBrowser.setArguments(bundleExport);
            getActivity().getSupportFragmentManager().popBackStack();
            ft.replace(R.id.container, fragmentBrowser ).commit();

        }else{
            // Move DOWN in tree

            nextDirPath = currentDirPath + File.separator + dirName;
            bundleExport.putString("dirName", nextDirPath);
            fragmentBrowser.setArguments(bundleExport);
            ft.replace(R.id.container, fragmentBrowser ).commit();
        }
    }

    public void openFile(String fileName){
        //  Try to open selected file
        File selectedFile = new File(currentDirPath + File.separator + fileName);

        //  Get preferred app to open this type of file
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        String mime = null;
        try {
            mime = URLConnection.guessContentTypeFromStream(new FileInputStream(selectedFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if( mime == null ) {
            mime=URLConnection.guessContentTypeFromName(selectedFile.getName());
        }
        myIntent.setDataAndType(Uri.fromFile(selectedFile), mime);
        startActivity(myIntent);

    }

}

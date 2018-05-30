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
import android.util.Log;
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

    public boolean NEED_REFRESH, AB_SETTINGS, ROOT_DIR;

    public List<ListItem> dirList = new ArrayList<>();
    public List<ListItem> fileList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View viewBrowser = inflater.inflate(R.layout.layout_browser ,null);
        //  Protects double filling of arrays at first startup
        NEED_REFRESH = false;
        AB_SETTINGS = false;

        //  Get arguments
        currentDirPath = Environment.getExternalStorageDirectory().getPath();
        Bundle bundleImport = this.getArguments();
        if (bundleImport != null) {
            currentDirPath = bundleImport.getString("dirName", currentDirPath);
            ROOT_DIR = bundleImport.getBoolean("dirDefault", false);
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
        //  Update current dir path -> convert real path to abstract path
        ((MainActivity)getActivity()).updateCurrentDirPath( realPathToAbstractPath() );
        //  Update list of items
        updateList();
    }

    @Override
    public void onPause() {
        super.onPause();
        NEED_REFRESH = true;
    }

    private void fillList() {
        if (ROOT_DIR){
            //  Just one "Default Folder"
            fillListRoot();

        }else{
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
    }

    private void fillListRoot(){
        //  Make file (Root Dir)
        currentDirFile = new File( currentDirPath );

        //  Count how many files containing Root Dir
        int subitemsCnt = 0;
        File[] subfoldersOfRootDir = currentDirFile.listFiles();
        if( subfoldersOfRootDir != null ){
            subitemsCnt = subfoldersOfRootDir.length;
        }else{
            subitemsCnt = 0;
        }

        //  Create a new Item, type Directory (Folder)
        dirList.add( new ListItem( "Default Folder", String.valueOf(subitemsCnt), "DIR" ) );
    }

    public void updateList(){
        if( NEED_REFRESH ){
            //  Refresh list ( fragment was paused or action button was pressed )

            //  Clear and Fill arrays again
            dirList.clear();
            fileList.clear();
            fillList();
            adapter.notifyDataSetChanged();

        }else{
            //  First startup of fragment
            currentDirFile = new File( currentDirPath );
            fillList();

            //  Set Adapter for Recyclerview
            adapter = new AdapterRecyclerView(dirList, getContext(), FragmentBrowser.this);
            recyclerView.setAdapter(adapter);

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
        //  Convert abstract name of root dir to realpath name
        if( isDirRoot(dirName) ){
            dirName = "0";
        }

        //  Move to another directory
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentBrowser fragmentBrowser = new FragmentBrowser();
        Bundle bundleExport = new Bundle();

        //  Make dirPath
        String nextDirPath;
        if( dirName.equals("..") ){
            //  Move UP in tree

            if( isDirDefault() ){
                //  Current Dir is Default Dir -> next Dir will have just one folder (Default Dir)
                AB_SETTINGS = false;
                nextDirPath = Environment.getExternalStorageDirectory().getParent();
                bundleExport.putString("dirName", nextDirPath);
                bundleExport.putBoolean("dirDefault", true );
                fragmentBrowser.setArguments(bundleExport);
                getActivity().getSupportFragmentManager().popBackStack();
                ft.replace(R.id.container, fragmentBrowser ).commit();

            }else{
                //  Current Dir is not Default Dir
                File tmp = new File(currentDirPath);
                nextDirPath = tmp.getParent();
                bundleExport.putString("dirName", nextDirPath);
                bundleExport.putBoolean("dirDefault", false );
                fragmentBrowser.setArguments(bundleExport);
                getActivity().getSupportFragmentManager().popBackStack();
                ft.replace(R.id.container, fragmentBrowser ).commit();
            }

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

    private boolean isDirDefault(){
        if( AB_SETTINGS ){
            AB_SETTINGS = false;
            return true;
        }

        return currentDirPath.equals(Environment.getExternalStorageDirectory().getPath());
    }

    private boolean isDirRoot(String dirName){
        if( dirName.equals("Default Folder") ){
            if( currentDirPath.equals(Environment.getExternalStorageDirectory().getParent()) ){
                return true;
            }
        }

        return false;
    }

    private String realPathToAbstractPath(){
        //  Init variables
        String tmpPath = String.valueOf(currentDirPath);
        String rootRealPath = Environment.getExternalStorageDirectory().getParent();
        String defaultRealPath = Environment.getExternalStorageDirectory().getPath();
        String abstractPath;


        if( currentDirPath.equals(rootRealPath) ){
            //  = /storage/emulated
            abstractPath = tmpPath.replace( rootRealPath, "/" );

        }else {
            //  = /storage/emulated???

            if( currentDirPath.length() > defaultRealPath.length() ){
                // = /storage/emulated/0/xyz
                abstractPath = tmpPath.replace( defaultRealPath, "/Default Folder" );

            }else {
                // = /storage/emulated/0
                abstractPath = tmpPath.replace( defaultRealPath, "/Default Folder" );

            }
        }

        return abstractPath;
    }
}

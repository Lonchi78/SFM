package com.lonchi.andrej.sfm;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView tvCurrentDirPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Get UI elements
        tvCurrentDirPath = (TextView) findViewById(R.id.tv_current_dir);

        //  Set Toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);
        mainToolbar.setTitle(R.string.app_name_long);
        mainToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar_text));
        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //  Load Fragment
        loadFragment();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  Set menu for toolbar
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentBrowser fragment;
        //  Handle action buttons clicks
        switch (item.getItemId()) {
            case android.R.id.home:
                //  Back arrow, finish current fragment
                fragment = (FragmentBrowser) getSupportFragmentManager().findFragmentById(R.id.container);
                fragment.needRefresh = true;
                fragment.openDir("..");
                break;

            case R.id.abRefresh:
                //  Refresh List of Items
                fragment = (FragmentBrowser) getSupportFragmentManager().findFragmentById(R.id.container);
                fragment.needRefresh = true;
                fragment.updateList();
                break;

            case R.id.abSettings:
                //  Open Default Path
                loadFragment();
                break;

            default:
                break;
        }
        return true;
    }

    private void loadFragment(){
        //  Load Fragment
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentBrowser fragmentBrowser = new FragmentBrowser();
        //  TODO
        //  Iba SDCard ??
        Bundle bundleExport = new Bundle();
        bundleExport.putString("dirName", Environment.getExternalStorageDirectory().getPath());
        fragmentBrowser.setArguments(bundleExport);
        ft.replace(R.id.container, fragmentBrowser ).commit();
    }

    public void updateCurrentDirPath(String dirPath){
        tvCurrentDirPath.setText(dirPath);
    }
}

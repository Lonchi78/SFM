package com.lonchi.andrej.sfm;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  Set menu for toolbar
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //  Handle action buttons clicks
        switch (item.getItemId()) {
            case R.id.abRefresh:
                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                        .show();
                break;

            case R.id.abSettings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;

            default:
                break;
        }
        return true;
    }

    public void updateCurrentDirPath(String dirPath){
        tvCurrentDirPath.setText(dirPath);
    }
}

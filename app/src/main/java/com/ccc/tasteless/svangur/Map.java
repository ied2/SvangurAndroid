package com.ccc.tasteless.svangur;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

/**
 * Created by Tasteless on 17.1.2016.
 */
public class Map extends ActionBarActivity {

    public static Activity activity;
    public static double latitude;
    public static double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        Bundle extras = getIntent().getExtras();

//        if (extras != null) {
//            this.latitude = Double.parseDouble(extras.getString("latitude"));
//            this.longitude = Double.parseDouble(extras.getString("longitude"));
//        }

        activity = this;
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_map, new GmapFragment()).commit();
    }

    /**
     * react to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the menu_main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}





//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

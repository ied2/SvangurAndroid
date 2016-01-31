package com.example.tasteless.svangur;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

/**
 * Created by Tasteless on 17.1.2016.
 */
public class Map extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        double latitude = 0;
        double longitude = 0;

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            latitude = Double.parseDouble(extras.getString("latitude"));
            longitude = Double.parseDouble(extras.getString("longitude"));
        }

        Log.d("IED", "latitude: " + latitude);
        Log.d("IED", "latitude: " + longitude);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_map, new GmapFragment(this, latitude, longitude)).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

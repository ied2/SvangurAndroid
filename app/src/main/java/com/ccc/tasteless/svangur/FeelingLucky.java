package com.ccc.tasteless.svangur;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FeelingLucky extends ActionBarActivity {

    private JSONArray restaurants;
    private String address;
    private String phoneNumber;
    private String website;
    public static double distance;
    private String logo;
    public static double latitude;
    public static double longitude;
    private int random;
    private String types = "";
    public static String name; // Nafn á veitingastað

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feelinglucky);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.feelingLucky);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.mapLoading).setVisibility(View.VISIBLE);
                randomRestaurant();
            }
        });

        restaurants = MainActivity.restaurants;

        randomRestaurant();
    }

    private void randomRestaurant() {
        int max = restaurants.length();
        random = Utility.randInt(0, max);

        JSONObject item = null;
        try {
            item = (JSONObject)restaurants.get(random);
            name = item.getString("name");
            address = item.getString("address");
            phoneNumber = item.getString("phonenumber");
            website = item.getString("url");
            distance = Double.parseDouble(item.getString("distance"));
            logo = item.getString("logo").trim();
            latitude = Double.parseDouble(item.getString("horizontal"));
            longitude = Double.parseDouble(item.getString("vertical"));
            types = "";
            if(item.getString("pizza").equals("1")) types += "pizza, ";
            if(item.getString("hamburger").equals("1")) types += "hamburger, ";
            if(item.getString("sushi").equals("1")) types += "sushi, ";
            if(item.getString("seafood").equals("1")) types += "seafood, ";
            if(item.getString("steak").equals("1")) types += "steak, ";
            if(item.getString("indian").equals("1")) types += "indian, ";
            if(item.getString("italian").equals("1")) types += "italian, ";
            if(item.getString("asian").equals("1")) types += "asian, ";
            if(item.getString("fastfood").equals("1")) types += "fastfood, ";
            if(item.getString("fancy").equals("1")) types += "fancy, ";
            if(item.getString("healthy").equals("1")) types += "healthy, ";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Actionbar title set to name of the current restaurant displayed
        this.setTitle(name);

        // Distance
        TextView view1 = (TextView) findViewById(R.id.info_km);
        if (distance > 999) {
            distance = distance / 1000;
            view1.setText(String.format("%.1f km", distance));
        } else {
            view1.setText(distance + "m");
        }

        // Address
        TextView view2 = (TextView) findViewById(R.id.address);
        view2.setText(address);

        // Phone number
        TextView view3 = (TextView) findViewById(R.id.phoneNumber);
        view3.setText(phoneNumber);

        // Website
//        TextView view4 = (TextView)findViewById(R.id.website);
//        view4.setText(website);

        // Display image
        ImageView view5 = (ImageView)findViewById(R.id.info_image);
        Picasso.with(this)
                .load(logo)
                .into(view5);

        // Types
        TextView view6 = (TextView)findViewById(R.id.types);
        String str = Utility.removeLastChar(types);
        view6.setText(str);

        // Click Listener for website
        View btn_website = findViewById(R.id.button_website);
        btn_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://" + website;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        // Click Listener for phone number
        View button_phoneNumber = findViewById(R.id.button_phoneNumber);
        button_phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(FeelingLucky.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);
            }
        });

        // Generate image of google map
        Double latEiffelTower = latitude;
        Double lngEiffelTower = longitude;
        String url = "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower +
                "&zoom=12&size=400x400&sensor=false&markers=color:green%7Clabel:S%7C" + latEiffelTower + "," + lngEiffelTower +
                "&markers=color:red%7Clabel:C%7C" + MainActivity.latitude + "," + MainActivity.longitude + "";
        ImageView imageView = (ImageView)findViewById(R.id.mapImage);
        Picasso.with(this)
                .load(url)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        findViewById(R.id.mapLoading).setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        // Click listener for map
        View map = findViewById(R.id.mapImage);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeelingLucky.this, MapFL.class);
//                intent.putExtra("latitude", latitude);
//                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });

        // Click listener for address button
        View address = findViewById(R.id.button_address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeelingLucky.this, MapFL.class);
//                intent.putExtra("latitude", latitude);
//                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });
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









//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        return true;
//    }
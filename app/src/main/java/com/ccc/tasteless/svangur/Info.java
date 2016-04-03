package com.ccc.tasteless.svangur;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Info extends AppCompatActivity {

    public static double latitude;
    public static double longitude;
    public static String name; // Nafn á veitingastað
    public static double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);

        // Turn on loading icon for map
        findViewById(R.id.mapLoading).setVisibility(View.VISIBLE);

        // Back arrow enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); SLOOOW
//        onBackPressed();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String logo = extras.getString("logo");
            String address = extras.getString("address");
            final String phoneNumber = extras.getString("phoneNumber");
            final String website = extras.getString("website").trim();
            latitude = Double.parseDouble(extras.getString("latitude"));
            longitude = Double.parseDouble(extras.getString("longitude"));
            name = extras.getString("name");
            distance = Double.parseDouble(extras.getString("distance"));
            String types = extras.getString("types");

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
//            TextView view4 = (TextView)findViewById(R.id.website);
//            view4.setText(website);

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
                    String url = "http://"+website;
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
                    if (ActivityCompat.checkSelfPermission(Info.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
        }

        // Generate image of google map
        String latEiffelTower = String.valueOf(latitude);
        String lngEiffelTower = String.valueOf(longitude);
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
                Intent intent = new Intent(Info.this, Map.class);
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
                Intent intent = new Intent(Info.this, Map.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });
    }

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

//        Button button = (Button)findViewById(R.id.mapButton);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Info.this, Map.class);
//                intent.putExtra("latitude", latitude);
//                intent.putExtra("longitude", longitude);
//                startActivity(intent);
//            }
//        });

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//}
//

// Generate image of google map
//        String latEiffelTower = latitude;
//        String lngEiffelTower = longitude;
//        String url = "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=12&size=400x200&sensor=false&markers=color:blue%7Clabel:S%7C" + latEiffelTower + "," + lngEiffelTower + "&markers=color:red%7Clabel:C%7C" + MainActivity.latitude + "," + MainActivity.longitude + "";
//        new DownloadImageTask((ImageView) findViewById(R.id.mapImage))
//                .execute(url);

//private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//    ImageView bmImage;
//
//    public DownloadImageTask(ImageView bmImage) {
//        this.bmImage = bmImage;
//    }
//
//    protected Bitmap doInBackground(String... urls) {
//        String urldisplay = urls[0];
//        Bitmap mIcon11 = null;
//        try {
//            InputStream in = new java.net.URL(urldisplay).openStream();
//            mIcon11 = BitmapFactory.decodeStream(in);
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//            e.printStackTrace();
//        }
//        return mIcon11;
//    }
//
//    protected void onPostExecute(Bitmap result) {
//        bmImage.setImageBitmap(result);
//    }
//}
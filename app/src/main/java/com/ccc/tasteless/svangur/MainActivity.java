package com.ccc.tasteless.svangur;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SeekBar.OnSeekBarChangeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GridView grid; // Grid to display restaurants
    int km; // KM for seekbar
    public static JSONArray restaurants = null; // Array of restaurants
    public static boolean doSortByDistance = false; // Do we sort by distance?
    private ReOrder reorder = new ReOrder(this);
    private boolean download = true; // Do we download JSON?

    // Google Maps variables
    public static double latitude;
    public static double longitude;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private static final Object REQUEST_CHECK_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.feelingLucky);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(restaurants != null) {
                    Intent i = new Intent(MainActivity.this, FeelingLucky.class);
                    startActivity(i);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initializing seekbar
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        }

        createLocationRequest();
    }

    @Override
    protected void onStart() {
        Log.d("IED", "onStart");
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("IED", "OnResume");
        if (mGoogleApiClient.isConnected() /*&& !mRequestingLocationUpdates*/) { // Fix mRequestingLocationUpdates later
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        Log.d("IED", "onStop");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void createLocationRequest() {
        Log.d("IED", "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10*1000); // 10 sec, app will get location on 10 sec interval
        mLocationRequest.setFastestInterval(5*1000); // 5 sec, if location is available
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);


        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    (Integer) REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    // When connection to mGoogleApiClient is complete, onConnected is called as callback
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("IED", "onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // Get last known location if available
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            // Download all restaurants
            if(download) {downloadRestaurants();download = false;}
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }

        startLocationUpdates();
    }


    // Start getting location information every few sec
    protected void startLocationUpdates() {

        Log.d("IED", "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    // Stop location updates when app is running in background
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    // When new location is found by requestLocationUpdates in method startLocationUpdates
    // This is a callback from that
    @Override
    public void onLocationChanged(Location location) {
        // Download restaurants from server if we haven't
        if(restaurants == null) downloadRestaurants();
        Log.d("IED", "onLocationChanged");
        Log.d("IED", "Location: " + location);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Disconnection from mGoogleApiClient
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("IED", "onConnectionSuspended");
    }

    // Connection failed to mGoogleApiClient
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("IED", "onConnectionFailed");
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "codecubacompany@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

        return super.onOptionsItemSelected(item);
    }

    public void downloadRestaurants() {

        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... params) {
                try {
                    return downloadHTML();
                } catch (Exception e) {
                    Log.d("IED", e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                try {

                    restaurants = (JSONArray)(new JSONTokener(result).nextValue());

                    reorder.orderRestaurants();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private String downloadHTML() throws Exception {

        String latitude = String.valueOf(this.latitude);
        String longitude = String.valueOf(this.longitude);

        URL url = new URL("http://svangur1.herokuapp.com/getRestaurants?distance=100000000000000&latitude=" + latitude + "&longitude=" + longitude);

        InputStream is = url.openStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        String s = "";

        while((line = br.readLine())!= null) {
//            Log.d("IED", line);
            s += line;
        }
        return s;
    }

    // SeekBar

    private void updateDistance() {
        TextView kmPlacholder = (TextView)findViewById(R.id.km);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar);

        km = seekBar.getProgress();
        String k = String.valueOf(km);

        if(k.equals("11")) k = "20";

        kmPlacholder.setText(k + " km");
    }

//    SeekBar Code BEGINS HERE

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateDistance();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        try {
            // Download restaurants from server if we haven't
            if(restaurants == null) downloadRestaurants();
            else reorder.orderRestaurants();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Checkbox Code BEGINS HERE

    public void pizzaClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked On Pizza" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void hamburgerClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked On Hamburger" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void sushiClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked On Sushi" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void seafoodClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked On Seafood" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void steakClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked on Steak" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void indianClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked On Indian" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void italianClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked on Italian" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void asianClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked on Asian" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void fastFoodClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked on FastFood" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void fancyClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked on Fancy" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void healthyClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked on Healthy" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

//    public void everythingClick(View v) throws Exception {
//        //code to check if this checkbox is checked!
//        Switch checkBox = (Switch)v;
//        if(checkBox.isActivated()){
//            reorder.orderRestaurants();
//            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
//        }
//        reorder.orderRestaurants();
//    }

    public void everythingClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkbox_pizza = (CheckBox)findViewById(R.id.checkbox_pizza);
        CheckBox checkbox_hamburger = (CheckBox)findViewById(R.id.checkbox_hamburger);
        CheckBox checkbox_sushi = (CheckBox)findViewById(R.id.checkbox_sushi);
        CheckBox checkbox_seafood = (CheckBox)findViewById(R.id.checkbox_seafood);
        CheckBox checkbox_steak = (CheckBox)findViewById(R.id.checkbox_steak);
        CheckBox checkbox_indian = (CheckBox)findViewById(R.id.checkbox_indian);
        CheckBox checkbox_italian = (CheckBox)findViewById(R.id.checkbox_italian);
        CheckBox checkbox_asian = (CheckBox)findViewById(R.id.checkbox_asian);
        CheckBox checkbox_fastFood = (CheckBox)findViewById(R.id.checkbox_fastFood);
        CheckBox checkbox_fancy = (CheckBox)findViewById(R.id.checkbox_fancy);
        CheckBox checkbox_healthy = (CheckBox)findViewById(R.id.checkbox_healthy);

        Switch switchBox = (Switch)v;

        if(switchBox.isChecked()) {
            checkbox_pizza.setChecked(true);
            checkbox_hamburger.setChecked(true);
            checkbox_sushi.setChecked(true);
            checkbox_seafood.setChecked(true);
            checkbox_steak.setChecked(true);
            checkbox_indian.setChecked(true);
            checkbox_italian.setChecked(true);
            checkbox_asian.setChecked(true);
            checkbox_fastFood.setChecked(true);
            checkbox_fancy.setChecked(true);
            checkbox_healthy.setChecked(true);
            reorder.orderRestaurants();
        } else {
            checkbox_pizza.setChecked(false);
            checkbox_hamburger.setChecked(false);
            checkbox_sushi.setChecked(false);
            checkbox_seafood.setChecked(false);
            checkbox_steak.setChecked(false);
            checkbox_indian.setChecked(false);
            checkbox_italian.setChecked(false);
            checkbox_asian.setChecked(false);
            checkbox_fastFood.setChecked(false);
            checkbox_fancy.setChecked(false);
            checkbox_healthy.setChecked(false);
            reorder.orderRestaurants();
        }
    }

    public void orderByDistanceClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        Switch switchBox = (Switch)v;
        if(switchBox.isChecked()) {
            doSortByDistance = true;
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "Order By Distance", Toast.LENGTH_LONG).show();
        } else {
            doSortByDistance = false;
            reorder.orderRestaurants();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}







//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    public JSONArray sortByType(JSONArray jsonArray) {
//
//        CheckBox checkbox_pizza = (CheckBox)findViewById(R.id.checkbox_pizza);
//        CheckBox checkbox_hamburger = (CheckBox)findViewById(R.id.checkbox_hamburger);
//
//        JSONArray outputArray = new JSONArray();
//
//        for (int i = 0; i < jsonArray.length(); i++) {
//            try {
//
//                JSONObject item = (JSONObject)jsonArray.get(i);
//
//                if(checkbox_pizza.isChecked()) {
//                    if("1".equals(item.getString("pizza"))) {
//                        outputArray.put(jsonArray.getJSONObject(i));
//                        continue;
//                    }
//                }
//
//                if(checkbox_hamburger.isChecked()) {
//                    if("1".equals(item.getString("hamburger"))) {
//                        outputArray.put(jsonArray.getJSONObject(i));
//                        continue;
//                    }
//
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return outputArray;
//    }
//
//    private void populate(final String[] web, final String[] imageId, final String[] distance, final String[] address, final String[] phoneNumber, final String[] website) {
//
//        // Create grids with restaurants
//        CustomGrid adapter = new CustomGrid(MainActivity.this, distance, imageId);
//        grid=(GridView) findViewById(R.id.grid);
//        grid.setAdapter(adapter);
//        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Toast.makeText(MainActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
//                // Start new activity when a restaurant is clicked
//                Intent i = new Intent(MainActivity.this, Info.class);
//                i.putExtra("name", web[position]);
//                i.putExtra("logo", imageId[position]);
//                i.putExtra("address", address[position]);
//                i.putExtra("phoneNumber", phoneNumber[position]);
//                i.putExtra("website", website[position]);
//                startActivity(i);
//            }
//        });
//    }

//    // Pop up window asking to enable "Location"
//    private void showDialogGPS() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(false);
//        builder.setTitle("Location");
//        builder.setMessage("Do you want to turn on Location?");
//        builder.setInverseBackgroundForced(true);
//        builder.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                startActivity(
//                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//            }
//        });
//        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

package com.example.tasteless.svangur;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    GridView grid; // Grid to display restaurants
    int km; // KM for seekbar
    public static JSONArray restaurants; // Array of restaurants
    public static boolean doSortByDistance = false; // Do we sort by distance?
    public Location location = null;
    private ReOrder reorder = new ReOrder(this);
    private LocationRequest request;
    private LocationManager mlocManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackLocation();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayRestaurants();

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);
    }



    private void trackLocation() {
        request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5*60*1000)
                .setFastestInterval(60*1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Make sure "Location" is enabled
        mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!enabled) {
            showDialogGPS();
        }
    }

    // Pop up window asking to enable "Location"
    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Location");
        builder.setMessage("Do you want to turn on Location?");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayRestaurants() {

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

        String id = "99";
        String password = URLEncoder.encode("lj=?&?&kk", "UTF-8");

        URL url = new URL("http://svangur1.herokuapp.com/getRestaurants?distance=10&latitude=64.1532475&longitude=-22.0068169");

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

    // Seekbar

    private void updateDistance() {
        TextView kmPlacholder = (TextView)findViewById(R.id.km);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar);

        km = seekBar.getProgress();
        String k = String.valueOf(km);

        kmPlacholder.setText("Km: " + k);
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
        displayRestaurants();
    }

    //    Checkbox Code BEGINS HERE

    public void pizzaClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void hamburgerClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void sushiClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void seafoodClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void steakClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void indianClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void italianClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void asianClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void fastFoodClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void fancyClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

    public void healthyClick(View v) throws Exception {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
            reorder.orderRestaurants();
            Toast.makeText(MainActivity.this, "You Clicked at checkbox" , Toast.LENGTH_LONG).show();
        }
        reorder.orderRestaurants();
    }

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

        CheckBox checkBox = (CheckBox)v;

        if(checkBox.isChecked()) {
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
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()) {
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

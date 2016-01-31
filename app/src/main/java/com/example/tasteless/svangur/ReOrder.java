package com.example.tasteless.svangur;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tasteless on 17.1.2016.
 */
public class ReOrder {

    GridView grid;
    public Activity activity;

    public ReOrder(Activity _activity){

        this.activity = _activity;
    }

    public void orderRestaurants() throws Exception {

        try {

            JSONArray restaurants = MainActivity.restaurants;

        Sort sort = new Sort(activity);
        // Seekbar sort
        restaurants = sort.sortByKm(restaurants);


        // Distance sort
        if(MainActivity.doSortByDistance) {
            restaurants = sort.sortByDistance(restaurants);
        }

        restaurants = sort.sortByType(restaurants);

            String[] title_list = new String[restaurants.length()];
            String[] logo_list = new String[restaurants.length()];
            String[] distance_list = new String[restaurants.length()];
            String[] address_list = new String[restaurants.length()];
            String[] phonenumber_list = new String[restaurants.length()];
            String[] website_list = new String[restaurants.length()];
            String[] latitude_list = new String[restaurants.length()];
            String[] longitude_list = new String[restaurants.length()];

        for(int i=0; i<restaurants.length(); i++) {
            JSONObject item = (JSONObject)restaurants.get(i);

            String title = item.getString("name");
            String address = item.getString("address");
            String phoneNumber = item.getString("phonenumber");
            String website = item.getString("url");
            String distance = item.getString("distance");
            String logo = item.getString("logo").trim();
            String latitude = item.getString("horizontal");
            String longitude = item.getString("vertical");

            latitude_list[i] = latitude;
            longitude_list[i] = longitude;
            title_list[i] = title;
            logo_list[i] = logo;
            distance_list[i] = distance;
            address_list[i] = address;
            phonenumber_list[i] = phoneNumber;
            website_list[i] = website;
        }

        populate(title_list, logo_list, distance_list, address_list, phonenumber_list, website_list, latitude_list, longitude_list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populate(final String[] web, final String[] imageId, final String[] distance, final String[] address, final String[] phoneNumber,
                          final String[] website, final String[] latitude, final String[] longitude) {

        // Create grids with restaurants
        CustomGrid adapter = new CustomGrid(activity, distance, imageId);
        grid=(GridView) activity.findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(activity, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
                // Start new activity when a restaurant is clicked
                Intent i = new Intent(activity, Info.class);
                i.putExtra("name", web[position]);
                i.putExtra("logo", imageId[position]);
                i.putExtra("address", address[position]);
                i.putExtra("phoneNumber", phoneNumber[position]);
                i.putExtra("website", website[position]);
                i.putExtra("latitude", latitude[position]);
                i.putExtra("longitude", longitude[position]);
                activity.startActivity(i);
            }
        });
    }

}

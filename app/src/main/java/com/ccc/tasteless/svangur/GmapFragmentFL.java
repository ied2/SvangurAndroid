package com.ccc.tasteless.svangur;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class GmapFragmentFL extends Fragment implements OnMapReadyCallback {

    private Activity activity = MapFL.activity;
    private GoogleMap mMap;
    private LocationManager locationManager;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragments_gmaps, container, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.directions);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+latitude+","+longitude+"(Hamborgarabúlla Tómasar)&mode=b");
//                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=" + MainActivity.latitude + "," + MainActivity.longitude + "&daddr=" + latitude + "," + longitude + "&mode=driving");
//                Uri gmmIntentUri = Uri.parse("google.navigation:q="+longitude+","+latitude+"");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f(%s)", latitude, longitude, "Where the party is at");
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", FeelingLucky.latitude, FeelingLucky.longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney and move the camera

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.getUiSettings().setZoomGesturesEnabled(true);
//        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(FeelingLucky.latitude, FeelingLucky.longitude), 14));

        Marker marker = mMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon))
                .title(FeelingLucky.name)
//                .snippet(Double.toString(FeelingLucky.distance))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(FeelingLucky.latitude, FeelingLucky.longitude)));
        marker.showInfoWindow();
    }
}








//        LatLng sydney = new LatLng(64.1532475, -22.0068169);
//        mMap.addMarker(new MarkerOptions()
//                .position(sydney).title("Marker in Sydney")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_camera))
//                .anchor(0.0f, 1.0f));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
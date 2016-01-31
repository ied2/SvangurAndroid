package com.example.tasteless.svangur;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class GmapFragment extends Fragment implements OnMapReadyCallback {

    private Activity activity;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;


    public GmapFragment(Activity activity, double latitude, double longitude) {
        this.activity = activity;
        this.latitude = latitude;
        this.longitude = longitude;
    }

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
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.getUiSettings().setZoomGesturesEnabled(true);
//        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude, longitude), 14));

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.burger))
                .title("Hamborgarabúlla Tómasar")
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(latitude, longitude)));
    }
}








//        LatLng sydney = new LatLng(64.1532475, -22.0068169);
//        mMap.addMarker(new MarkerOptions()
//                .position(sydney).title("Marker in Sydney")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_camera))
//                .anchor(0.0f, 1.0f));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
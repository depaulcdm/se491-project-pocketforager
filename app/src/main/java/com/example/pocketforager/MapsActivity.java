package com.example.pocketforager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.example.pocketforager.location.LocationVolley;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pocketforager.location.Occurrence;
import com.example.pocketforager.model.Plant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.pocketforager.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final float zoomDefault = 15.0f;
    private static final int LOCATION_REQUEST = 111;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;
    private LocationVolley locationVolley;
    private ArrayList<String> Science_names = new ArrayList<>();
    private String TAG = "MAp Activity: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        locationVolley = new LocationVolley(this);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //PlantDetails plant = (PlantDetails) getIntent().get;

        Intent intent = getIntent();
        ArrayList<String> receivedList = intent.getStringArrayListExtra("Science_Names");

        if (receivedList != null) {
            for (String item : receivedList) {
                Log.d("ReceivedItem", item);
                Science_names.add(item);
            }
        }

        determineLocation();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomDefault));
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }




    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    Toast.makeText(this, "Location Permission not Granted", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        // Add a marker at current location
                        LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                        double lat0 = location.getLatitude();
                        double lon0 = location.getLongitude();
                        double delta = 0.1;
                        double lonMin = lon0 - delta;
                        double lonMax = lon0 + delta;
                        double latMin = lat0 - delta;
                        double latMax = lat0 + delta;

                        //mMap.addMarker(new MarkerOptions().position(origin).title("My Origin"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, zoomDefault));
                        for(String name: Science_names){
                            Log.d(TAG, "Fruit name: " + name);
                            locationVolley.getOccurrences(name,lonMin,lonMax,latMin,latMax,200,
                                    new LocationVolley.OccurrenceCallback() {
                                @Override
                                public void onSuccess(List<Occurrence> occurrences) {

                                    for (Occurrence o : occurrences) {

                                        LatLng pos = new LatLng(o.getLatitude(), o.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(pos));
                                    }

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat0, lon0), 10f));
                                }

                                @Override
                                public void onFailure(Exception e) {

                                    //Toast.makeText(this, "Failed to load occurrences", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "failed to add markers of plant location.");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(
                            e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}
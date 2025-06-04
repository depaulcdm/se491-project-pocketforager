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
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketforager.location.OccurencePlantaeLocationVolley;
import com.example.pocketforager.location.Occurrence;
import com.example.pocketforager.model.Plant;
import com.example.pocketforager.utils.MapPinHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.pocketforager.databinding.ActivityMapsBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
    private ArrayList<String> Science_names_details = new ArrayList<>();
    private ArrayList<String> Science_names_near = new ArrayList<>();
    private String TAG = "MAp Activity: ";
    private GBIFVolley gbifVolley = new GBIFVolley();
    private String common_name;

    private String urlImage = "";

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

        ArrayList<String> nearList = intent.getStringArrayListExtra("scienceNamesNear");

        String url = intent.getStringExtra("imageURL");
        String name = intent.getStringExtra("NAME");

        if(nearList != null && !nearList.isEmpty()){
            for (String item : nearList) {
                Log.d("ReceivedItem for Near", item);
                Science_names_near.add(item);
            }
        }
        if (receivedList != null && !receivedList.isEmpty()) {
            for (String item : receivedList) {
                Log.d("ReceivedItem fr Details", item);
                Science_names_details.add(item);
            }
        }
        if (url!= null && !url.isEmpty()){
            urlImage = url;
        }

        if(name != null && !name.isEmpty()){
            common_name = name;
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
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null; // Use default frame
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.pop_up_marker, null);
                LatLng position = marker.getPosition();

                TextView title = view.findViewById(R.id.plant_title);
                TextView snippet = view.findViewById(R.id.marker_latlon);
                ImageView image = view.findViewById(R.id.marker_image);


                if(common_name!= null && !common_name.isEmpty()){
                    title.setText(common_name);
                }


                if (urlImage != null && !urlImage.isEmpty()) {

                    Picasso.get().load(urlImage).placeholder(R.drawable.photo_box_border_rounded).into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            // image was found
                            image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });


                } else {
                    // if no url exists
//                    binding.tvNoPhoto.setVisibility(View.VISIBLE);
//                    binding.imagePlant.setImageDrawable(null);
                }
                title.setText(marker.getTitle());
                snippet.setText("Latitude: " + position.latitude + "\nLongitude: " + position.longitude);

                return view;
            }
        });

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


                        //mMap.addMarker(new MarkerOptions().position(origin).title("My Origin"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(origin));
                        if (Science_names_details.size() == 1) {
                            for (String name : Science_names_details) {
                                Log.d(TAG, "Fruit name: " + name);
                                gbifVolley.fetchOccurrences(name, 1000, this);
                            }
                        } else {
                            double km = .5;
                            OccurencePlantaeLocationVolley occurencePlantaeLocationVolley = new OccurencePlantaeLocationVolley();
                            occurencePlantaeLocationVolley.getPlantaeOccurrences(
                                    lat0,
                                    lon0,
                                    km,
                                    100,
                                    this
                                    );

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, zoomDefault));


                            CircleOptions circleOptions = new CircleOptions()
                                    .center(origin)
                                    .radius(km * 1500) // radius in meters
                                    .strokeColor(Color.BLUE)
                                    .strokeWidth(2f)
                                    .fillColor(0x220000FF);

                            mMap.addCircle(circleOptions);



                        }


                    })
                    .addOnFailureListener(this, e -> {
                        Log.e(TAG, "Error getting location: " + e.getMessage());
                        Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void addMarkers(ArrayList<LatLng> latlngs) {
        List<MarkerOptions> markers = MapPinHelper.generateMarkers(latlngs);
        for (MarkerOptions marker : markers) {
            mMap.addMarker(marker);
        }
    }




}
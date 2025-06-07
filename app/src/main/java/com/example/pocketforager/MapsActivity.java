package com.example.pocketforager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.pocketforager.data.PlantEntity;
import com.example.pocketforager.location.LocationVolley;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketforager.location.OccurencePlantaeLocationVolley;
import com.example.pocketforager.location.OccurrenceCuratedListvolley;
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
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final float zoomDefault = 15.0f;

    private float zoomOut = 10.0f;
    private static final int LOCATION_REQUEST = 111;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;
    private LocationVolley locationVolley;
    private ArrayList<String> Science_names_details = new ArrayList<>();
    private ArrayList<String> Science_names_near = new ArrayList<>();
    private String TAG = "MAp Activity: ";
    private GBIFVolley gbifVolley = new GBIFVolley();
    private String common_name;
    public ArrayList<Pair<LatLng,String>> plantsNearList = new ArrayList<>();

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
                TextView scientificName = view.findViewById(R.id.scientificname);
                double tolerance = 0.0001;



                if(common_name!= null && !common_name.isEmpty()){
                    title.setText(common_name);
                    scientificName.setText("");
                }

                if (plantsNearList != null && !plantsNearList.isEmpty()) {
                    List<PlantEntity> curatedPlants = new CuratedPlantList().PLANTS;

                    for (Pair<LatLng, String> entry : plantsNearList) {
                        if (isCloseEnough(position, entry.first, tolerance)) {
                            // Find the matching plant in curatedPlants by scientific name
                            String matchingUrl = "";
                            String commonName = entry.second;


                            for (PlantEntity plant : curatedPlants) {
                                Log.d(TAG, "getInfoContents: " + plant.getScientificName() + " vs " + entry.second);
                                if (plant.getScientificName().equals(entry.second)) {
                                    matchingUrl = plant.getImageURL();
                                    commonName = plant.getCommonName();
                                    scientificName.setText(plant.getScientificName());
                                    break;
                                }
                            }

                            title.setText(commonName);
                            if (!matchingUrl.isEmpty()) {
                                Log.d(TAG, "getInfoContents: "  + matchingUrl);
                                Picasso.get()
                                        .load(matchingUrl)
                                        .placeholder(R.drawable.not_available)
                                        .into(image);
                                image.setVisibility(View.VISIBLE);
                            } else {

                                image.setVisibility(View.GONE);
                            }

                            break;
                        }
                    }


                if(common_name!= null && !common_name.isEmpty()){
                    title.setText(common_name);
                }

//                            for (PlantEntity plant : new CuratedPlantList().PLANTS) {
//                                Log.d(TAG, "getInfoContents: " + plant.getScientificName() + " vs " + entry.second);
//                                if (plant.getScientificName().equals(entry.second)) {
//
//                                    urlImage = plant.getImageURL();
//                                    title.setText(plant.getCommonName());
//                                    break;
//                                }
//                            }

//                            title.setText(entry.second);
//                            if (urlImage == null || urlImage.isEmpty()) {
//                                urlImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/No_image_available.svg/640px-No_image_available.svg.png";
//                            }
//                            Picasso.get().load(urlImage).placeholder(R.drawable.photo_box_border_rounded).into(image);
//                            image.setVisibility(View.VISIBLE);
//                            break;
//                        }
//                    }



                if (urlImage != null && !urlImage.isEmpty()) {
                    Log.d(TAG, "getInfoContents: " + urlImage);

                    Picasso.get().load(urlImage).placeholder(R.drawable.not_available).into(image, new Callback() {
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
                    image.setVisibility(View.VISIBLE);
                    Log.d(TAG, "getInfoContents: " + "No image URL found");
                }
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


    public boolean isCloseEnough(LatLng markerPosition, LatLng plantPosition, double threshold) {
        return Math.abs(markerPosition.latitude - plantPosition.latitude) < threshold &&
                Math.abs(markerPosition.longitude - plantPosition.longitude) < threshold;
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
                                    Log.d(TAG, "Multiple fruit names found, fetching occurrences for all.");
                                    double km = 5;
// this method gets a curated list of edible plants
                                    OccurrenceCuratedListvolley occurrenceCuratedListvolley = new OccurrenceCuratedListvolley();
                                    occurrenceCuratedListvolley.getPlantaeOccurrences(
                                            lat0,
                                            lon0,
                                            km,
                                            100,
                                            this
                                    );
// This method gets all plants currently
//                            OccurencePlantaeLocationVolley occurencePlantaeLocationVolley = new OccurencePlantaeLocationVolley();
//                            occurencePlantaeLocationVolley.getPlantaeOccurrences(
//                                    lat0,
//                                    lon0,
//                                    km,
//                                    100,
//                                    this
//                                    );

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, zoomOut));


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

    public void nearUserMarkers(ArrayList<Pair<LatLng,String>> paired){
        ArrayList <LatLng> latlngs = new ArrayList<>();
        plantsNearList.addAll(paired);
        String name = "";
        for(Pair<LatLng,String> entry: paired){
            latlngs.add(entry.first);
            name = entry.second;
            System.out.println(name);
        }

        List<MarkerOptions> markers = MapPinHelper.generateMarkers(latlngs);
        for (MarkerOptions marker : markers) {
            mMap.addMarker(marker);
        }
    }


    public class CuratedPlantList {
        public final List<PlantEntity> PLANTS = Arrays.asList(
                new PlantEntity("Elderberry", "Sambucus spp.", "https://upload.wikimedia.org/wikipedia/commons/a/a9/Sambucus-berries.jpg", "", false),
                new PlantEntity("Jerusalem artichoke", "Helianthus tuberosus", "https://perenual.com/storage/species_image/3400_helianthus_tuberosus/regular/jerusalem-artichoke-flower-bright-yellow-small-sunflower.jpg", "", true),
                new PlantEntity("Serviceberry", "Amelanchier spp.", "https://herbalramble.wordpress.com/wp-content/uploads/2018/04/dscn5796-e1523516785715.jpg?w=1024&h=768", "", false),
                new PlantEntity("Mayapple", "Podophyllum peltatum L.", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Podophyllum_peltatum_Arkansas.jpg/1280px-Podophyllum_peltatum_Arkansas.jpg", "", false),
                new PlantEntity("Wild bergamot", "Monarda fistulosa L.", "https://perenual.com/storage/species_image/5242_monarda_fistulosa/regular/52232588250_bb9d806633_b.jpg", "", true),
                new PlantEntity("Black Huckleberry", "Gaylussacia baccata", "https://perenual.com/storage/species_image/8430_gaylussacia_baccata/regular/32047720371_d9bab0e6c7_b.jpg", "", true),
                new PlantEntity("Greenbrier", "Smilax pulverulenta", "https://wildfoods4wildlife.com/wp-content/uploads/2020/03/image1-1024x768.jpg", "", false),
                new PlantEntity("Muscadine Grape", "Vitis rotundifolia", "https://upload.wikimedia.org/wikipedia/commons/8/8b/Vitis_rotundifolia.jpg", "", false),
                new PlantEntity("Wild plum", "Prunus americana Marshall", "https://perenual.com/storage/species_image/6327_prunus_americana/regular/50368039726_6e3d13f4f8_b.jpg", "", true),
                new PlantEntity("Chicory", "Cichorium intybus L.", "https://perenual.com/storage/species_image/1879_cichorium_intybus/regular/52347380770_908f5f52e9_b.jpg", "", true),
                new PlantEntity("American Beech", "Fagus grandifolia Ehrh.", "https://mortonarb.org/app/uploads/2020/12/96574_ca_object_representations_media_23637_large-0x1920-c-default.jpg", "", false),
                new PlantEntity("American Hazelnut", "Corylus americana", "https://www.illinoiswildflowers.info/trees/photos/am_hazelnut1.jpg", "", false),
                new PlantEntity("Dandelion", "Taraxacum officinale Weber ex F.H.Wigg.", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/DandelionFlower.jpg/960px-DandelionFlower.jpg", "", false),
                new PlantEntity("Spicebush", "Lindera benzoin (L.) Blume", "https://perenual.com/storage/species_image/4916_lindera_benzoin/regular/28007412598_ced3351efa_b.jpg", "", true),
                new PlantEntity("Cattail", "Typha spp.", "https://cdn.mos.cms.futurecdn.net/aJDYpLLYV4JxPfBU6YSEZT.jpg", "", false),
                new PlantEntity("Scarlet strawberry", "Fragaria virginiana Duchesne", "https://perenual.com/storage/species_image/3029_fragaria_virginiana/regular/9000977033_fc93917ef1_b.jpg", "", true),
                new PlantEntity("Common Persimmon", "Diospyros virginiana", "https://perenual.com/storage/species_image/274_diospyros_virginiana/regular/49014714943_39e9d002d6_b.jpg", "American Persimmon, Eastern Persimmon", true),
                new PlantEntity("Daylily", "Hemerocallis fulva (L.) L.", "https://upload.wikimedia.org/wikipedia/commons/6/66/Hemerocallis_lilioasphodelus.jpg", "", false),
                new PlantEntity("Small White Leek", "Allium tricoccum Aiton", "https://perenual.com/storage/species_image/8892_allium_tricoccum/regular/52351847169_fed2da6e16_b.jpg", "Ramp, Wild Leek", true),
                new PlantEntity("American chestnut", "Castanea dentata", "https://perenual.com/storage/species_image/1710_castanea_dentata/regular/50331266137_77acd7616d_b.jpg", "Sweet Chestnut", true),
                new PlantEntity("Blackberry", "Rubus fruticosus 'Chester'", "https://perenual.com/storage/species_image/6954_rubus_fruticosus_chester/regular/autumn-fruit-blackberry-healthy-fresh-delicious.jpg", "", true),
                new PlantEntity("Black Raspberry", "Rubus occidentalis", "https://perenual.com/storage/species_image/8431_rubus_occidentalis/regular/35280453713_213b0ebab1_b.jpg", "", true),
                new PlantEntity("American Groundnut", "Apios americana", "https://perenual.com/storage/species_image/9022_apios_americana/regular/51577689362_678683c38e_b.jpg", "", true)
        );
    }


    public void openGoogleMaps (View view) {
        if (mMap != null) {
            LatLng currentLocation = mMap.getCameraPosition().target;
            String uri = "http://maps.google.com/maps?q=" + currentLocation.latitude + "," + currentLocation.longitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Map is not ready yet", Toast.LENGTH_SHORT).show();
        }
    }


}
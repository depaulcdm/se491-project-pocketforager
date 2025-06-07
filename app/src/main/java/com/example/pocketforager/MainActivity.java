package com.example.pocketforager;

import static androidx.core.content.ContentProviderCompat.requireContext;


import static com.example.pocketforager.GetPlantDataVolley.downloadSinglePlant;

import static java.security.AccessController.getContext;


import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.data.AppDatabase;
import com.example.pocketforager.data.PlantEntity;
import com.example.pocketforager.location.OccurencePlantaeLocationVolley;
import com.example.pocketforager.location.SearchByLocationFragment;
import com.example.pocketforager.model.Plant;


import java.util.Arrays;

import java.util.HashSet;

import java.util.List;
import com.example.pocketforager.databinding.ActivityMainBinding;
import com.example.pocketforager.utils.NearbyPlantFinder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ConnectivityManager connectivityManager;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private FusedLocationProviderClient fusedLocationClient;

    private TextView locationTextView;

    private ActivityMainBinding binding;
    private ArrayList<Plants> Plants = new ArrayList<>();
    private PlantAdapter mAdapter;

    private boolean isSearching = false;
    private RecyclerView searchResults;
    private PlantAdapter plantAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //GetPlantDataVolley.fetchAllEdiblePlants(getApplicationContext());


        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<PlantEntity> plants = db.plantDao().getAllEdiblePlants();
            for (PlantEntity p : plants) {
                Log.d("EDIBLE_PLANTS", p.commonName + " - " + p.scientificName);
            }
        });

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchResults = findViewById(R.id.searchResults);
        searchResults.setLayoutManager(new LinearLayoutManager(this));
        plantAdapter = new PlantAdapter(new ArrayList<>(), this, false);
        searchResults.setAdapter(plantAdapter);

        connectivityManager = getSystemService(ConnectivityManager.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            getLastLocation(locationData -> {
                if (locationData != null && !locationData.isEmpty()) {
                    double latitude = locationData.get(0);
                    double longitude = locationData.get(1);
                    Log.d("MainActivity", "Latitude: " + latitude + ", Longitude: " + longitude);
                }
            });
        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //downloadAllCuratedPlants(this);

        binding.progressBar.setVisibility(View.VISIBLE);
        GetPlantDataVolley.downloadPlants(this, "");
        binding.progressBar.setVisibility(View.GONE);

        //binding.searchResults.setLayoutManager(new LinearLayoutManager(this));
        //binding.searchResults.setAdapter(new PlantAdapter(plants, this, false));


    }

    public interface LocationCallback {
        void onLocationRetrieved(ArrayList<Double> locationData);
    }

    private void getLastLocation(LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            ArrayList<Double> locationData = new ArrayList<>();
                            locationData.add(latitude);
                            locationData.add(longitude);
                            Log.d("MainActivity", "Latitude: " + latitude + ", Longitude: " + longitude);
                            callback.onLocationRetrieved(locationData);
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @SuppressLint("NotifyDataSetChanged")
    public void acceptPlants(ArrayList<Plants> Plants) {
        this.Plants.clear();
        this.Plants.addAll(Plants);
        System.out.println("Plants: " + Plants.size());
        for (Plants plant : Plants) {
            System.out.println("ID: " + plant.getID());
            System.out.println("Common Name: " + plant.getCommonName());
            System.out.println("Scientific Name: " + plant.getScientificName());
            System.out.println("Other Name: " + plant.getOtherName());
            System.out.println("Image URL: " + plant.getImageURL());
            System.out.println("------------------");
        }

        if (!Plants.isEmpty()) {
            boolean isGrid = !isSearching;
            mAdapter = new PlantAdapter(Plants, this, isGrid);

            if (isGrid) {
                binding.searchResults.setLayoutManager(new GridLayoutManager(this, 3));
            } else {
                binding.searchResults.setLayoutManager(new LinearLayoutManager(this));
            }

            binding.searchResults.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            showAlertDialog("No Results", "No plants found. Please try again.");
        }
    }

    public void search(View view) {
        isSearching = true;
        String searchQuery = Objects.requireNonNull(binding.SearchTextBar.getText()).toString();
        if (searchQuery.isEmpty()) {
            return;
        }
        if (searchQuery.length() < 3) {
            showAlertDialog("Search string too short", "Please try a longer search string.");
            return;
        }

        if (!isNetworkAvailable()) {
            showAlertDialog("No Connection", "No network connection available. Cannot contact Art Institute API.");
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);


        GetPlantDataVolley.downloadPlants(this, searchQuery);
        //GetPlantDataVolley.downloadSinglePlant(this, curatedPlant);
        binding.textView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);

        // binding.SearchTextBar.setText("");


    }

    public void searchLocation(View view) {
        String searchQuery = Objects.requireNonNull(binding.SearchTextBar.getText()).toString();
        if (searchQuery.isEmpty()) {
            return;
        }
        if (searchQuery.length() < 3) {
            showAlertDialog("Search string too short", "Please try a longer search string.");
            return;
        }

        if (!isNetworkAvailable()) {
            showAlertDialog("No Connection", "No network connection available. Cannot contact Art Institute API.");
            return;
        }


    }

    public void useCurrentLocation(View view) {

        if (!isNetworkAvailable()) {
            showAlertDialog("No Connection", "No network connection available. Cannot Search without internet connection.");
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showAlertDialog("Permission Denied", "Location permission is required to use this feature.");
            return;
        }

        ArrayList<String> scienceNamesList = new ArrayList<>();

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("scienceNamesNear", scienceNamesList);
        startActivity(intent);
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    public void clearSearch(View v) {
        binding.SearchTextBar.setText("");

    }


    public void dismissKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    private boolean isNetworkAvailable() {
        Network currentNetwork = connectivityManager.getActiveNetwork();
        return currentNetwork != null;
    }

    public void openDetails(Plant modelPlant, boolean isFromNearbySearch) {

        Intent intent = new Intent(this, DetailsPageActivity.class);
        intent.putExtra(DetailsPageActivity.EXTRA_PLANT, modelPlant);
        intent.putExtra("fromNearby", isFromNearbySearch);
        startActivity(intent);
    }



    public void searchByLocation(View view) {
        String query = Objects.requireNonNull(binding.SearchTextBar.getText()).toString().trim();

        if (!query.contains(",") || query.length() < 5) {
            Toast.makeText(this, "Enter location in format: City, ST", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] parts = query.split(",");
        if (parts.length < 2) {
            Toast.makeText(this, "Invalid format. Use City, ST", Toast.LENGTH_SHORT).show();
            return;
        }

        String city = parts[0].trim();
        String state = parts[1].trim().toUpperCase();

        if (state.length() != 2) {
            Toast.makeText(this, "State must be 2 letters", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedQuery = city + ", " + state;

        NearbyPlantFinder.findNearbyPlants(formattedQuery, this, new NearbyPlantFinder.NearbyPlantCallback() {
            @Override
            public void onResult(List<PlantEntity> plantEntities) {
                Log.d("PocketForager", "NearbyPlantFinder returned " + plantEntities.size() + " results");
                if (plantEntities.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No edible plants found nearby.", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Plants> converted = new ArrayList<>();

                    for (PlantEntity entity : plantEntities) {
                        Log.d("PocketForager", "Found plant: " + entity.commonName + " (" + entity.scientificName + ")");

                        List<String> sciList = new ArrayList<>();
                        if (entity.scientificName != null && !entity.scientificName.isEmpty()) {
                            sciList.add(entity.scientificName);
                        }

                        List<String> otherList = new ArrayList<>();
                        if (entity.otherName != null && !entity.otherName.isEmpty()) {
                            otherList.add(entity.otherName);
                        }

                        Plants p = new Plants(
                                entity.id,
                                entity.commonName,
                                sciList,
                                otherList,
                                entity.imageUrl
                        );

                        converted.add(p);
                    }

                    Set<String> seenScientificNames = new HashSet<>();
                    ArrayList<Plants> uniquePlants = new ArrayList<>();

                    for (Plants plant : converted) {
                        List<String> sciNames = plant.getScientificName();
                        if (!sciNames.isEmpty() && seenScientificNames.add(sciNames.get(0))) {
                            uniquePlants.add(plant);
                        }
                    }

                    isSearching = true;
                    binding.textView.setVisibility(View.GONE);

                    mAdapter = new PlantAdapter(uniquePlants, MainActivity.this, false);
                    binding.searchResults.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    binding.searchResults.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                }
            }



            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

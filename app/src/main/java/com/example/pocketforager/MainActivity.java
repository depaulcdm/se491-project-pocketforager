package com.example.pocketforager;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketforager.model.Plant;
import java.util.List;
import com.example.pocketforager.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ConnectivityManager connectivityManager;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private FusedLocationProviderClient fusedLocationClient;

    private TextView locationTextView;

    private ActivityMainBinding binding;
    private ArrayList<Plants> Plants = new ArrayList<>();
    private PlantAdapter mAdapter;

    private boolean isSearching = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        connectivityManager  = getSystemService(ConnectivityManager.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            getLastLocation();
        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.progressBar.setVisibility(View.VISIBLE);
        GetPlantDataVolley.downloadPlants(this,"");
        binding.progressBar.setVisibility(View.GONE);


    }

    private void getLastLocation() {
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
                            Toast.makeText(MainActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
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
        getLastLocation();

        // Get the current location and use it for the search
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    public void clearSearch (View v){
        binding.SearchTextBar.setText("");

    }


    public void dismissKeyboard (View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    private boolean isNetworkAvailable() {
        Network currentNetwork = connectivityManager.getActiveNetwork();
        return currentNetwork != null;
    }

    public void openDetails(Plant modelPlant) {

        Intent intent = new Intent(this, DetailsPageActivity.class);
        intent.putExtra(DetailsPageActivity.EXTRA_PLANT, modelPlant);
        startActivity(intent);
    }
}
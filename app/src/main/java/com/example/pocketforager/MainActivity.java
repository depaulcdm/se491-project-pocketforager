package com.example.pocketforager;


import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketforager.PlantAdapter;
import com.example.pocketforager.model.Plant;
import java.util.List;

import com.example.pocketforager.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ConnectivityManager connectivityManager;

    private ActivityMainBinding binding;
    private ArrayList<Plants> Plants = new ArrayList<>();
    private PlantAdapter mAdapter;

    // for when clicking on the plant to view details page

    private RecyclerView rvPlants;
    private PlantAdapter adapter;
    private List<Plant> plantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        connectivityManager  = getSystemService(ConnectivityManager.class);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //GetPlantDataVolley.downloadPlants(this,"");


    }

    @SuppressLint("NotifyDataSetChanged")
    public void acceptPlants(ArrayList<Plants> Plants){
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

        if(!Plants.isEmpty()){
            mAdapter = new PlantAdapter(Plants,this);
            binding.searchResults.setAdapter(mAdapter);
            binding.searchResults.setLayoutManager(new LinearLayoutManager(this));
            mAdapter.notifyDataSetChanged();
        }



    }

    public void search(View view) {
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

        GetPlantDataVolley.downloadPlants(this, searchQuery);
        // binding.SearchTextBar.setText("");


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

    public void openDetails(Plants plant) {
        Intent intent = new Intent(this, DetailsPageActivity.class);
        intent.putExtra(DetailsPageActivity.EXTRA_PLANT, plant);
        startActivity(intent);
    }

}
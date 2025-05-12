package com.example.pocketforager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Response;
import com.example.pocketforager.Plants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlantDetailsActivity extends AppCompatActivity {

    private TextView tvCommonName, tvScientificName;
    private ImageView ivPlantImage;
    private static final String TAG = "PlantDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        tvCommonName = findViewById(R.id.tvCommonName);
        tvScientificName = findViewById(R.id.tvScientificName);
        ivPlantImage = findViewById(R.id.ivPlantImage);

        Plants plant = getIntent().getParcelableExtra("plant_data");

        if (plant != null) {
            tvCommonName.setText(plant.getCommonName());
            tvScientificName.setText(String.join(", ", plant.getScientificNames()));

            if (plant.getImageURL() != null && !plant.getImageURL().isEmpty()) {
                Picasso.get().load(plant.getImageURL()).into(ivPlantImage);
            }
        } else {
            Toast.makeText(this, "No plant data received", Toast.LENGTH_SHORT).show();
        }
    }


    // Accept result from the API
    public void acceptPlants(java.util.ArrayList<Plants> plantList) {
        if (!plantList.isEmpty()) {
            Plants plant = plantList.get(0);
            tvCommonName.setText(plant.getCommonName());
            tvScientificName.setText(plant.getScientificNames().toString());

            if (plant.getImageURL() != null && !plant.getImageURL().isEmpty()) {
                Picasso.get().load(plant.getImageURL()).into(ivPlantImage);
            }
        } else {
            Toast.makeText(this, "No plant data found", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.pocketforager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pocketforager.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<Plants> Plants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GetPlantDataVolley.downloadPlants(this,"");



    }

    public void acceptPlants(ArrayList<Plants> Plants){
        this.Plants.addAll(Plants);
        System.out.println("Plants: " + Plants.size());
        for (Plants plant : Plants) {
            System.out.println("ID: " + plant.getID());
            System.out.println("Common Name: " + plant.getCommonName());
            System.out.println("Scientific Name: " + plant.getScientificName());
            System.out.println("Other Name: " + plant.getOtherName());
            System.out.println("Image URL: " + plant.getImageURL());
        }



    }
}
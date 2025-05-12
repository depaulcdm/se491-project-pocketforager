package com.example.pocketforager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pocketforager.databinding.ActivityDetailsBinding;
import com.example.pocketforager.model.Plant;

public class DetailsPageActivity extends AppCompatActivity {
    public static final String EXTRA_PLANT = "extra_plant";

    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve a Plants, not model.Plant
        Plant plant = (Plant) getIntent().getSerializableExtra(EXTRA_PLANT);

        if (plant != null) {
            // Photo logic
            String url = plant.getImageURL();
            if (url != null && !url.isEmpty()) {
                binding.tvNoPhoto.setVisibility(View.GONE);
                // e.g. Glide.with(this).load(url).into(binding.imagePlantDetail);
            } else {
                binding.tvNoPhoto.setVisibility(View.VISIBLE);
            }

            // Populate text fields
            binding.tvPlantName.setText(plant.getCommonName());
            binding.tvScientificName.setText(plant.getScientificName());
            binding.tvOtherName.setText(plant.getOtherName().isEmpty() ? "—" : plant.getOtherName());
            //************* binding.tvEdible.setText(plant.isEdible() ? "Yes" : "No");
        }
    }
}

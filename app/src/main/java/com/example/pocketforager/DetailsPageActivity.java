package com.example.pocketforager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketforager.databinding.ActivityDetailsBinding;
import com.example.pocketforager.model.Plant;

public class DetailsPageActivity extends AppCompatActivity {
    public static final String EXTRA_PLANT = "extra_plant";
    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView ivPhoto = findViewById(R.id.imagePlant);
        TextView tvNoPhoto = findViewById(R.id.tvNoPhoto);
        TextView tvName = findViewById(R.id.tvPlantName);
        TextView tvSci = findViewById(R.id.tvScientificName);
        TextView tvOther = findViewById(R.id.tvOtherName);
        TextView tvEdible = findViewById(R.id.tvEdible);


        Intent intent = getIntent();
        Plant plant = (Plant) intent.getSerializableExtra(EXTRA_PLANT);

        if (plant != null) {
            // Photo
            if (plant.getImageUrl() != null && !plant.getImageUrl().isEmpty()) {
                tvNoPhoto.setVisibility(View.GONE);

            } else {
                tvNoPhoto.setVisibility(View.VISIBLE);
            }

            tvName.setText(plant.getCommonName());
            tvSci.setText(plant.getScientificName());
            tvOther.setText(plant.getOtherName());
            tvEdible.setText(plant.isEdible() ? "Yes" : "No");
        }
    }
}

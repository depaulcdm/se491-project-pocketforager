package com.example.pocketforager;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.widget.Toast;
import com.example.pocketforager.data.AppDatabase;
import com.example.pocketforager.data.PlantEntity;
import java.util.Date;
import android.util.Log;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;



import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pocketforager.databinding.ActivityDetailsBinding;
import com.example.pocketforager.model.Plant;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

public class DetailsPageActivity extends AppCompatActivity {
    public static final String EXTRA_PLANT = "extra_plant";

    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Plant plant = (Plant) getIntent().getSerializableExtra(EXTRA_PLANT);


        if (plant != null) {
            String url = plant.getImageURL();
            if (url != null && !url.isEmpty()) {

                // getting the photo with picasso
                binding.tvNoPhoto.setVisibility(View.GONE);
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.photo_box_border_rounded)
                        .into(binding.imagePlant, new Callback() {
                            @Override
                            public void onSuccess() {
                                // image was found
                                binding.tvNoPhoto.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                // if getting the photo didn't work
                                binding.tvNoPhoto.setVisibility(View.VISIBLE);
                                binding.imagePlant.setImageDrawable(null);
                            }
                        });


            } else {
                // if no url exists
                binding.tvNoPhoto.setVisibility(View.VISIBLE);
                binding.imagePlant.setImageDrawable(null);
            }

            // Fill in text fields

            binding.tvPlantName.setText(plant.getCommonName());
            binding.tvScientificName.setText(plant.getScientificName());
            binding.tvOtherName.setText(plant.getOtherName().isEmpty() ? "—" : plant.getOtherName());
            binding.btnLogVisit.setOnClickListener(view -> {
                if (plant == null) {
                    Snackbar.make(binding.getRoot(), "Plant not loaded", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                PlantEntity entity = new PlantEntity();
                entity.plantApiId = "api-id-not-used";
                entity.commonName = plant.getCommonName();
                entity.scientificName = plant.getScientificName();
                entity.location = getLocation();;
                entity.foundAt = new Date();

                new Thread(() -> {
                    try {
                        AppDatabase.getInstance(getApplicationContext())
                                .plantDao()
                                .insertPlant(entity);

                        runOnUiThread(() -> {
                            Snackbar snackbar = Snackbar.make(binding.getRoot(), "Visit logged successfully", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.primary));
                            snackbar.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                            snackbar.show();
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            Snackbar.make(binding.getRoot(), "Insert failed: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            });

            //************* binding.tvEdible.setText(plant.isEdible() ? "Yes" : "No");
        }
    }
}

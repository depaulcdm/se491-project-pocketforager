package com.example.pocketforager;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.data.PlantEntity;
import com.example.pocketforager.databinding.GridrecyclerviewBinding;
import com.example.pocketforager.databinding.PlantlistRecyclerviewBinding;

import java.util.ArrayList;
import android.text.TextUtils;
import java.util.List;
import com.squareup.picasso.Picasso;


public class PlantAdapter extends RecyclerView.Adapter<PlantListHolder> {



    private final ArrayList<Plants> plants;

    private final MainActivity mainActivity;

    private final boolean isGridLayout = false;

    public PlantAdapter(ArrayList<Plants> plants, MainActivity mainActivity, boolean isGrid) {
        this.plants = plants;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public PlantListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (isGridLayout) {
            GridrecyclerviewBinding binding = GridrecyclerviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new PlantListHolder(binding);
        } else {
            PlantlistRecyclerviewBinding binding = PlantlistRecyclerviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new PlantListHolder(binding);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull PlantListHolder holder, int position) {
        Plants plant = plants.get(position);

        if (isGridLayout && holder.gridBinding != null) {
            holder.gridBinding.gridTitle.setText(plant.getCommonName());
            String imageUrl = plant.getImageURL();
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.not_available)
                    .error(R.drawable.not_available)
                    .into(holder.gridBinding.gridThumbnail);
        } else if (holder.listBinding != null) {
            holder.listBinding.RecyclerTitle.setText(plant.getCommonName());
            holder.listBinding.recyclerScientificName.setText(plant.getScientificName().get(0));
            String imageUrl = plant.getImageURL();
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = "https://example.com/default_image.png"; // Fallback URL
            }
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.not_available)
                    .error(R.drawable.not_available)
                    .into(holder.listBinding.thumbnail);
        }

        // Adding click listener
        if (holder.listBinding != null || holder.gridBinding != null) {
            holder.itemView.setOnClickListener(v -> {
                com.example.pocketforager.model.Plant p = new com.example.pocketforager.model.Plant();
                p.setID(plant.getID());
                p.setCommonName(plant.getCommonName());

                List<String> sciList = plant.getScientificName();
                String sci = (sciList != null && !sciList.isEmpty()) ? sciList.get(0) : "—";
                p.setScientificName(sci);

                List<String> otherList = plant.getOtherName();
                String other = (otherList != null && !otherList.isEmpty()) ? TextUtils.join(", ", otherList) : "—";
                p.setOtherName(other);

                p.setImageURL(plant.getImageURL());

                mainActivity.openDetails(p, true); //added a flag for search by location

            });
        }
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }


    public void updatePlants(ArrayList<Plants> newPlants) {
        this.Plants = newPlants;
        this.Plants.clear();
        this.Plants.addAll(newPlants);
        notifyDataSetChanged();
    }

}


package com.example.pocketforager;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.databinding.PlantentryBinding;

import java.util.ArrayList;
import android.text.TextUtils;
import java.util.List;


public class PlantAdapter extends RecyclerView.Adapter<PlantListHolder> {


    private final ArrayList<Plants> Plants;
    private final MainActivity mainActivity;

    public PlantAdapter(ArrayList<com.example.pocketforager.Plants> plants, MainActivity mainActivity) {
        Plants = plants;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public PlantListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlantentryBinding binding = PlantentryBinding.inflate(LayoutInflater.from
                (parent.getContext()),parent,false);

        //binding.getRoot().setOnClickListener(mainActivity);
        return new PlantListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantListHolder holder, int position) {

        Plants plant = Plants.get(position);
        holder.binding.plantName.setText(plant.getCommonName());

        // adding this for clicking on plant to show details
        holder.binding.getRoot().setOnClickListener(v -> {
            // converting Plants to Plant
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
            //********************************** p.setEdible(plant.isEdible());

            mainActivity.openDetails(p);
        });

    }

    @Override
    public int getItemCount() {
        return Plants.size();
    }
}


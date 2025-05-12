package com.example.pocketforager;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.databinding.PlantentryBinding;

import java.util.ArrayList;

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
        // click on plant goes to main activity
        holder.binding.getRoot().setOnClickListener(v -> mainActivity.openDetails(plant));

    }

    @Override
    public int getItemCount() {
        return Plants.size();
    }
}


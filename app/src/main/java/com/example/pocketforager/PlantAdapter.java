package com.example.pocketforager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.databinding.PlantentryBinding;

import java.util.ArrayList;

public class PlantAdapter extends RecyclerView.Adapter<PlantListHolder> {

    private final ArrayList<Plants> Plants;
    private final Context context;

    public PlantAdapter(ArrayList<Plants> plants, Context context) {
        this.Plants = plants;
        this.context = context;
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
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(context, PlantDetailsActivity.class);
            intent.putExtra("plant_data", plant);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return Plants.size();
    }
}

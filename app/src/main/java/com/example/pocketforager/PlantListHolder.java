package com.example.pocketforager;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.databinding.PlantentryBinding;

public class PlantListHolder extends RecyclerView.ViewHolder{
    PlantentryBinding binding;

    PlantListHolder(PlantentryBinding binding){
        super((binding.getRoot()));
        this.binding = binding;
    }
}

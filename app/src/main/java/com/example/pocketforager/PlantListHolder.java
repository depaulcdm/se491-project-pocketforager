package com.example.pocketforager;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.databinding.GridrecyclerviewBinding;
import com.example.pocketforager.databinding.PlantlistRecyclerviewBinding;

public class PlantListHolder extends RecyclerView.ViewHolder{
   PlantlistRecyclerviewBinding binding;
    public ImageView thumbnail;
    public TextView title;
    public TextView scientificName;

    public PlantlistRecyclerviewBinding listBinding;
    public GridrecyclerviewBinding gridBinding;

    public PlantListHolder(PlantlistRecyclerviewBinding binding) {
        super(binding.getRoot());
        this.listBinding = binding;
    }

    public PlantListHolder(GridrecyclerviewBinding binding) {
        super(binding.getRoot());
        this.gridBinding = binding;
    }
}


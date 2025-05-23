package com.example.pocketforager;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.databinding.PlantlistRecyclerviewBinding;

public class PlantListHolder extends RecyclerView.ViewHolder{
   PlantlistRecyclerviewBinding binding;
    public ImageView thumbnail;
    public TextView title;
    public TextView scientificName;

    PlantListHolder(PlantlistRecyclerviewBinding binding){
        super((binding.getRoot()));
        this.binding = binding;
        thumbnail = binding.thumbnail;
        title = binding.RecyclerTitle;
        scientificName = binding.recyclerScientificName;

    }
}

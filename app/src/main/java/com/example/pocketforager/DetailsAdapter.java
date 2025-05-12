package com.example.pocketforager;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.databinding.PlantentryBinding;
import com.example.pocketforager.model.Plant;

import java.util.List;

public class DetailsAdapter
        extends RecyclerView.Adapter<DetailsAdapter.DetailsHolder> {

    private final List<Plants> rawPlants;
    private final MainActivity mainActivity;

    public DetailsAdapter(List<Plants> rawPlants, MainActivity mainActivity) {
        this.rawPlants    = rawPlants;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public DetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlantentryBinding binding = PlantentryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new DetailsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsHolder holder, int position) {
        Plants oldPlant = rawPlants.get(position);
        holder.binding.plantName.setText(oldPlant.getCommonName());

        holder.binding.getRoot().setOnClickListener(v -> {
            Plant p = new Plant();
            p.setID(oldPlant.getID());
            p.setCommonName(oldPlant.getCommonName());

            List<String> sciList = oldPlant.getScientificName();
            String sci = (sciList != null && !sciList.isEmpty())
                    ? sciList.get(0)
                    : "—";
            p.setScientificName(sci);

            List<String> otherList = oldPlant.getOtherName();
            String other = (otherList != null && !otherList.isEmpty())
                    ? TextUtils.join(", ", otherList)
                    : "—";
            p.setOtherName(other);

            p.setImageURL(oldPlant.getImageURL());
            //*********************** p.setEdible(oldPlant.isEdible());

            Intent intent = new Intent(mainActivity, DetailsPageActivity.class);
            intent.putExtra(DetailsPageActivity.EXTRA_PLANT, p);
            mainActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return rawPlants.size();
    }

    static class DetailsHolder extends RecyclerView.ViewHolder {
        final PlantentryBinding binding;
        DetailsHolder(@NonNull PlantentryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

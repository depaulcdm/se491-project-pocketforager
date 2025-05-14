package com.example.pocketforager;

import com.example.pocketforager.model.Plant;

public class DetailsMapper {
    public static Details fromPlant(Plant plant) {

        String sci = (plant.getScientificName() == null || plant.getScientificName().isEmpty()) ? "—" : plant.getScientificName();

        String other = (plant.getOtherName() == null || plant.getOtherName().isEmpty()) ? "—" : plant.getOtherName();

        String edible = plant.isEdible() ? "Yes" : "No";

        boolean site = (plant.getImageURL() == null || plant.getImageURL().isEmpty());

        return new Details(plant.getCommonName(), sci, other, edible, site);
    }
}

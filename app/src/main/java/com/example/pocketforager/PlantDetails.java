package com.example.pocketforager;

import java.io.Serializable;
import java.util.List;

public class PlantDetails implements Serializable {
    private final String photo_url;
    private final String common_name;
    private final List<String> scientific_name;
    private final List<String> other_name;
    private final boolean edible_fruit;
    private final boolean edible_leaf;
    private final String hardiness_location;

    //private final boolean is_edible;


    public PlantDetails(String photoUrl, String commonName, List<String> scientificName, List<String> otherName, boolean edibleFruit, boolean edibleLeaf, String hardinessLocation) {
        photo_url = photoUrl;
        common_name = commonName;
        scientific_name = scientificName;
        other_name = otherName;
        edible_fruit = edibleFruit;
        edible_leaf = edibleLeaf;
        hardiness_location = hardinessLocation;
        //is_edible = edible;
    }

    public String getCommon_name() {
        return common_name;
    }

    public String getHardiness_location() {
        return hardiness_location;
    }

    public List<String> getOther_name() {
        return other_name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public List<String> getScientific_name() {
        return scientific_name;
    }

    public boolean isEdible_fruit() {
        return edible_fruit;
    }

    public boolean isEdible_leaf() {
        return edible_leaf;
    }
}

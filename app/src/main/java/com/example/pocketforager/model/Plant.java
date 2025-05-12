package com.example.pocketforager.model;

import java.io.Serializable;

public class Plant implements Serializable {
    private String commonName;
    private String scientificName;
    private String otherName;
    private String imageUrl;
    private boolean edible;

    public Plant(String commonName, String scientificName, String otherName, String imageUrl, boolean edible) {
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.otherName = otherName;
        this.imageUrl = imageUrl;
        this.edible = edible;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isEdible() {
        return edible;
    }

    public void setEdible(boolean edible) {
        this.edible = edible;
    }
}

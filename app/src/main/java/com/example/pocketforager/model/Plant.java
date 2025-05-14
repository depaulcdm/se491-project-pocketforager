package com.example.pocketforager.model;

import java.io.Serializable;

public class Plant implements Serializable {
    //private static final long serialVersionUID = 1L;
    private int ID;
    private String commonName;
    private String scientificName;
    private String otherName;
    private String imageURL;
    private boolean edible;

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getCommonName() { return commonName; }
    public void setCommonName(String commonName) { this.commonName = commonName; }

    public String getScientificName() { return scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }

    public String getOtherName() { return otherName; }
    public void setOtherName(String otherName) { this.otherName = otherName; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public boolean isEdible() { return edible; }
    public void setEdible(boolean edible) { this.edible = edible; }
}

package com.example.pocketforager;

import java.io.Serializable;
import java.util.List;

public class Plants implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int ID;
    private final String commonName;
    private final List<String> scientificName;
    private final List<String> otherName;
    private final String imageURL;

    public Plants(int id, String commonName, List<String> scientificName, List<String> otherName, String imageURL) {
        ID = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.otherName = otherName;
        this.imageURL = imageURL;
    }
    int getID(){return ID;}
    String getCommonName(){return commonName;}
    List<String> getScientificName(){return scientificName;}
    List<String> getOtherName(){return otherName;}
    String getImageURL(){return imageURL;}

}


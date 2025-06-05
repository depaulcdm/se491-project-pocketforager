package com.example.pocketforager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import java.util.Date;

@Entity(tableName = "plants")
public class PlantEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "api_id")
    public String plantApiId;

    @ColumnInfo(name = "common_name")
    public String commonName;

    @ColumnInfo(name = "scientific_name")
    public String scientificName;

    @ColumnInfo(name = "found_at")
    public Date foundAt;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "image_url")
    public String imageUrl;

    @ColumnInfo(name = "other_name")
    public String otherName;

    @ColumnInfo(name = "edible")
    public boolean edible;

    public PlantEntity(String commonName, String scientificName, String imageUrl, String otherName, boolean edible) {
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.imageUrl = imageUrl;
        this.otherName = otherName;
        this.edible = edible;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getImageURL() {
        return imageUrl;
    }

    // I'll add edibility status and more later on
}


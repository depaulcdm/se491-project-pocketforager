package com.example.pocketforager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import java.util.Date;

@Entity(tableName = "plants")
public class PlantEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

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

    // I'll add edibility status and more later on
}


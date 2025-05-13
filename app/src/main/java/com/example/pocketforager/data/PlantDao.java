package com.example.pocketforager.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface PlantDao {
    @Insert
    long insertPlant(PlantEntity plant);

    @Delete
    void deletePlant(PlantEntity plant);

    @Query("SELECT * FROM plants ORDER BY found_at DESC")
    List<PlantEntity> getAllPlants();

    @Query("SELECT * FROM plants WHERE api_id = :apiId LIMIT 1")
    PlantEntity findByApiId(String apiId);
}

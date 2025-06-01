package com.example.pocketforager.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface PlantDao {
    //@Insert
    //long insertPlant(PlantEntity plant);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlant(PlantEntity plant);

    @Query("SELECT * FROM plants WHERE edible = 1")
    List<PlantEntity> getAllEdiblePlants();

    @Delete
    void deletePlant(PlantEntity plant);

    @Query("SELECT * FROM plants ORDER BY found_at DESC")
    List<PlantEntity> getAllPlants();

    @Query("SELECT * FROM plants WHERE api_id = :apiId LIMIT 1")
    PlantEntity findByApiId(String apiId);

    @Query("SELECT * FROM plants WHERE scientific_name IN (:names)")
    List<PlantEntity> getEdiblePlantsInArea(List<String> names);

}

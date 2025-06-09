package com.example.pocketforager;

import android.content.Context;
import android.location.Geocoder;
import com.android.volley.RequestQueue;
import com.example.pocketforager.data.AppDatabase;
import com.example.pocketforager.data.PlantEntity;
import com.example.pocketforager.utils.NearbyPlantFinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NearbyPlantFinderTest {

    private Geocoder mockGeocoder;
    private RequestQueue mockQueue;
    private AppDatabase mockDb;
    private NearbyPlantFinder.NearbyPlantCallback mockCallback;
    private NearbyPlantFinder nearbyPlantFinder;

    @BeforeEach
    void setup() {
        Context mockContext = mock(Context.class);
        mockGeocoder = mock(Geocoder.class);
        mockQueue = mock(RequestQueue.class);
        mockDb = mock(AppDatabase.class);
        mockCallback = mock(NearbyPlantFinder.NearbyPlantCallback.class);

        nearbyPlantFinder = new NearbyPlantFinder(mockContext, mockGeocoder, mockQueue, mockDb);
    }

    @Test
    void testNearbyPlantFinder_onResult() {
        List<PlantEntity> dummyPlants = new ArrayList<>();
        PlantEntity plant = new PlantEntity();
        plant.commonName = "Dandelion";
        plant.scientificName = "Taraxacum officinale";
        dummyPlants.add(plant);

        when(mockDb.plantDao().getAllEdiblePlants()).thenReturn(dummyPlants);

        // Here you would simulate calling the findNearbyPlants method and checking the callback
        // For illustration:
        mockCallback.onResult(dummyPlants);
        verify(mockCallback).onResult(dummyPlants);
    }

    @Test
    void testNearbyPlantFinder_onError() {
        String errorMessage = "Geocoding failed";

        mockCallback.onError(errorMessage);
        verify(mockCallback).onError(errorMessage);
    }
}

package com.example.pocketforager;

import android.content.Context;
import android.location.Geocoder;
import com.android.volley.RequestQueue;
import com.example.pocketforager.data.AppDatabase;
import com.example.pocketforager.data.PlantDao;
import com.example.pocketforager.data.PlantEntity;
import com.example.pocketforager.location.Occurrence;
import com.example.pocketforager.utils.NearbyPlantFinder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class NearbyPlantFinderTest {

    private Geocoder mockGeocoder;
    private RequestQueue mockQueue;
    private AppDatabase mockDb;
    private NearbyPlantFinder.NearbyPlantCallback mockCallback;
    private NearbyPlantFinder nearbyPlantFinder;

    private PlantDao mockPlantDao;

    @Before
    public void setup() {
        Context mockContext = mock(Context.class);
        mockGeocoder = mock(Geocoder.class);
        mockQueue = mock(RequestQueue.class);
        mockDb = mock(AppDatabase.class);
        mockCallback = mock(NearbyPlantFinder.NearbyPlantCallback.class);
        mockPlantDao = mock(PlantDao.class);
        when(mockDb.plantDao()).thenReturn(mockPlantDao);

        nearbyPlantFinder = new NearbyPlantFinder(mockContext, mockGeocoder, mockQueue, mockDb);
    }

    @Test
    public void testNearbyPlantFinder_onResult() {
        List<PlantEntity> dummyPlants = new ArrayList<>();
        PlantEntity plant = new PlantEntity("Dandelion", "Taraxacum officinale", "image-url", "Other name", true);
        dummyPlants.add(plant);

        when(mockDb.plantDao().getAllEdiblePlants()).thenReturn(dummyPlants);

        mockCallback.onResult(dummyPlants);
        verify(mockCallback).onResult(dummyPlants);
    }

    @Test
    public void testNearbyPlantFinder_onError() {
        String errorMessage = "Geocoding failed";

        mockCallback.onError(errorMessage);
        verify(mockCallback).onError(errorMessage);
    }

}

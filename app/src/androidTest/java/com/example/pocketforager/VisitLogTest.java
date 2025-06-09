package com.example.pocketforager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pocketforager.data.AppDatabase;
import com.example.pocketforager.data.PlantDao;
import com.example.pocketforager.data.PlantEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class VisitLogTest {

    private AppDatabase db;
    private PlantDao plantDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        plantDao = db.plantDao();
    }

    //@After
    //public void tearDown() {
        //db.close();
    //}

    @Test
    public void testLogVisitInsertsCorrectData() {
        PlantEntity entity = new PlantEntity("Mint", "Mentha", "https://example.com/mint.jpg", "", true);
        entity.plantApiId = "plant-001";
        entity.location = "Botanic Garden";
        entity.foundAt = new Date();

        plantDao.insertPlant(entity);

        List<PlantEntity> visits = plantDao.getAllPlants();
        assertEquals(1, visits.size());
        assertEquals("Mint", visits.get(0).commonName);
        assertEquals("Botanic Garden", visits.get(0).location);
        assertNotNull(visits.get(0).foundAt);
    }
}


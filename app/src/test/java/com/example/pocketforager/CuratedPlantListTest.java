package com.example.pocketforager;

import com.example.pocketforager.data.PlantEntity;
import com.example.pocketforager.location.OccurrenceCuratedListvolley;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class CuratedPlantListTest {

    @Test
    public void testCuratedPlantListIsNotEmpty() {

        OccurrenceCuratedListvolley.CuratedPlantList curatedList = new OccurrenceCuratedListvolley.CuratedPlantList();
        List<PlantEntity> plants = curatedList.PLANTS;

        assertNotNull(plants);
        assertFalse(plants.isEmpty());
        assertEquals(23, plants.size());
    }

    @Test
    public void testCuratedPlantList_hasExpectedPlant() {

        OccurrenceCuratedListvolley.CuratedPlantList curatedList = new OccurrenceCuratedListvolley.CuratedPlantList();
        List<PlantEntity> plants = curatedList.PLANTS;

        boolean found = false;
        for (PlantEntity plant : plants) {

            if ("Elderberry".equals(plant.getCommonName()) &&
                    "Sambucus spp.".equals(plant.getScientificName())) {

                found = true;
                break;
            }
        }

        assertTrue("Elderberry not found in curated list", found);
    }
}

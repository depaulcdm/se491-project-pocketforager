package com.example.pocketforager;

import com.example.pocketforager.model.Plant;
import org.junit.Test;
import static org.junit.Assert.*;

public class DetailsUnitTest {
    @Test
    public void fromPlant_correctData() {
        Plant p = new Plant();
        p.setCommonName("Common Paw Paw");
        p.setScientificName("Asimina triloba");
        p.setOtherName("Pawpaw Apple, False-Banana, Poor Man's Banana, Pawpaw, False Banana, Pawpaw Custardapple");
        p.setImageURL("https://perenual.com/storage/species_image/184_asimina_triloba/regular/36488336082_9d0132fcd0_b.jpg");
        p.setEdible(false);

        Details d = DetailsMapper.fromPlant(p);

        assertEquals("Common Paw Paw", d.commonName);
        assertEquals("Asimina triloba", d.scientificName);
        assertEquals("Pawpaw Apple, False-Banana, Poor Man's Banana, Pawpaw, False Banana, Pawpaw Custardapple", d.otherName);
        assertEquals("No", d.edibleText);
        assertFalse(d.site);
    }

    @Test
    public void fromPlant_missingData() {
        Plant p = new Plant();
        p.setCommonName("");
        p.setScientificName("");
        p.setOtherName(null);
        p.setImageURL("");
        p.setEdible(true);

        Details d = DetailsMapper.fromPlant(p);

        assertEquals("", d.commonName);
        assertEquals("—", d.scientificName);
        assertEquals("—", d.otherName);
        assertEquals("Yes", d.edibleText);
        assertTrue(d.site);
    }
}


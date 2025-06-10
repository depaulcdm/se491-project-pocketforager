package com.example.pocketforager;

import com.example.pocketforager.utils.MapPinHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapPinHelperUnitTest {

    @Test
    public void testGenerateMarkers_withNullInput_returnsEmptyList() {
        List<MarkerOptions> result = MapPinHelper.generateMarkers(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGenerateMarkers_withValidLatLngList_returnsMarkers() {
        LatLng point = new LatLng(41.9, -87.6);
        List<LatLng> points = Collections.singletonList(point);

        List<MarkerOptions> result = MapPinHelper.generateMarkers(points);

        assertEquals(1, result.size());
        assertEquals(point, result.get(0).getPosition());
    }

    @Test
    public void testGenerateMarkers_withMultiplePoints() {
        LatLng p1 = new LatLng(41.9, -87.6);
        LatLng p2 = new LatLng(42.0, -87.7);
        List<LatLng> points = Arrays.asList(p1, p2);

        List<MarkerOptions> result = MapPinHelper.generateMarkers(points);

        assertEquals(2, result.size());
        assertEquals(p1, result.get(0).getPosition());
        assertEquals(p2, result.get(1).getPosition());
    }
}

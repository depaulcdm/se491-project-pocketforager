package com.example.pocketforager;

import com.example.pocketforager.utils.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapPinHelperTest {

    @Test
    public void testGenerateMarkers_returnsCorrectMarkers() {
        List<LatLng> coords = Arrays.asList(
                new LatLng(12.9716, 77.5946),
                new LatLng(13.0827, 80.2707)
        );

        List<MarkerOptions> markers = MapPinHelper.generateMarkers(coords);

        assertEquals(2, markers.size());
        assertEquals(12.9716, markers.get(0).getPosition().latitude, 0.0001);
        assertEquals(80.2707, markers.get(1).getPosition().longitude, 0.0001);
    }

    @Test
    public void testGenerateMarkers_withNullInput_returnsEmptyList() {
        List<MarkerOptions> result = MapPinHelper.generateMarkers(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGenerateMarkers_withEmptyList_returnsEmptyList() {
        List<MarkerOptions> result = MapPinHelper.generateMarkers(Collections.emptyList());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGenerateMarkers_withSingleCoordinate() {
        LatLng single = new LatLng(40.7128, -74.0060);
        List<MarkerOptions> result = MapPinHelper.generateMarkers(Collections.singletonList(single));

        assertEquals(1, result.size());
        assertEquals(40.7128, result.get(0).getPosition().latitude, 0.0001);
        assertEquals(-74.0060, result.get(0).getPosition().longitude, 0.0001);
    }

    @Test
    public void testGenerateMarkers_withDuplicateCoordinates() {
        LatLng dup = new LatLng(37.7749, -122.4194);
        List<LatLng> list = Arrays.asList(dup, dup, dup);

        List<MarkerOptions> result = MapPinHelper.generateMarkers(list);

        assertEquals(3, result.size());
        for (MarkerOptions marker : result) {
            assertEquals(37.7749, marker.getPosition().latitude, 0.0001);
            assertEquals(-122.4194, marker.getPosition().longitude, 0.0001);
        }
    }

    @Test
    public void testGenerateMarkers_withNegativeCoordinates() {
        LatLng southPole = new LatLng(-90.0, 0.0);
        LatLng westernAus = new LatLng(-31.9505, 115.8605);
        List<LatLng> coords = Arrays.asList(southPole, westernAus);

        List<MarkerOptions> result = MapPinHelper.generateMarkers(coords);

        assertEquals(2, result.size());
        assertEquals(-90.0, result.get(0).getPosition().latitude, 0.0001);
        assertEquals(115.8605, result.get(1).getPosition().longitude, 0.0001);
    }
}


package com.example.pocketforager.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapPinHelper {

    public static List<MarkerOptions> generateMarkers(List<LatLng> latLngs) {
        List<MarkerOptions> result = new ArrayList<>();
        if (latLngs == null) return result;

        for (LatLng point : latLngs) {
            result.add(new MarkerOptions().position(point));
        }
        return result;
    }
}


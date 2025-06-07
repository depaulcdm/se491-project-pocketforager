package com.example.pocketforager.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pocketforager.data.AppDatabase;
import com.example.pocketforager.data.PlantEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.util.*;

public class NearbyPlantFinder {

    public interface NearbyPlantCallback {
        void onResult(List<PlantEntity> plants);
        void onError(String error);
    }



    public static void findNearbyPlants(String cityState, Context context, NearbyPlantCallback callback) {
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> results = geocoder.getFromLocationName(cityState, 1);
            if (results.isEmpty()) {
                callback.onError("Location not found");
                return;
            }

            double lat = results.get(0).getLatitude();
            double lon = results.get(0).getLongitude();
            double latDelta = 0.009;
            double lonDelta = 0.012;

            String wktPolygon = String.format(Locale.US,
                    "POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                    lon - lonDelta, lat + latDelta,
                    lon + lonDelta, lat + latDelta,
                    lon + lonDelta, lat - latDelta,
                    lon - lonDelta, lat - latDelta,
                    lon - lonDelta, lat + latDelta
            );

            String url = "https://api.gbif.org/v1/occurrence/search?geometry=" +
                    URLEncoder.encode(wktPolygon, "UTF-8") +
                    "&hasCoordinate=true&limit=100";

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {

                            Log.d("GBIFResponse", response.toString());

                            JSONArray gbifResults = response.getJSONArray("results");
                            Set<String> gbifNames = new HashSet<>();

                            for (int i = 0; i < gbifResults.length(); i++) {

                                JSONObject item = gbifResults.getJSONObject(i);

                                String kingdom = item.optString("kingdom");
                                if (!"Plantae".equalsIgnoreCase(kingdom)) continue;

                                String rawName = item.optString("acceptedScientificName", item.optString("scientificName"));

                                String[] parts = rawName.trim().split("\\s+");
                                String cleanName = (parts.length >= 2) ? parts[0] + " " + parts[1] : rawName.trim();

                                Log.d("GBIFName", "Accepted: " + cleanName);
                                gbifNames.add(cleanName);

                            }


                            AppDatabase db = AppDatabase.getDatabase(context);
                            new Thread(() -> {
                                List<PlantEntity> matchingPlants = db.plantDao().getEdiblePlantsInArea(new ArrayList<>(gbifNames));
                                // not running on main thread because it causes error
                                android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
                                mainHandler.post(() -> callback.onResult(matchingPlants));
                            }).start();


                        } catch (Exception e) {
                            Log.e("GBIFParsingError", "Error parsing GBIF JSON: " + e.getMessage());
                            callback.onError("Parse error: " + e.getMessage());
                        }
                    },
                    error -> {
                                    Log.e("GBIFAPIError", "API error: " + error.getMessage());
                                    callback.onError("API error: " + error.getMessage());
                    }

            );

            queue.add(request);
        } catch (Exception e) {
            callback.onError("Geocoding error: " + e.getMessage());
        }
    }
}

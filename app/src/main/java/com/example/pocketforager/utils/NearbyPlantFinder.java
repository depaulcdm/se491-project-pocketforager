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

    private final Geocoder geocoder;
    private final RequestQueue queue;
    private final AppDatabase db;
    private final Context context;

    // Constructor for dependency injection
    public NearbyPlantFinder(Context context, Geocoder geocoder, RequestQueue queue, AppDatabase db) {
        this.context = context;
        this.geocoder = geocoder;
        this.queue = queue;
        this.db = db;
    }

    // Static factory for production use
    public static NearbyPlantFinder create(Context context) {
        Geocoder geocoder = new Geocoder(context);
        RequestQueue queue = Volley.newRequestQueue(context);
        AppDatabase db = AppDatabase.getDatabase(context);
        return new NearbyPlantFinder(context, geocoder, queue, db);
    }

    public void findNearbyPlants(String cityState, NearbyPlantCallback callback) {
        try {
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
                            new Thread(() -> {
                                List<PlantEntity> matchingPlants = db.plantDao().getEdiblePlantsInArea(new ArrayList<>(gbifNames));
                                android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
                                mainHandler.post(() -> callback.onResult(matchingPlants));
                            }).start();
                        } catch (JSONException e) {
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

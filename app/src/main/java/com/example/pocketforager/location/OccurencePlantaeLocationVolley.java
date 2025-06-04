package com.example.pocketforager.location;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pocketforager.MainActivity;
import com.example.pocketforager.MapsActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OccurencePlantaeLocationVolley {

    private static final String TAG = "OccurencePlantaeLocationVolley";

    public static final String BASE_URL = "https://api.plantnet.org/v2/occurrence/search?taxon=Plantae&include=location&limit=1000&offset=0&sort=score&sortOrder=desc&fields=decimalLatitude,decimalLongitude";


    private RequestQueue queue;
    public void getPlantaeOccurrences(double centerLat, double centerLon, double radiusKm, int limit, MapsActivity context) {

        ArrayList<String> names = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(context);



        double radiusDeg = radiusKm / 111.0;

        double latMin = centerLat - radiusDeg;
        double latMax = centerLat + radiusDeg;
        double lonMin = centerLon - radiusDeg;
        double lonMax = centerLon + radiusDeg;
        @SuppressLint("DefaultLocale") String polygonWKT = String.format(
                "POLYGON ((%f %f, %f %f, %f %f, %f %f, %f %f))",
                lonMin, latMin,
                lonMax, latMin,
                lonMax, latMax,
                lonMin, latMax,
                lonMin, latMin // close the polygon
        );

        String url = new Uri.Builder()
                .scheme("https")
                .authority("api.gbif.org")
                .appendPath("v1")
                .appendPath("occurrence")
                .appendPath("search")
                .appendQueryParameter("taxonKey", String.valueOf(6))
                .appendQueryParameter("has_coordinate", "true")
                .appendQueryParameter("geometry", polygonWKT)
                .appendQueryParameter("limit", String.valueOf(limit))
                .build()
                .toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: " + response.toString());
                            ArrayList<LatLng> coordinates = new ArrayList<>();
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject occurrence = results.getJSONObject(i);
                                if (occurrence.has("decimalLatitude") && occurrence.has("decimalLongitude")) {
                                    double lat = occurrence.getDouble("decimalLatitude");
                                    double lon = occurrence.getDouble("decimalLongitude");
                                    String name = occurrence.optString("scientificName", "Unknown");
                                    Log.d(TAG, "Lat: " + lat + ", Lon: " + lon + ", Name: " + name);
                                    LatLng location = new LatLng(lat, lon);
                                    coordinates.add(location);
                                    context.addMarkers(coordinates);

                                }

                            }



                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error: " + error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);
    }


    public interface OccurrenceCallback {
        void onResponse(ArrayList<String> names);
        void onError(String errorMessage);
    }

}

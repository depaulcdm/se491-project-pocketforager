package com.example.pocketforager;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GBIFVolley {

    private static final String TAG = "GBIFApi";
    private static final String URL = "https://api.gbif.org/v1/occurrence/search?scientificName=Panthera%20leo&limit=200";

    private RequestQueue requestQueue;

//    public GBIFVolley(Context context) {
//        requestQueue = Volley.newRequestQueue(context);
//    }

    public void fetchOccurrences(String scientificName, int limit, MapsActivity context) {

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = new Uri.Builder()
                .scheme("https")
                .authority("api.gbif.org")
                .appendPath("v1")
                .appendPath("occurrence")
                .appendPath("search")
                .appendQueryParameter("scientificName", scientificName)
                .appendQueryParameter("limit", String.valueOf(limit))
                .build()
                .toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<LatLng> coordinates = new ArrayList<>();
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject occurrence = results.getJSONObject(i);
                                if (occurrence.has("decimalLatitude") && occurrence.has("decimalLongitude")) {
                                    double lat = occurrence.getDouble("decimalLatitude");
                                    double lon = occurrence.getDouble("decimalLongitude");
                                    Log.d(TAG, "Lat: " + lat + ", Lon: " + lon);
                                    LatLng location = new LatLng(lat, lon);
                                    coordinates.add(location);


                                }
                            }
                            context.addMarkers(coordinates);

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
}

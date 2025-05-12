package com.example.pocketforager;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetPlantDataVolley {
    private static final String url = "https://perenual.com/api/v2/species-list";
    private static final String TAG = "PlantVolley";

    public static void downloadPlants(Context context, String plantName, PlantCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        buildURL.appendQueryParameter("key", "sk-5GzT681c162c2fcf210300");
        buildURL.appendQueryParameter("q", plantName);
        buildURL.appendQueryParameter("edible", "1");
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "URL: " + urlToUse);

        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString(), callback);
            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing failed", e);
            }
        };

        Response.ErrorListener error = error1 -> {
            if (error1 instanceof NoConnectionError) {
                Log.d(TAG, "No network connection");
            } else {
                Log.d(TAG, "Volley error: " + error1.getMessage());
            }
        };

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        queue.add(jsonObjectRequest);
    }

    private static void handleSuccess(String responseText, PlantCallback callback) throws JSONException {
        JSONObject response = new JSONObject(responseText);
        ArrayList<Plants> plants = new ArrayList<>();
        JSONArray data = response.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            JSONObject jData = data.getJSONObject(i);
            int id = jData.getInt("id");
            String commonName = jData.optString("common_name", "");

            JSONArray scientificNameArray = jData.getJSONArray("scientific_name");
            List<String> scientificNames = new ArrayList<>();
            for (int j = 0; j < scientificNameArray.length(); j++) {
                scientificNames.add(scientificNameArray.getString(j));
            }

            List<String> otherNames = new ArrayList<>();
            if (!jData.isNull("other_name")) {
                JSONArray otherNameArray = jData.getJSONArray("other_name");
                for (int j = 0; j < otherNameArray.length(); j++) {
                    otherNames.add(otherNameArray.getString(j));
                }
            }

            String originalUrl = "";
            if (!jData.isNull("default_image")) {
                JSONObject defaultImage = jData.getJSONObject("default_image");
                if (defaultImage.has("regular_url") && !defaultImage.isNull("regular_url")) {
                    originalUrl = defaultImage.getString("regular_url");
                }
            }

            plants.add(new Plants(id, commonName, scientificNames, otherNames, originalUrl));
        }

        callback.onPlantsDownloaded(plants);
    }
}

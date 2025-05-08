package com.example.pocketforager;

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
    private static String API = "sk-0T7n681a331e6f78b10272";
    private static String url ="https://perenual.com/api/v2/species-list";
    private static final String TAG = "PlantVolley";

    public static void downloadPlants(MainActivity mainActivity,String plantName){
        RequestQueue queue = Volley.newRequestQueue(mainActivity);


        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        buildURL.appendQueryParameter("key", "sk-0T7n681a331e6f78b10272");
        buildURL.appendQueryParameter("edible", "1");
        String urlToUse = buildURL.build().toString();


        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString(), mainActivity,plantName);
            } catch (JSONException e) {
                Log.d(TAG, "JSON failed");
                throw new RuntimeException(e);

            }
        };

        Response.ErrorListener error = error1 -> {
            if (error1 instanceof NoConnectionError) {
                // Handle no network connection error
                Log.d(TAG, "No network connection");
                Log.d(TAG, "JSON failed");
                // You can also show a user-friendly message or take appropriate action
                //mainActivityIn.shutDown();
            } else {
                //handleFail(error1, mainActivityIn);
            }};

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);


    }
    private static void handleSuccess(String responseText, MainActivity mainActivity, String plantName) throws JSONException {

        JSONObject response = new JSONObject(responseText);
        ArrayList<Plants> Plants = new ArrayList<>();
        JSONArray data = response.getJSONArray("data");

        for(int i =0; i < data.length(); i++){
            JSONObject jData = (JSONObject) data.get(i);
            int id = jData.getInt("id");
            String commonName = jData.getString("common_name");

            // Extracting scientific_name array
            JSONArray scientificNameArray = jData.getJSONArray("scientific_name");
            List<String> scientificNames = new ArrayList<>();
            for (int j = 0; j < scientificNameArray.length(); j++) {
                scientificNames.add(scientificNameArray.getString(j));

            }

            // Extracting other_name array (if not null)
            List<String> otherNames = new ArrayList<>();
            if (!jData.isNull("other_name")) {
                JSONArray otherNameArray = jData.getJSONArray("other_name");
                for (int j = 0; j < otherNameArray.length(); j++) {
                    otherNames.add(otherNameArray.getString(j));
                }
            }


            // Extracting image URLs from default_image object
            String originalUrl = "";
            if (!jData.isNull("default_image")) {
                JSONObject defaultImage = jData.getJSONObject("default_image");
                if (defaultImage.has("regular_url") && !defaultImage.isNull("regular_url")) {
                    originalUrl = defaultImage.getString("regular_url");
                } else {
                    Log.d(TAG, "No value for regular_url in default_image");
                }
            } else {
                Log.d(TAG, "default_image is null");
            }
            Log.d(TAG, originalUrl);
            Plants plant = new Plants(id,commonName,scientificNames,otherNames,originalUrl);
            Plants.add(plant);
        }
        mainActivity.acceptPlants(Plants);

    }
}

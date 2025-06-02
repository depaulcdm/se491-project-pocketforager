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

public class PlantDetailsVolley {

    private static String API = "sk-0vim681b5258c92e110289";
    private static String url ="https://perenual.com/api/v2/species/details/";
    private static final String TAG = "PlantDetailsVolley";

    public static void downloadPlants(DetailsPageActivity mainActivity,String ID){
        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        String combine = url + ID;
        Uri.Builder buildURL = Uri.parse(combine).buildUpon();
        buildURL.appendQueryParameter("key", "sk-0vim681b5258c92e110289");

        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "URL: " + urlToUse);


        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString(), mainActivity);
            } catch (JSONException e) {
                Log.d(TAG, "JSON failed");
                mainActivity.failedDetails();
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
                mainActivity.failedDetails();
            }};

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);


    }
    private static void handleSuccess(String responseText, DetailsPageActivity mainActivity) throws JSONException {

        JSONObject response = new JSONObject(responseText);
        ArrayList<Plants> Plants = new ArrayList<>();


        JSONObject hardinessLocation = response.getJSONObject("hardiness_location");


        String common_name = response.getString("common_name");
        Log.d(TAG, "common name is: " + common_name);
        String full_url ="";
        if(!hardinessLocation.isNull("full_url")){
            full_url = hardinessLocation.getString("full_url");
            Log.d(TAG, "location is in this url: " + full_url);
        }

        // Extracting scientific_name array
        JSONArray scientificNameArray = response.getJSONArray("scientific_name");
        List<String> scientificNames = new ArrayList<>();
        for (int j = 0; j < scientificNameArray.length(); j++) {
            scientificNames.add(scientificNameArray.getString(j));
            Log.d(TAG, "science name is: " + scientificNameArray.getString(j));
        }

        // Extracting other_name array (if not null)
        List<String> otherNames = new ArrayList<>();
        if (!response.isNull("other_name")) {
            JSONArray otherNameArray = response.getJSONArray("other_name");
            for (int j = 0; j < otherNameArray.length(); j++) {
                otherNames.add(otherNameArray.getString(j));
                Log.d(TAG, "other name is: " + otherNameArray.getString(j));
            }
        }

        boolean edible_fruit = response.getBoolean("edible_fruit");

        if(edible_fruit){
            Log.d(TAG, "fruit is edible");
        }

        boolean edible_leaf = response.getBoolean("edible_leaf");

        if(edible_leaf){
            Log.d(TAG, "leaf is edible");
        }

        JSONObject defaultImages = response.getJSONObject("default_image");

        String originalUrl = "";
        if (!defaultImages.isNull("default_image")) {
            JSONObject defaultImage = defaultImages.getJSONObject("original_url");
            originalUrl =defaultImage.getString("original_url");
            if (defaultImage.has("regular_url") && !defaultImage.isNull("regular_url")) {
                originalUrl = defaultImage.getString("regular_url");
            } else {
                Log.d(TAG, "No value for regular_url in default_image");
            }
        } else {
            Log.d(TAG, "default_image is null");
        }



        PlantDetails details = new PlantDetails(originalUrl,common_name,scientificNames,otherNames,edible_fruit,edible_leaf,full_url);


        mainActivity.acceptDetails(details);

    }
}

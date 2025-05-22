package com.example.pocketforager.location;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class LocationVolley {
    private static final String BASE_URL = "https://api.gbif.org/v1/occurrence/search";
    private RequestQueue queue;

    public interface OccurrenceCallback {
        void onSuccess(List<Occurrence> occurrences);
        void onFailure(Exception e);
    }

    // For running the app
    public LocationVolley(Context context) {
        this.queue = createRequestQueue(context);
    }

    // For running unit tests
    public LocationVolley(RequestQueue requestQueue) {
        this.queue = requestQueue;
    }


    protected RequestQueue createRequestQueue(Context context) {
        return Volley.newRequestQueue(context);
    }


    public void getOccurrences(String scientificName, double lonMin, double lonMax, double latMin, double latMax,
            int limit, OccurrenceCallback callback) {

        String envelope = String.format("ENVELOPE(%f,%f,%f,%f)", lonMin, lonMax, latMax, latMin);
        String url = BASE_URL + "?scientificName=" + scientificName + "&has_coordinate=true" + "&geometry=" + envelope
                + "&limit=" + limit;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> parseResponse(response, callback), error -> callback.onFailure(error));

        queue.add(request);
    }


    public void parseResponse(JSONObject response, OccurrenceCallback callback) {
        try {
            JSONArray results = response.getJSONArray("results");
            List<Occurrence> list = new ArrayList<>();

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);

                if ("Plantae".equals(item.optString("kingdom")) && !item.isNull("decimalLatitude")
                        && !item.isNull("decimalLongitude")) {

                    double lat = item.getDouble("decimalLatitude");
                    double lon = item.getDouble("decimalLongitude");
                    list.add(new Occurrence(lat, lon));
                }
            }

            callback.onSuccess(list);

        } catch (JSONException e) {
            callback.onFailure(e);
        }
    }
}



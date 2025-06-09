package com.example.pocketforager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pocketforager.location.LocationVolley;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LocationVolleyTest {

    private RequestQueue mockQueue;
    private LocationVolley.OccurrenceCallback mockCallback;
    private LocationVolley locationVolley;

    @BeforeEach
    void setup() {
        mockQueue = mock(RequestQueue.class);
        mockCallback = mock(LocationVolley.OccurrenceCallback.class);
        locationVolley = new LocationVolley(mockQueue);
    }

    @Test
    void testOnSuccessCallbackCalled() throws Exception {

        String jsonResponse = "{ \"results\": [ { \"taxonKey\": 123, \"species\": \"Test Plant\" } ] }";
        JSONObject responseObject = new JSONObject(jsonResponse);

        Response.Listener<JSONObject> listener = response -> {
            try {

                List<Occurrence> occurrences = locationVolley.parseOccurrences(response);
                assertEquals(1, occurrences.size());
                assertEquals(123, occurrences.get(0).getTaxonKey());
                assertEquals("Test Plant", occurrences.get(0).getSpecies());
            } catch (Exception e) {
                fail(e);
            }
        };

        listener.onResponse(responseObject);
    }

    @Test
    void testOnFailureCallbackCalled() {
        VolleyError error = new VolleyError("Network error");

        Response.ErrorListener errorListener = responseError -> {
            assertEquals("Network error", responseError.getMessage());
        };

        errorListener.onErrorResponse(error);
    }
}

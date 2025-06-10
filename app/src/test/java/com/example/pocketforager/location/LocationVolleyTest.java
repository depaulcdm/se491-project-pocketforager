package com.example.pocketforager.location;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

public class LocationVolleyTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testCreateRequestQueue() {


    }

    @Test
    public void getOccurrences_createsCorrectUrlAndRequest() {
        RequestQueue mockQueue = mock(RequestQueue.class);
        LocationVolley locationVolley = new LocationVolley(mockQueue);

        String scientificName = "Quercus";
        double lonMin = -120.0, lonMax = -110.0, latMin = 30.0, latMax = 40.0;
        int limit = 50;
        LocationVolley.OccurrenceCallback mockCallback = mock(LocationVolley.OccurrenceCallback.class);

        locationVolley.getOccurrences(scientificName, lonMin, lonMax, latMin, latMax, limit, mockCallback);

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(mockQueue).add(captor.capture());

        JsonObjectRequest request = (JsonObjectRequest) captor.getValue();
        String url = request.getUrl();

        assertTrue(url.contains("scientificName=Quercus"));
        assertTrue(url.contains("geometry=ENVELOPE(-120.000000,-110.000000,40.000000,30.000000)"));
        assertTrue(url.contains("limit=50"));
        assertTrue(url.contains("has_coordinate=true"));
    }

    @Test
    public void getOccurrences_buildsCorrectRequestUrl() {
        RequestQueue mockQueue = mock(RequestQueue.class);
        LocationVolley locationVolley = new LocationVolley(mockQueue);

        locationVolley.getOccurrences("Quercus", -10, 10, -5, 5, 100, mock(LocationVolley.OccurrenceCallback.class));

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(mockQueue).add(captor.capture());

        JsonObjectRequest request = (JsonObjectRequest) captor.getValue();
        String url = request.getUrl();

        assertTrue(url.contains("scientificName=Quercus"));
        assertTrue(url.contains("geometry=ENVELOPE(-10.000000,10.000000,5.000000,-5.000000)"));
        assertTrue(url.contains("limit=100"));
        assertTrue(url.contains("has_coordinate=true"));
    }

    @Test
    public void parseResponse_malformedJson_triggersFailure() throws JSONException {
        LocationVolley locationVolley = new LocationVolley((RequestQueue) null);

        JSONObject response = new JSONObject(); // missing "results" array

        LocationVolley.OccurrenceCallback mockCallback = mock(LocationVolley.OccurrenceCallback.class);
        locationVolley.parseResponse(response, mockCallback);

        verify(mockCallback).onFailure(any(JSONException.class));
    }

    public void testParseResponse_validPlantaeEntries_successfulCallback() throws JSONException {
            LocationVolley locationVolley = new LocationVolley((RequestQueue) null); // null because we don't need queue here

            // Sample JSON response
            String json = "{ \"results\": [" +
                    "{\"kingdom\": \"Plantae\", \"decimalLatitude\": 10.0, \"decimalLongitude\": 20.0}," +
                    "{\"kingdom\": \"Animalia\", \"decimalLatitude\": 10.0, \"decimalLongitude\": 20.0}," +
                    "{\"kingdom\": \"Plantae\", \"decimalLatitude\": 15.0, \"decimalLongitude\": 25.0}" +
                    "]}";

            JSONObject response = new JSONObject(json);

            LocationVolley.OccurrenceCallback mockCallback = mock(LocationVolley.OccurrenceCallback.class);
            locationVolley.parseResponse(response, mockCallback);

            ArgumentCaptor<List<Occurrence>> captor = ArgumentCaptor.forClass(List.class);
            verify(mockCallback).onSuccess(captor.capture());

            List<Occurrence> occurrences = captor.getValue();
            assertEquals(2, occurrences.size());
            assertEquals(10.0, occurrences.get(0).getLatitude(), 0.01);
            assertEquals(25.0, occurrences.get(1).getLongitude(), 0.01);
        }
    }

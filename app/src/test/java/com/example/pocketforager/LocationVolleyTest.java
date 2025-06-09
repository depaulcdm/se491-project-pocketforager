package com.example.pocketforager;

import com.android.volley.RequestQueue;
import com.example.pocketforager.location.LocationVolley;
import com.example.pocketforager.location.Occurrence;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.*;

public class LocationVolleyTest {

    private RequestQueue mockQueue;
    private LocationVolley.OccurrenceCallback mockCallback;
    private LocationVolley locationVolley;

    @Before
    public void setup() {
        mockQueue = mock(RequestQueue.class);
        mockCallback = mock(LocationVolley.OccurrenceCallback.class);
        locationVolley = new LocationVolley(mockQueue);
    }

    @Test
    public void testParseResponse_callsOnSuccess() throws Exception {

        String jsonResponse = "{ \"results\": [ { " +
                "\"kingdom\": \"Plantae\", " +
                "\"decimalLatitude\": 41.9, " +
                "\"decimalLongitude\": -87.6 } ] }";

        JSONObject responseObject = new JSONObject(jsonResponse);


        locationVolley.parseResponse(responseObject, mockCallback);


        ArgumentCaptor<List<Occurrence>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockCallback).onSuccess(captor.capture());

        List<Occurrence> occurrences = captor.getValue();
        assertEquals(1, occurrences.size());
        assertEquals(41.9, occurrences.get(0).getLatitude(), 0.0001);
        assertEquals(-87.6, occurrences.get(0).getLongitude(), 0.0001);

    }

    @Test
    public void testParseResponse_handlesJSONException_andCallsOnFailure() throws Exception {

        String badJson = "{ \"bad_key\": [] }";
        JSONObject responseObject = new JSONObject(badJson);

        locationVolley.parseResponse(responseObject, mockCallback);


        verify(mockCallback).onFailure(any(JSONException.class));
    }
}

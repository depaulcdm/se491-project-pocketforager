package com.example.pocketforager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.android.volley.RequestQueue;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.example.pocketforager.location.Occurrence;
import com.example.pocketforager.location.LocationVolley;
import com.example.pocketforager.location.LocationFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
@Ignore
public class LocationUnitTest {

    private LocationVolley client;
    private RequestQueue mockQueue;


    @Before
    public void setUp() {
        mockQueue = mock(RequestQueue.class);
        client = new LocationVolley(mockQueue);
    }

    @Test
    public void occurrenceGetterTest() {

        Occurrence o = new Occurrence(10.00, 20.00);
        assertEquals(10.00, o.getLatitude(), 0.0);
        assertEquals(20.00, o.getLongitude(), 0.0);
    }

    @Test
    public void localVolleyTest() throws JSONException {

        String json = "{\"results\":[" +"{\"kingdom\":\"Plantae\",\"decimalLatitude\":1.0,\"decimalLongitude\":2.0},"
                +"{\"kingdom\":\"Animalia\",\"decimalLatitude\":3.0,\"decimalLongitude\":4.0},"
                +"{\"kingdom\":\"Plantae\",\"decimalLatitude\":5.0,\"decimalLongitude\":6.0}" +"]}";

        JSONObject response = new JSONObject(json);

        LocationVolley.OccurrenceCallback cb = mock(LocationVolley.OccurrenceCallback.class);
        client.parseResponse(response, cb);

        ArgumentCaptor<List<Occurrence>> captor = ArgumentCaptor.forClass(List.class);
        verify(cb).onSuccess(captor.capture());
        List<Occurrence> list = captor.getValue();

        assertEquals(2, list.size());

        assertEquals(1.0, list.get(0).getLatitude(),  0.0);
        assertEquals(2.0, list.get(0).getLongitude(), 0.0);

        assertEquals(5.0, list.get(1).getLatitude(),  0.0);
        assertEquals(6.0, list.get(1).getLongitude(), 0.0);

    }

    @Test
    public void localFragmentTest() {

        boolean implementsInterface = false;

        for (Class<?> inface : LocationFragment.class.getInterfaces()) {
            if (inface == OnMapReadyCallback.class) {
                implementsInterface = true;
            }
        }

        assertTrue(implementsInterface);
    }
}


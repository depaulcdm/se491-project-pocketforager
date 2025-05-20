package com.example.pocketforager.location;

import androidx.fragment.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.example.pocketforager.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.pocketforager.location.Occurrence;
import com.example.pocketforager.location.LocationVolley;

import java.util.List;

public class LocationFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private LocationVolley gbifClient;
    private Location userLocation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        gbifClient = new LocationVolley(requireContext());

        // I'm assuming userLocation is given by someone?
        return inflater.inflate(R.layout.location_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFrag.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;

        if (userLocation != null) {

            queryAndPlot("Quercus rubra"); // this is Northern Red Oak, maybe just a starting point?
        }
    }

    private void queryAndPlot(String scientificName) {

        double lat0 = userLocation.getLatitude();
        double lon0 = userLocation.getLongitude();
        double delta = 0.1;
        double lonMin = lon0 - delta;
        double lonMax = lon0 + delta;
        double latMin = lat0 - delta;
        double latMax = lat0 + delta;

        gbifClient.getOccurrences(scientificName, lonMin, lonMax, latMin, latMax, 200,
                new LocationVolley.OccurrenceCallback() {
                    @Override
                    public void onSuccess(List<Occurrence> occurrences) {

                        for (Occurrence o : occurrences) {

                            LatLng pos = new LatLng(o.getLatitude(), o.getLongitude());
                            map.addMarker(new MarkerOptions().position(pos));
                        }

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat0, lon0), 10f));
                    }

                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(requireContext(), "Failed to load occurrences", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}

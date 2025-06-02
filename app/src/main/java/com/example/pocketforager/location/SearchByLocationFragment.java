package com.example.pocketforager.location;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketforager.MainActivity;
import com.example.pocketforager.R;
import com.example.pocketforager.PlantAdapter;
import com.example.pocketforager.data.PlantEntity;
import com.example.pocketforager.utils.NearbyPlantFinder;

import java.util.ArrayList;
import java.util.List;

public class SearchByLocationFragment extends Fragment {

    private EditText cityInput;
    private EditText stateInput;
    private Button searchButton;
    private RecyclerView resultsList;
    private PlantAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("LOCATION_SEARCH", "Fragment view created");


        View view = inflater.inflate(R.layout.fragment_search_by_location, container, false);

        cityInput = view.findViewById(R.id.city_input);
        stateInput = view.findViewById(R.id.state_input);
        searchButton = view.findViewById(R.id.search_button);

        if (searchButton == null) {
            Log.e("LOCATION_SEARCH", "searchButton is NULL");
        } else {
            Log.d("LOCATION_SEARCH", "searchButton is ready");
        }

        resultsList = view.findViewById(R.id.results_list);

        adapter = new PlantAdapter(new ArrayList<>(), (MainActivity) requireActivity(), false);
        resultsList.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsList.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {

            Log.d("LOCATION_SEARCH", "Search button clicked");

            String city = cityInput.getText().toString().trim();
            String state = stateInput.getText().toString().trim().toUpperCase();

            Log.d("LOCATION_SEARCH", "Button clicked with input: " + city + ", " + state);


            if (city.isEmpty() || state.length() != 2) {
                Toast.makeText(getContext(), "Enter a valid city and 2-letter state", Toast.LENGTH_SHORT).show();
                return;
            }

            String query = city + ", " + state;

            Log.d("LOCATION_SEARCH", "Searching for: " + query);

            NearbyPlantFinder.findNearbyPlants(query, getContext(), new NearbyPlantFinder.NearbyPlantCallback() {
                @Override
                public void onResult(List<PlantEntity> plants) {
                    //adapter.updateData(plants);
                    if (plants.isEmpty()) {
                        Toast.makeText(getContext(), "No edible plants found nearby", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}

package com.example.pocketforager.location;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pocketforager.MapsActivity;
import com.example.pocketforager.data.PlantEntity;
import com.example.pocketforager.data.PlantMakerData;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OccurrenceCuratedListvolley {
    private RequestQueue queue;

    public ArrayList<PlantMakerData.PlantMarkerData> resolvedMarkers = new ArrayList<>();



    public class CuratedPlantList {
        public final List<PlantEntity> PLANTS = Arrays.asList(
                new PlantEntity("Elderberry", "Sambucus spp.", "https://upload.wikimedia.org/wikipedia/commons/a/a9/Sambucus-berries.jpg", "", false),
                new PlantEntity("Jerusalem artichoke", "Helianthus tuberosus", "https://perenual.com/storage/species_image/3400_helianthus_tuberosus/regular/jerusalem-artichoke-flower-bright-yellow-small-sunflower.jpg", "", true),
                new PlantEntity("Serviceberry", "Amelanchier spp.", "https://herbalramble.wordpress.com/wp-content/uploads/2018/04/dscn5796-e1523516785715.jpg?w=1024&h=768", "", false),
                new PlantEntity("Mayapple", "Podophyllum peltatum", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Podophyllum_peltatum_Arkansas.jpg/1280px-Podophyllum_peltatum_Arkansas.jpg", "", false),
                new PlantEntity("Wild bergamot", "Monarda fistulosa", "https://perenual.com/storage/species_image/5242_monarda_fistulosa/regular/52232588250_bb9d806633_b.jpg", "", true),
                new PlantEntity("Black Huckleberry", "Gaylussacia baccata", "https://perenual.com/storage/species_image/8430_gaylussacia_baccata/regular/32047720371_d9bab0e6c7_b.jpg", "", true),
                new PlantEntity("Greenbrier", "Smilax pulverulenta", "https://wildfoods4wildlife.com/wp-content/uploads/2020/03/image1-1024x768.jpg", "", false),
                new PlantEntity("Muscadine Grape", "Vitis rotundifolia", "https://upload.wikimedia.org/wikipedia/commons/8/8b/Vitis_rotundifolia.jpg", "", false),
                new PlantEntity("Wild plum", "Prunus americana", "https://perenual.com/storage/species_image/6327_prunus_americana/regular/50368039726_6e3d13f4f8_b.jpg", "", true),
                new PlantEntity("Chicory", "Cichorium intybus", "https://perenual.com/storage/species_image/1879_cichorium_intybus/regular/52347380770_908f5f52e9_b.jpg", "", true),
                new PlantEntity("American Beech", "Fagus grandifolia", "https://mortonarb.org/app/uploads/2020/12/96574_ca_object_representations_media_23637_large-0x1920-c-default.jpg", "", false),
                new PlantEntity("American Hazelnut", "Corylus americana", "https://www.illinoiswildflowers.info/trees/photos/am_hazelnut1.jpg", "", false),
                new PlantEntity("Dandelion", "Taraxacum officinale", "https://upload.wikimedia.org/wikipedia/commons/4/4f/DandelionFlower.jpg", "", false),
                new PlantEntity("Spicebush", "Lindera benzoin", "https://perenual.com/storage/species_image/4916_lindera_benzoin/regular/28007412598_ced3351efa_b.jpg", "", true),
                new PlantEntity("Cattail", "Typha spp.", "https://cdn.mos.cms.futurecdn.net/aJDYpLLYV4JxPfBU6YSEZT.jpg", "", false),
                new PlantEntity("Scarlet strawberry", "Fragaria virginiana", "https://perenual.com/storage/species_image/3029_fragaria_virginiana/regular/9000977033_fc93917ef1_b.jpg", "", true),
                new PlantEntity("Common Persimmon", "Diospyros virginiana", "https://perenual.com/storage/species_image/274_diospyros_virginiana/regular/49014714943_39e9d002d6_b.jpg", "American Persimmon, Eastern Persimmon", true),
                new PlantEntity("Daylily", "Hemerocallis fulva", "https://www.easytogrowbulbs.com/cdn/shop/products/DaylilyAutumnRed_squareWeb_SHUT.jpg?v=1618954565", "", false),
                new PlantEntity("Small White Leek", "Allium tricoccum", "https://perenual.com/storage/species_image/8892_allium_tricoccum/regular/52351847169_fed2da6e16_b.jpg", "Ramp, Wild Leek", true),
                new PlantEntity("American chestnut", "Castanea dentata", "https://perenual.com/storage/species_image/1710_castanea_dentata/regular/50331266137_77acd7616d_b.jpg", "Sweet Chestnut", true),
                new PlantEntity("Blackberry", "Rubus fruticosus 'Chester'", "https://perenual.com/storage/species_image/6954_rubus_fruticosus_chester/regular/autumn-fruit-blackberry-healthy-fresh-delicious.jpg", "", true),
                new PlantEntity("Black Raspberry", "Rubus occidentalis", "https://perenual.com/storage/species_image/8431_rubus_occidentalis/regular/35280453713_213b0ebab1_b.jpg", "", true),
                new PlantEntity("American Groundnut", "Apios americana", "https://perenual.com/storage/species_image/9022_apios_americana/regular/51577689362_678683c38e_b.jpg", "", true)
        );
    }

    public void getPlantaeOccurrences(
            double centerLat,
            double centerLon,
            double radiusKm,
            int limitPerPlant,
            MapsActivity context) {

        List<PlantEntity> plants = new CuratedPlantList().PLANTS;
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.d(TAG, "RequestQueue created");

        double radiusDeg = radiusKm / 111.0;
        double latMin = centerLat - radiusDeg;
        double latMax = centerLat + radiusDeg;
        double lonMin = centerLon - radiusDeg;
        double lonMax = centerLon + radiusDeg;
        @SuppressLint("DefaultLocale") String polygonWKT = String.format(
                "POLYGON ((%f %f, %f %f, %f %f, %f %f, %f %f))",
                lonMin, latMin,
                lonMax, latMin,
                lonMax, latMax,
                lonMin, latMax,
                lonMin, latMin
        );

        // Use thread-safe list to gather all occurrences from all plants
        List<Pair<LatLng, String>> allOccurrences = Collections.synchronizedList(new ArrayList<>());

        AtomicInteger requestsRemaining = new AtomicInteger(plants.size());

        for (PlantEntity plant : plants) {
            String scientificName = plant.getScientificName();
            Log.d(TAG, "Processing plant: " + scientificName);

            // Build URL with scientificName filter (instead of taxonKey)
            String url = new Uri.Builder()
                    .scheme("https")
                    .authority("api.gbif.org")
                    .appendPath("v1")
                    .appendPath("occurrence")
                    .appendPath("search")
                    .appendQueryParameter("scientificName", scientificName)
                    .appendQueryParameter("has_coordinate", "true")
                    .appendQueryParameter("geometry", polygonWKT)
                    .appendQueryParameter("limit", String.valueOf(limitPerPlant))
                    .build()
                    .toString();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject occurrence = results.getJSONObject(i);
                                if (occurrence.has("decimalLatitude") && occurrence.has("decimalLongitude")) {
                                    double lat = occurrence.getDouble("decimalLatitude");
                                    double lon = occurrence.getDouble("decimalLongitude");
                                    String name = occurrence.optString("scientificName", "Unknown");
                                    Log.d(TAG, "getPlantaeOccurrences: Lat: " + lat + ", Lon: " + lon + ", Name: " + name);

                                    LatLng location = new LatLng(lat, lon);
                                    allOccurrences.add(new Pair<>(location, name));
                                    Log.d(TAG, "getPlantaeOccurrences() called with: centerLat = [" + centerLat + "], centerLon = [" + centerLon + "], radiusKm = [" + radiusKm + "], limitPerPlant = [" + limitPerPlant + "], context = [" + context + "]");
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error for " + scientificName + ": " + e.getMessage());
                        } finally {
                            // When last request completes, update UI
                            if (requestsRemaining.decrementAndGet() == 0) {
                                context.nearUserMarkers(new ArrayList<>(allOccurrences));
                            }
                        }
                    },
                    error -> {
                        Log.e(TAG, "Volley error for " + scientificName + ": " + error.getMessage());
                        if (requestsRemaining.decrementAndGet() == 0) {
                            context.nearUserMarkers(new ArrayList<>(allOccurrences));
                        }
                    });

            queue.add(request);
            Log.d(TAG, "Request added to queue");
        }
    }


}

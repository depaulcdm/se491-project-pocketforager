package com.example.pocketforager.data;

import com.google.android.gms.maps.model.LatLng;

public class PlantMakerData {
    public class PlantMarkerData {
        public LatLng location;
        public String commonName;
        public String scientificName;
        public String imageUrl;

        public PlantMarkerData(LatLng location, String commonName, String scientificName,String imageUrl) {
            this.location = location;
            this.commonName = commonName;
            this.imageUrl = imageUrl;
            this.scientificName = scientificName;
        }
    }

}

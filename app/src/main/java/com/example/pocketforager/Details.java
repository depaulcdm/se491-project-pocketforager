package com.example.pocketforager;

public class Details {
        public final String commonName;
        public final String scientificName;
        public final String otherName;
        public final String edibleText;
        public final boolean site;

        public Details(String commonName, String scientificName, String otherName,
                       String edibleText, boolean site) {
            this.commonName = commonName;
            this.scientificName = scientificName;
            this.otherName = otherName;
            this.edibleText = edibleText;
            this.site = site;
        }

}

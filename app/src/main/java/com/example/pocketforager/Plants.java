package com.example.pocketforager;
import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;
import java.util.List;

public class Plants implements Parcelable {
    private final int ID;
    private final String commonName;
    private final List<String> scientificName;
    private final List<String> otherName;
    private final String imageURL;

    public Plants(int ID, String commonName, List<String> scientificName, List<String> otherName, String imageURL) {
        this.ID = ID;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.otherName = otherName;
        this.imageURL = imageURL;
    }
    protected Plants(Parcel in) {
        ID = in.readInt();
        commonName = in.readString();
        scientificName = in.createStringArrayList();
        otherName = in.createStringArrayList();
        imageURL = in.readString();
    }

    public static final Creator<Plants> CREATOR = new Creator<Plants>() {
        @Override
        public Plants createFromParcel(Parcel in) {
            return new Plants(in);
        }

        @Override
        public Plants[] newArray(int size) {
            return new Plants[size];
        }
    };

    public int getID() {
        return ID;
    }

    public String getCommonName() {
        return commonName;
    }

    public List<String> getScientificNames() {
        return scientificName;
    }

    public List<String> getOtherNames() {
        return otherName;
    }

    public String getImageURL() {
        return imageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(commonName);
        dest.writeStringList(scientificName);
        dest.writeStringList(otherName);
        dest.writeString(imageURL);
    }

}


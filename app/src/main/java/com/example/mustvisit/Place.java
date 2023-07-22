package com.example.mustvisit;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Place {
    protected Category category;
    protected String name;
    protected String city;
    protected Point position;
    protected LatLng cord;
    protected String description;
    protected double distance;
    private MarkerOptions markerOptions;

    public Place(Category category, String name, String city,Point position, String description, double distance) {
        this.category = category;
        this.name = name;
        this.city = city;
        this.position = position;
        this.description = description;
        this.distance = distance;
    }

    public Place(Category category, String name, String city,Point position, String description, double distance, LatLng cord) {
        this.category = category;
        this.name = name;
        this.city = city;
        this.position = position;
        this.description = description;
        this.distance = distance;
        this.cord = cord;
    }

    public void setCord(LatLng placeCord){
        this.cord = placeCord;
    }
    public void setMarkerOptions(float markerColor) {
        this.markerOptions = new MarkerOptions().
                                position(this.cord).
                                title(this.name + "," + this.city).
                                icon(BitmapDescriptorFactory.defaultMarker(markerColor));
    }

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }
}

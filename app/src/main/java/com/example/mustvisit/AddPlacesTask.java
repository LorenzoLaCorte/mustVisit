package com.example.mustvisit;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class AddPlacesTask extends AsyncTask<TopPlaces, Void, Void> {

    private static final String TAG = "AddPlacesTask";
    private GoogleMap mMap;
    private Geocoder geocoder;
    private TopPlaces places;

    public AddPlacesTask(GoogleMap mMap, Geocoder geocoder) {
        this.mMap = mMap;
        this.geocoder = geocoder;
    }

    private float ChooseMarkerColor(Category category) throws Exception {
        if (category.toString().equals("HISTORICAL_PLACES")) {
            return BitmapDescriptorFactory.HUE_AZURE;
        }
        else if (category.toString().equals("FUN_ATTRACTIONS")) {
            return BitmapDescriptorFactory.HUE_RED;
        }
        else if (category.toString().equals("PARKS")) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        }
        else if (category.toString().equals("BEACHES")) {
            return BitmapDescriptorFactory.HUE_ORANGE;
        }
        else throw new Exception("Non-existent category");
    }

    @Override
    protected Void doInBackground(TopPlaces... topPlacesArray) {
        if (topPlacesArray.length == 0) {
            return null;
        }

        places = topPlacesArray[0];
        Log.d(TAG, "Category: " + places.category);
        float markerColor;
        try {
            markerColor = ChooseMarkerColor(places.category);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Place place : places.topPlaces) {
            Log.d(TAG, "Place Stats --> Name: " + place.name + ", City: " + place.city + ", Coordinates: " + place.position.x + "," + place.position.y);
            Address address;
            if (place.name.length() != 0) {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(place.name + "," + place.city, 1);
                    LatLng placeCord;
                    if (addresses != null && addresses.size() > 0) {
                        Log.d(TAG, "Using Geocoder for " + place.name);
                        // for the future : maybe instead of taking 0, take the nearest to userLocation
                        address = addresses.get(0);
                        placeCord = new LatLng(address.getLatitude(), address.getLongitude());
                    } else {
                        Log.d(TAG, "Using Standard Position for " + place.name);
                        placeCord = new LatLng(place.position.x, place.position.y);
                    }
                    // Store the marker details in a data structure or list (we'll handle it in onPostExecute)
                    place.setMarkerOptions(placeCord, markerColor);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // Now, run on the main thread and add the markers to the map.
        for (Place place : places.topPlaces) {
            if (place.getMarkerOptions() != null) {
                mMap.addMarker(place.getMarkerOptions());
            }
        }
    }
}
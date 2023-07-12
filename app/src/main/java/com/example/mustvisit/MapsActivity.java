package com.example.mustvisit;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mustvisit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng cord = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        String Category=intent.getStringExtra("category");
        List<TopPlaces> ListPlaces =Utility.getInstance().getList();
        for (TopPlaces places: ListPlaces){
            Log.d(TAG, "Category: "+places.category+ "----- Intent: "+Category);
            Log.d(TAG, "Risultato IF: "+places.category.toString().equals(Category));
            if (places.category.toString().equals("HISTORICAL_PLACES")) {
                for (Place p : places.topPlaces) {
                    Log.d(TAG, "Name: "+p.name+" coord: " + p.position.x+","+p.position.y);
                    cord=new LatLng( p.position.x, p.position.y );
                    mMap.addMarker(new MarkerOptions().position(cord).title(p.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }

            } else if (places.category.toString().equals("FUN_ATTRACTIONS")) {
                for (Place p : places.topPlaces) {
                    Log.d(TAG, "Name: "+p.name+" coord: " + p.position.x+","+p.position.y);
                    cord=new LatLng( p.position.x, p.position.y );
                    mMap.addMarker(new MarkerOptions().position(cord).title(p.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            } else if (places.category.toString().equals("PARKS")) {
                for (Place p : places.topPlaces) {
                    Log.d(TAG, "Name: "+p.name+" coord: " + p.position.x+","+p.position.y);
                    cord=new LatLng( p.position.x, p.position.y );
                    mMap.addMarker(new MarkerOptions().position(cord).title(p.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                }
            }else {
                for (Place p : places.topPlaces) {
                    Log.d(TAG, "Name: "+p.name+" coord: " + p.position.x+","+p.position.y);
                    cord=new LatLng( p.position.x, p.position.y );
                    mMap.addMarker(new MarkerOptions().position(cord).title(p.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
            }

        }
        moveToCurrentLocation(cord);
    }
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


    }
}


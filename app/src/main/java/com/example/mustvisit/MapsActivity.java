package com.example.mustvisit;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    private GoogleMap mMap;
    private ArrayList<String> SendToMaps = new ArrayList<String>(); // Create an ArrayList object
    private Point userLocation;
    private LatLng userLocationMaps = null;
    private RelativeLayout.LayoutParams layoutParams;
    private List<TopPlaces> topPlaces;
    private Geocoder geocoder;
    private Button lastAddedBtn = null;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this, Locale.getDefault());
        constraintLayout = findViewById(R.id.layout);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        topPlaces = Utility.getInstance().getList();

        for (TopPlaces places: topPlaces){
            try {
                AddPlacesTask addPlacesTask = new AddPlacesTask(mMap, geocoder);
                addPlacesTask.execute(places);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        userLocation = (Point) intent.getSerializableExtra("userLocation");
        userLocationMaps = new LatLng(userLocation.x, userLocation.y);
        moveToCurrentLocation(userLocationMaps);
        layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
    }
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    public Button createAddStopBtn() {
        // Create and Set Properties
        Button newBtn = new Button(this);
        newBtn.setId(View.generateViewId());
        newBtn.setText(R.string.add_stop);
        newBtn.setTextColor(Color.WHITE);
        newBtn.setBackgroundResource(R.drawable.button_shape_small);
        newBtn.setLayoutParams(layoutParams);
        constraintLayout.addView(newBtn);

        // Define Positioning
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(newBtn.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(newBtn.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(newBtn.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(newBtn.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.setHorizontalBias(newBtn.getId(), 0.5f);
        constraintSet.setVerticalBias(newBtn.getId(), 0.875f);
        constraintSet.applyTo(constraintLayout);

        return newBtn;
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        // Remove the existing button
        constraintLayout.removeView(lastAddedBtn);

        lastAddedBtn = createAddStopBtn();
        String titleMarker = marker.getTitle();
        lastAddedBtn.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SendToMaps.contains(titleMarker)){
                    Toast.makeText(getApplicationContext(), "Stop already added to the Trip! Select a new One!", Toast.LENGTH_SHORT).show();
                }
                else {
                    SendToMaps.add(titleMarker);
                    Toast.makeText(getApplicationContext(), "Stop added to the Trip!", Toast.LENGTH_SHORT).show();
                    constraintLayout.removeView(lastAddedBtn);
                }
            }
        });
        return false;
    }

     /**
     * ULR must be of the type:
     * ../x,y/First+Place/Second+Place/...
     */
    public void openGM(View view) {
        if (SendToMaps.size() != 0) {
            StringBuilder valueToSendMap = new StringBuilder();
            for (String s : SendToMaps) {
                valueToSendMap.append("/").append(s.replace(" ", "+"));
            }
            Uri url = Uri.parse("https://www.google.com/maps/dir/"
                                    + Double.valueOf(userLocation.x).toString() + "," + Double.valueOf(userLocation.y).toString()
                                    + valueToSendMap);

            Log.d(TAG, "Itinerary ULR: " + url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);

            SendToMaps.clear();
            startActivity(browserIntent);
        }
        else {
            Toast.makeText(getApplicationContext(),"Impossible to generate an Itinerary , add Stop!",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // Remove the existing button
        constraintLayout.removeView(lastAddedBtn);
    }
}


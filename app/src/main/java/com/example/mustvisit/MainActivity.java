package com.example.mustvisit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.google.android.material.slider.RangeSlider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RangeSlider rangeSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rangeSlider = findViewById(R.id.rangeSlider);
    }

    private void getUserLocation(final LocationCallback locationCallback) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Point userLocation = new Point(latitude, longitude);
                        locationCallback.onLocationReceived(userLocation);
                    } else {
                        throw new IllegalStateException("Unable to retrieve user location");
                    }
                }
            }, null);
        }
    }

    public void showResults(View v) {
        getUserLocation(new LocationCallback() {
            @Override
            public void onLocationReceived(Point userLocation) {
                double range = rangeSlider.getValues().get(0);
                // Retrieve the selected categories
                List<Category> selectedCategories = new ArrayList<>();
                CheckBox[] checkBoxes = {
                        findViewById(R.id.checkBoxBeaches),
                        findViewById(R.id.checkBoxFunAttractions),
                        findViewById(R.id.checkBoxParks),
                        findViewById(R.id.checkBoxHistoricalPlaces)
                };

                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isChecked()) {
                        String categoryText = checkBox.getText().toString().replaceAll("\\s+", "_");
                        Category category = Category.valueOf(categoryText.toUpperCase());
                        selectedCategories.add(category);
                    }
                }

                Intent intent = new Intent(MainActivity.this, Results.class);
                intent.putExtra("userLocation", userLocation);
                intent.putExtra("range", range);
                intent.putExtra("categories", (Serializable) selectedCategories);

                // Start the other activity
                startActivity(intent);
            }
        });
    }

    private interface LocationCallback {
        void onLocationReceived(Point userLocation);
    }
}

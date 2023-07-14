package com.example.mustvisit;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mustvisit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LatLng cord = null;
    private String titleMarker= null;
    private ArrayList<String> SendToMaps = new ArrayList<String>(); // Create an ArrayList object
    private Point userLocation;
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
            ChooseMarker(places);

        }
        moveToCurrentLocation(cord);

        mMap.setOnMarkerClickListener(this);
    }
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


    }
    private void ChooseMarker( TopPlaces Places){

        if (Places.category.toString().equals("HISTORICAL_PLACES")) {
            AddHistoricalPlaces(Places);
        } else if (Places.category.toString().equals("FUN_ATTRACTIONS")) {
           AddFunAttractions(Places);
        } else if (Places.category.toString().equals("PARKS")) {
           AddParks(Places);
        }else {
           AddBeaches(Places);
        }

    }
    public void AddHistoricalPlaces(TopPlaces Places){
        for (Place p : Places.topPlaces) {
            Log.d(TAG, "Name: "+p.name+" coord: " + p.position.x+","+p.position.y);
            cord=new LatLng( p.position.x, p.position.y );
            mMap.addMarker(new MarkerOptions().position(cord).title(p.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
    }
    public void AddFunAttractions(TopPlaces Places) {
        for (Place p : Places.topPlaces) {
            Log.d(TAG, "Name: " + p.name + " coord: " + p.position.x + "," + p.position.y);
            cord = new LatLng(p.position.x, p.position.y);
            mMap.addMarker(new MarkerOptions().position(cord).title(p.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }
    }
    public void AddParks(TopPlaces Places) {
        for (Place p : Places.topPlaces) {
            Log.d(TAG, "Name: " + p.name + " coord: " + p.position.x + "," + p.position.y);
            cord = new LatLng(p.position.x, p.position.y);
            mMap.addMarker(new MarkerOptions().position(cord).title(p.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        }
    }

    public void AddBeaches(TopPlaces Places) {
        for (Place p : Places.topPlaces) {
            Log.d(TAG, "Name: " + p.name + " coord: " + p.position.x + "," + p.position.y);
            cord = new LatLng(p.position.x, p.position.y);
            mMap.addMarker(new MarkerOptions().position(cord).title(p.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final RelativeLayout[] relativeLayout = {findViewById(R.id.layout)};
        Button button = new Button(this);
        button.setText("Add Stop");

        // Define layout parameters for the button
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        // Set the position of the button using layout rules
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        button.setId(View.generateViewId());
        // Add the button to the RelativeLayout
        relativeLayout[0].addView(button, layoutParams);
        titleMarker=marker.getTitle().toString();
        button.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View view) {
                SendToMaps.add(titleMarker);
                Toast.makeText(getApplicationContext(),"Stop added to the Trip!",Toast.LENGTH_SHORT).show();
                relativeLayout[0] = (RelativeLayout) button.getParent();
                if(null!= relativeLayout[0]) //for safety only  as you are doing onClick
                    relativeLayout[0].removeView(button);

            }
        });

        return false;
    }



    public void openGM(View view){
        //get User Location
        Intent intent = getIntent();
        userLocation = (Point) intent.getSerializableExtra("userLocation");
        String cordX= new Double(userLocation.x).toString();
        String cordY= new Double(userLocation.y).toString();
        String valueToSendMap="";
        for (String s: SendToMaps){
            s=s.replace(" ","+");
            Log.d(TAG, "Valore Send Maps: "+s);
            valueToSendMap+="/"+s;
            Log.d(TAG, "Value: "+valueToSendMap);

        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/"+cordX+","+cordY+""+valueToSendMap));
                        //44.38073010887341,9.043476119134446/Spiaggia+Pubblica+di+Priaruggia,+Genoa,+Italy/Spiaggia+di+Boccadasse,+Genoa,+Italy/Acquario+di+Genova,+Genoa,+Italy"));
        startActivity(browserIntent);
    }
}


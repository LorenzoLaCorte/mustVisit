package com.example.mustvisit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Results extends AppCompatActivity implements GptChatApiService.ChatGPTResponseListener {
    // TODO: optimize the application: use multithreading for each query to GPT
    private final int numResults = 5;     // number of results to return for each query
    private Point userLocation;
    private List<Category> userCategories = new ArrayList<>();
    private Double range;

    // Structure that contains Category and List of Places
    private List<TopPlaces> topPlacesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            userLocation = (Point) intent.getSerializableExtra("userLocation");
            range = extras.getDouble("range");
            userCategories = (List<Category>) extras.getSerializable("categories");
        }

        for (Category category : userCategories) {
            queryGPT(category);
        }
    }

    private void queryGPT(Category category) {
        String query = "Tell me the %s best %s near these coordinates (%s, %s) in the range of %s kilometers. " +
                "I want as response a numbered list without further descriptions. " +
                "I want all elements in the list to contain {name} - {coordinates (x,y)} - {short description}.";

        query = String.format(query, numResults, category.name().toLowerCase().replaceAll("_", " "), userLocation.x, userLocation.y, range);
        Log.d("ChatGPT", "Query: " + query);

        // TODO: insert threads here
        TopPlaces initTopPlaces = new TopPlaces(category, query);

        GptChatApiService.queryChatGPT(initTopPlaces, this);
        // onResponseReceived(new TopPlaces(Category.BEACHES, query, "1. Paraggi Beach - 44.319652, 9.195813 - Small, exclusive cove with turquoise waters and rocky cliffs. \n" +
        //           "2. Bay of Silence - 44.305145, 9.342084 - Wide, sandy beach with calm waters and a relaxing atmosphere. \n" +
        //           "3. Sestri Levante Beach - 44.271786, 9.399056 - Popular seaside resort with two sandy beaches and lots of amenities. \n" +
        //           "4. Lavagna Beach - 44.320249, 9.322719 - Long, pebbly beach with crystal-clear waters and a laid-back vibe. \n" +
        //           "5. Moneglia Beach - 44.246954, 9.512802 - Picturesque beach with fine sand and clear, shallow waters, great for families.\n"));
    }

    @Override
    public void onResponseReceived(TopPlaces topPlaces) {
        Log.d("ChatGPT", "Response: " + topPlaces.response);
        parseQueryResult(topPlaces);
        topPlacesList.add(topPlaces);

        if(topPlacesList.size() == userCategories.size()){
            renderUI();
        }
    }

    private void retryQuery(TopPlaces topPlaces){
        if(topPlaces.tryIncrementingRetries()){
            // TODO: insert threads here
            // TODO: if Rate limit reached, stop and render the UI
            Log.d("ChatGPT", "Retrying query, try number " +  topPlaces.retries);
            GptChatApiService.queryChatGPT(topPlaces, this);
        }
        else {
            Log.d("ChatGPT", "Limit of Retries Reached");
            topPlaces.setTopPlaces(null);
            if(topPlacesList.size() == userCategories.size()){
                renderUI();
            }
        }
    }

    @Override
    public void onError(TopPlaces topPlaces) {
        Log.d("ChatGPT", "Timeout Occurred, retrying..");
        retryQuery(topPlaces);
    }

    private List<Place> filterResult(List<Place> places){
        List<Place> filteredPlaces = new ArrayList<>();
        for (Place place : places){
            if(place.distance <= range) filteredPlaces.add(place);
        }
        return filteredPlaces;
    }
    private void parseQueryResult(TopPlaces topPlaces) {
        Parser parser = new Parser();
        List<Place> rawPlaces = parser.parseResult(topPlaces.response, topPlaces.category, userLocation);
        List<Place> places = filterResult(rawPlaces);
        if(places.size() == 0){
            Log.d("ChatGPT", "No Results, retrying..");
            retryQuery(topPlaces);
        }
        else {
            topPlaces.setTopPlaces(places);
        }
    }

    private void renderUI() {
        LinearLayout linearLayout = findViewById(R.id.resultContainer);
        linearLayout.removeAllViews(); // Clear the existing views

        for (TopPlaces topPlaces : topPlacesList) {

            // Add category as a TextView with custom formatting
            TextView categoryTextView = new TextView(this);
            LinearLayout.LayoutParams categoryLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            categoryTextView.setLayoutParams(categoryLayoutParams);
            categoryTextView.setTextAppearance(this, android.R.style.TextAppearance_Large);
            categoryTextView.setText(topPlaces.category.name().replaceAll("_", " "));
            linearLayout.addView(categoryTextView);

            // Add a separator (a single newline) between categories
            View categorySeparatorView = new View(this);
            LinearLayout.LayoutParams categorySeparatorLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 4);
            categorySeparatorView.setLayoutParams(categorySeparatorLayoutParams);
            categorySeparatorView.setBackgroundColor(Color.GRAY);
            linearLayout.addView(categorySeparatorView);

            if(topPlaces.topPlaces == null){
                TextView placeNameTextView = new TextView(this);
                LinearLayout.LayoutParams placeNameLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                placeNameTextView.setLayoutParams(placeNameLayoutParams);
                placeNameTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                placeNameTextView.setText("No Results Found.");
                linearLayout.addView(placeNameTextView);
                continue;
            }
            for (Place place : topPlaces.topPlaces) {
                // Add the place name in bold
                TextView placeNameTextView = new TextView(this);
                LinearLayout.LayoutParams placeNameLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                placeNameTextView.setLayoutParams(placeNameLayoutParams);
                placeNameTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                placeNameTextView.setText(place.name);
                linearLayout.addView(placeNameTextView);

                // Add other place details
                TextView placeDetailsTextView = new TextView(this);
                LinearLayout.LayoutParams placeDetailsLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                placeDetailsTextView.setLayoutParams(placeDetailsLayoutParams);
                placeDetailsTextView.setTextAppearance(this, android.R.style.TextAppearance_Small);
                placeDetailsTextView.setText("\uD83D\uDCCC " + String.format("%.02f", place.distance) + " km\n"
                                + "ℹ️ " + place.description +  "\n");
                linearLayout.addView(placeDetailsTextView);
            }
            categoryTextView.setOnClickListener(new TextView.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(Results.this, MapsActivity.class);
                    startActivity(myIntent);
                }
            });
        }
    }

}
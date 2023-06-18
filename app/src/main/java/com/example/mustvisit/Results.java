package com.example.mustvisit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Results extends AppCompatActivity implements GptChatApiService.ChatGPTResponseListener {

    private final int numResults = 5;     // number of results to return for each query

    private Point userLocation;
    List<Category> userCategories = new ArrayList<>();
    private Double range;

    // Structure that maps Category to List of Places
    private TopPlaces[] topPlacesList;
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

        // TODO: when every response is received, update the UI with a function
    }

    // TODO: update UI function:
    // waits for having all responses (same number as userCategories length)
    // and then properly visualize them

    private void queryGPT(Category category) {
        String query = "Tell me the %s best %s near these coordinates (%s, %s) in the range of %s kilometers. " +
                "I want as response a numbered list without further descriptions. " +
                "I want all elements in the list to contain {name} - {coordinates (x,y)} - {short description}.";

        query = String.format(query, numResults, category.name().toLowerCase().replaceAll("_", " "), userLocation.x, userLocation.y, range);
        Log.d("ChatGPT", "Query: " + query);

        // queryChatGPT should take in input but also return the type
        // GptChatApiService.queryChatGPT(category, query, this);
        onResponseReceived(new TopPlaces(Category.BEACHES, query, "1. Paraggi Beach - 44.319652, 9.195813 - Small, exclusive cove with turquoise waters and rocky cliffs. \n" +
                 "2. Bay of Silence - 44.305145, 9.342084 - Wide, sandy beach with calm waters and a relaxing atmosphere. \n" +
                 "3. Sestri Levante Beach - 44.271786, 9.399056 - Popular seaside resort with two sandy beaches and lots of amenities. \n" +
                 "4. Lavagna Beach - 44.320249, 9.322719 - Long, pebbly beach with crystal-clear waters and a laid-back vibe. \n" +
                 "5. Moneglia Beach - 44.246954, 9.512802 - Picturesque beach with fine sand and clear, shallow waters, great for families.\n"));
    }

    @Override
    public void onResponseReceived(TopPlaces topPlaces) {
        Log.d("ChatGPT", "Response: " + topPlaces.response);
        parseQueryResult(topPlaces);
        // TODO: add topPlaces to topPlacesList
    }

    @Override
    public void onError(String errorMessage) {
        Log.d("ChatGPT", "Error: " + errorMessage);
    }

    private void parseQueryResult(TopPlaces topPlaces) {
        Parser parser = new Parser();
        List<Place> places = parser.parseResult(topPlaces.response, topPlaces.category, userLocation);
        topPlaces.setTopPlaces(places);

        // TODO: move the logic to a visualization function and make a scroll view
        TextView tv = findViewById(R.id.textView);
        String viewText = "";
        String tmp = "";
        for (Place place : topPlaces.topPlaces) {
            tmp = "Name: " + place.name + "\n" + "Position: (" + place.position.x + ", " + place.position.y + ")\n" + "Description: " + place.description + "\n" + "Distance: " + place.distance + " km\n" + "-----\n";
            viewText += tmp;
        }
        tv.setText(viewText);
    }
}
package com.example.mustvisit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class Results extends AppCompatActivity implements GptChatApiService.ChatGPTResponseListener {

    private final int numResults = 5;     // number of results to return for each query

    private String query;
    private String queryResult;

    private Point userLocation = new Point(44.1, 9.3);
    private Category[] userCategories = {Category.BEACHES};
    private Double range = 20.0;

    // Structure that maps Category to List of Places
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // TODO: parse all parameters and assign to fields
        for (Category category : userCategories) {
            queryGPT(category);
        }
    }

    private void queryGPT(Category category) {
        query =
        "Tell me the %s best %s near these coordinates (%s, %s) in the range of %s kilometers. " +
        "I want as response a numbered list without further descriptions. " +
        "I want all elements in the list to contain {name} - {coordinates (x,y)} - {short description}.";

        query = String.format(query, numResults, category.name().toLowerCase(), userLocation.x, userLocation.y, range);
        Log.d("ChatGPT", "Query: " + query);

        // queryChatGPT should take in input but also return the type
        // In deploy, use this: GptChatApiService.queryChatGPT(query, this);
        onResponseReceived("1. Paraggi Beach - 44.319652, 9.195813 - Small, exclusive cove with turquoise waters and rocky cliffs. \n" +
                "2. Bay of Silence - 44.305145, 9.342084 - Wide, sandy beach with calm waters and a relaxing atmosphere. \n" +
                "3. Sestri Levante Beach - 44.271786, 9.399056 - Popular seaside resort with two sandy beaches and lots of amenities. \n" +
                "4. Lavagna Beach - 44.320249, 9.322719 - Long, pebbly beach with crystal-clear waters and a laid-back vibe. \n" +
                "5. Moneglia Beach - 44.246954, 9.512802 - Picturesque beach with fine sand and clear, shallow waters, great for families.\n");

    }

    // TODO: result has to become two return parameters (response, category)
    @Override
    public void onResponseReceived(String response) {
        Log.d("ChatGPT", "Response: " + response);
        this.queryResult = response;

        Category category = Category.BEACHES; // this should be returned by the callback
        parseQueryResult(category);
    }

    private void parseQueryResult(Category category) {
        Parser parser = new Parser();
        List<Place> places = parser.parseResult(queryResult, category, userLocation);

        TextView tv = findViewById(R.id.textView);
        String viewText = "";
        String tmp = "";
        for (Place place : places) {
            tmp = "Name: " + place.name + "\n" + "Position: (" + place.position.x + ", " + place.position.y + ")\n" + "Description: " + place.description + "\n" + "Distance: " + place.distance + " km\n" + "-----\n";
            viewText += tmp;
        }
        tv.setText(viewText);
    }
}
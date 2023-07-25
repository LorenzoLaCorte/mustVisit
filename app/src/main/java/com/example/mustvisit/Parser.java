package com.example.mustvisit;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static Double[] strToCoordinates(String positionStr) {
        // Define the pattern to match the numbers
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        // Find all matches in the input string
        Matcher matcher = pattern.matcher(positionStr);

        // Initialize an array to store the extracted numbers
        Double[] numbers = new Double[2];

        // Extract the first two numbers found (if any)
        for (int i = 0; i < 2 && matcher.find(); i++) {
            String numberStr = matcher.group();
            numbers[i] = Double.parseDouble(numberStr);
        }

        return numbers;
    }

    public List<Place> parseResult(String result, Category category, Point userLocation) {
        try{
            List<Place> places = new ArrayList<>();

            String[] lines = result.split("\n");
            int i = 1;

            for (String line : lines) {
                line = line.trim();
                if (!line.startsWith(i + ".")) {
                    continue;
                }
                else
                    line = line.substring(3);

                String[] parts = line.split(" - ");
                String name = parts[0].trim();
                Log.d(TAG, "name: " + parts[0].trim());

                String city = parts[1].trim();
                Log.d(TAG, "City: " + parts[1].trim());

                String positionStr = parts[2].trim();
                Log.d(TAG, "PositionSTR: " +parts[2].trim());
                Double[] coordinates = strToCoordinates(positionStr);
                Point placeLocation = new Point(coordinates[0], coordinates[1]);

                String description = parts[3].trim();

                double distance = userLocation.computeDistance(placeLocation);

                Place place = new Place(category, name,city, placeLocation, description, distance);
                places.add(place);

                i++;
            }
            return places;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

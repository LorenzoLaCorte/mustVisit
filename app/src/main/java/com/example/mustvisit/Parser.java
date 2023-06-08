package com.example.mustvisit;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public List<Place> parseResult(String result, Category category, Point userLocation) {
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

            String[] parts = line.split("-");
            String name = parts[0].trim();
            String positionStr = parts[1].trim();

            String[] coordinates = positionStr.split(",");
            double x = Double.parseDouble(coordinates[0].trim());
            double y = Double.parseDouble(coordinates[1].trim());
            Point placeLocation = new Point(x, y);

            String description = parts[2].trim();

            double distance = userLocation.computeDistance(placeLocation);

            Place place = new Place(category, name, placeLocation, description, distance);
            places.add(place);

            i++;
        }

        return places;
    }
}

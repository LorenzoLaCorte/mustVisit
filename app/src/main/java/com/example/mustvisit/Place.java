package com.example.mustvisit;

public class Place {
    protected Category category;
    protected String name;
    protected Point position;
    protected String description;
    protected double distance;

    public Place(Category category, String name, Point position, String description, double distance) {
        this.category = category;
        this.name = name;
        this.position = position;
        this.description = description;
        this.distance = distance;
    }
}

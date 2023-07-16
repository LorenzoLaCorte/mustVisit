package com.example.mustvisit;

public class Place {
    protected Category category;
    protected String name;
    protected String city;
    protected Point position;
    protected String description;
    protected double distance;

    public Place(Category category, String name, String city,Point position, String description, double distance) {
        this.category = category;
        this.name = name;
        this.city=city;
        this.position = position;
        this.description = description;
        this.distance = distance;
    }
}

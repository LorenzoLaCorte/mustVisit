package com.example.mustvisit;

import java.util.ArrayList;
import java.util.List;

public class TopPlaces {
    protected Category category;
    protected String query;
    protected String response;
    protected List<Place> topPlaces = new ArrayList<>();
    protected int retries = 0;

    public TopPlaces(Category category, String query) {
        this.category = category;
        this.query = query;
    }
    public TopPlaces(Category category, String query, String response) {
        this.category = category;
        this.query = query;
        this.response = response;
    }

    public void setTopPlaces(List<Place> topPlaces) {
        this.topPlaces = topPlaces;
    }

    public Boolean tryIncrementingRetries(){
        this.retries += 1;
        this.topPlaces.clear();
        return (this.retries < 3);
    }
}

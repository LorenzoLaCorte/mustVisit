package com.example.mustvisit;

public class Point {
    protected double x;
    protected double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double computeDistance(Point other) {
        double lat1 = Math.toRadians(this.x);
        double lon1 = Math.toRadians(this.y);
        double lat2 = Math.toRadians(other.x);
        double lon2 = Math.toRadians(other.y);

        double d_lat = lat2 - lat1;
        double d_lon = lon2 - lon1;

        double a = Math.pow(Math.sin(d_lat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(d_lon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double earthRadius = 6371.0;
        return earthRadius * c;
    }
}

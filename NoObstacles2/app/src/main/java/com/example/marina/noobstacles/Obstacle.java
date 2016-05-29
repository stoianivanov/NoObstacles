package com.example.marina.noobstacles;

/**
 * Created by Marina on 29/05/2016.
 */
public class Obstacle {

    private double latitude;
    private double longitude;
    private String type;
    private int state;

    public Obstacle(double latitude, double longitude, String type, int state) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.state = state;
    }
}

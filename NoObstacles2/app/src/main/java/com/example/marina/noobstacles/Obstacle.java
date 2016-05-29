package com.example.marina.noobstacles;

/**
 * Created by Marina on 29/05/2016.
 */
public class Obstacle {

    private double latitude;
    private double longitude;
    private String type;
    private int state;

    public Obstacle() {
    }

    public Obstacle(double latitude, double longitude, String type, int state) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.state = state;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Obstacle{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", type='" + type + '\'' +
                ", state=" + state +
                '}';
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

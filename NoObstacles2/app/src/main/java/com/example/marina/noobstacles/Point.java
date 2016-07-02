package com.example.marina.noobstacles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by stoian on 29.05.16.
 */
public class Point {
    private Double lng;
    private Double lat;
    public Point(Double lat, Double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public Point(){

    }
    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
    @Override
    public String toString() {
        return "Point{" +
                "lng=" + lng +
                ", lat=" + lat +
                '}';
    }

    public boolean isProblem(Obstacle obstacle, Point point){
        if (obstacle.getState() == 0){
            if(obstacle.getLatitude() - point.getLat() < new Double(0.01)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
    public Boolean isGoodPoint(Obstacle obstacle, Point point){
        if (obstacle.getState() == 1){
            if(obstacle.getLatitude() - point.getLat() < new Double(0.01)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }


}

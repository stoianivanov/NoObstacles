package com.example.marina.noobstacles;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marina on 29/05/2016.
 */
public class RequestObstacles {

    private List<Obstacle> obstacles;
    private List<Obstacle> noObstacles;
    private List<Obstacle> withObstacles;

    public RequestObstacles() {
        this.obstacles = new ArrayList<>();
        this.noObstacles = new ArrayList<>();
        this.withObstacles = new ArrayList<>();
        new GetObstaclesTask().execute();
    }

    public List<Obstacle> getObstacles() {
        return new ArrayList<>(obstacles);
    }

    public List<Obstacle> getNoObstacles(){
        return noObstacles;
    }
    public List<Obstacle> getWthObstacles(){
        return withObstacles;
    }


    public void initNoObstacles(){
        for (Obstacle obstacle: obstacles) {
            if(obstacle.getState() == 0){
                noObstacles.add(obstacle);
            }
        }
    }
    public void initWithObstacles(){
        for (Obstacle obstacle: obstacles) {
            if(obstacle.getState() == 1){
                withObstacles.add(obstacle);
            }
        }
    }

    private class GetObstaclesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return getObstacles();
        }

        @Override
        protected void onPostExecute(String obstaclesStr) {
            ParseJsonData(obstaclesStr);
            initNoObstacles();
            initWithObstacles();
        }

        private String getObstacles() {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String obstaclesStr = null;

            try {
                URL url = new URL("http://dirigible.eclipse.org//services/js/NoObstaclesApp/obstacles.js");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    obstaclesStr = null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    obstaclesStr = null;
                }

                obstaclesStr = buffer.toString();
            } catch (IOException e) {
                Log.e("Request Obstacles", "Error ", e);
                obstaclesStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Request Obstacles", "Error closing stream", e);
                    }
                }
            }

            return obstaclesStr;
        }

        private void ParseJsonData(String obstaclesStr) {
            JSONArray json = null;
            try {
                json = new JSONArray(obstaclesStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < json.length(); i++) {
                JSONObject obstacleJson = null;
                try {
                    obstacleJson = json.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Obstacle obstacle = new Obstacle();
                double latitude = 0;
                try {
                    latitude = Double.parseDouble(obstacleJson.getString("latitude"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                obstacle.setLatitude(latitude);

                double longitude = 0;
                try {
                    longitude = Double.parseDouble(obstacleJson.getString("longitude"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                obstacle.setLongitude(longitude);

                String type = null;
                try {
                    type = obstacleJson.getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                obstacle.setType(type);

                int state = 1;
                try {
                    state = Integer.parseInt(obstacleJson.getString("state"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                obstacle.setState(state);
                obstacles.add(obstacle);
            }
        }
    }
}



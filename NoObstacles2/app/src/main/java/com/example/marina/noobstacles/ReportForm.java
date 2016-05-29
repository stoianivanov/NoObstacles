package com.example.marina.noobstacles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class ReportForm extends Activity {

    String[] problems = {"Липса на рампа/асансьор",
            "Неработеща рампа/асансьор",
            "Неравности/дупки",
            "Автомобили върху тротоар",
            "Липсващ тротоар",
            "Автобус/трамвай/тролейбус без рампа",
            "Път в ремонт",
            "Временно препятствие",
            "Друго"};

    private String lng;
    private String lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);

        Bundle bundle = getIntent().getExtras();
        lat = bundle.getString("Latitude");
        lng = bundle.getString("Longitude");
        Log.d("Log", bundle.getString("Latitude"));
        Log.d("Log", bundle.getString("Longitude"));

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.report_form_adapter_layout, problems);

        ListView listView = (ListView) findViewById(R.id.problems_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lng);
                TextView textViewProblems = (TextView) findViewById(R.id.problems);
                String type;
                String typeBg = textViewProblems.getText().toString();
                int index = Arrays.asList(problems).indexOf(typeBg);
                switch (index) {
                    case 0:
                        type = "noramp";
                        break;
                    case 1:
                        type = "brkramp";
                        break;
                    case 2:
                        type = "hole";
                        break;
                    case 3:
                        type = "car";
                        break;
                    case 4:
                        type = "pavement";
                        break;
                    case 5:
                        type = "bus";
                        break;
                    case 6:
                        type = "roadblock";
                        break;
                    case 7:
                        type = "tempblock";
                        break;
                    case 8:
                        type = "other";
                        break;
                    default:
                        type = "other";
                        break;
                }

                Log.d("Type", type);
                int state = 0;

                Obstacle obstacle = new Obstacle(latitude, longitude, type, state);
                Gson gsonObstacle = new Gson();
                String jsonObstacle = gsonObstacle.toJson(obstacle);
                new AddObstacleAsync().execute(jsonObstacle);
            }
        });
    }

    private class AddObstacleAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return addObstacle(params[0]);
            } catch (IOException e) {
                String errMessage = e.getMessage();
                return errMessage;
            }
        }

        private String addObstacle(String obstacleToAdd) throws IOException {
            URL baseUrl = new URL("http://dirigible.eclipse.org//services/js/NoObstaclesApp/obstacles.js");
            HttpURLConnection urlConnection = (HttpURLConnection) baseUrl.openConnection();

            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(obstacleToAdd);
            out.close();

            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode > 201) {
                return urlConnection.getResponseMessage();
            }

            InputStream is = urlConnection.getInputStream();
            int numberOfChars;
            StringBuffer sb = new StringBuffer();
            while ((numberOfChars = is.read()) != -1) {
                sb.append((char) numberOfChars);
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            String msg = new String(lat + "|" + lng);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

}

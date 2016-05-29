package com.example.marina.noobstacles;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
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

    String[] problems = {
            "Налична рампа/асансьор",
            "Автобус пригоден за хора с затруднен достъп",
            "Тротоар пригоден за хора с затруднен достъп",
            "Приспособена тоалетна за хора с затруднен достъп",
            "Кръстовище пригодено за хора с затруднен достъп",
            "Заведение за хранене пригодено за хора с затруднен достъп",
            "Липса на рампа/асансьор",
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

        final ListView listView = (ListView) findViewById(R.id.problems_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lng);
                TextView textViewProblems = (TextView) findViewById(R.id.problems);
                String type;

                String typeBg = textViewProblems.getText().toString();
                Log.d("FFFFFFFFF","" +l);
                int index = Arrays.asList(problems).indexOf(l);
                int state = 0;
                switch ((int)l) {
                    case 0:
                        type = "ramp";
                        state = 1;
                        break;
                    case 1:
                        type = "bus";
                        state = 1;
                        break;
                    case 2:
                        type = "aleq";
                        state = 1;
                        break;
                    case 3:
                        type = "wc";
                        state = 1;
                        break;
                    case 4:
                        type = "crsrd";
                        state = 1;
                        break;
                    case 5:
                        type = "dinner";
                        state = 1;
                        break;
                    case 6:
                        type = "noramp";
                        break;
                    case 7:
                        type = "brkramp";
                        break;
                    case 8:
                        type = "hole";
                        break;
                    case 9:
                        type = "car";
                        break;
                    case 10:
                        type = "pavement";
                        break;
                    case 11:
                        type = "bus";
                        break;
                    case 12:
                        type = "roadblock";
                        break;
                    case 13:
                        type = "tempblock";
                        break;
                    case 14:
                        type = "other";
                        break;
                    default:
                        type = "other";
                        break;
                }

                Log.d("Type", type);


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
            finish();
        }
    }
}

package com.example.marina.noobstacles;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

}

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



                String msg = new String(lat + "|" + lng);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

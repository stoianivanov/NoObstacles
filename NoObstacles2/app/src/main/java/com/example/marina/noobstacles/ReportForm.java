package com.example.marina.noobstacles;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReportForm extends Activity {

    String[] problems = {"rado e gei","rado e gei","rado e gei","rado e gei"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);


        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.report_form_adapter_layout, problems);

        ListView listView = (ListView) findViewById(R.id.problems_list);
        listView.setAdapter(adapter);
    }

}

package yeti.mathgym;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Score extends AppCompatActivity {
    private Button clickMe = null;
    private ArrayList<String> notAttemptedOps = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscore);

        Spinner spinner = (Spinner) findViewById(R.id.chooseLevel);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setScore();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ;
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gameLevels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(DataHolder.level);
        spinner.setSelection(spinnerPosition);

        clickMe = (Button) findViewById(R.id.goHome);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LaunchPage.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.navigation, menu);
        MenuItem mItem = menu.findItem(R.id.displaySettings);
        mItem.setVisible(false);
        mItem = menu.findItem(R.id.actionRevise);
        mItem.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = null;
        switch(item.getItemId()){
            case R.id.actionSettings:
                intent = new Intent(this, Setting.class);
                startActivity(intent);
                return true;
            case R.id.actionFeedback:
                intent = new Intent(this, Feedback.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        int index = DataHolder.operationsList.size() - 1;
        int pos = 0;
        Spinner spinnerLevel = (Spinner) findViewById(R.id.chooseLevel);
        String levelSelected = spinnerLevel.getSelectedItem().toString();
        notAttemptedOps.clear();
        for(index = 0; index < DataHolder.operationsList.size(); index++){
            String op = DataHolder.operationsList.get(index);
            try{
                JSONObject obj = DataHolder.score.getJSONObject(levelSelected).getJSONObject(op);
                long total = obj.getLong("total");
                if( total < 1){
                    notAttemptedOps.add(op);
                }
                BarEntry v1e1 = new BarEntry((float)obj.getDouble("percCorrect"), pos);
                valueSet1.add(v1e1);
                v1e1 = new BarEntry((float)obj.getDouble("percTimedCorrect"), pos);
                valueSet2.add(v1e1);
                pos++;
            }catch(Exception e){
            }
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "correct");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        barDataSet1.setBarSpacePercent(0);
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "timedCorrect");
        barDataSet2.setColor(Color.rgb(0,0, 155));
        barDataSet2.setBarSpacePercent(0);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }
    void setScore(){
        HorizontalBarChart chart = (HorizontalBarChart) findViewById(R.id.scoreGraph);

        ArrayList<BarDataSet> dataSet = getDataSet();
        TextView infoOnOps = (TextView) findViewById(R.id.infoOnOps);
        if( notAttemptedOps.size() > 0){
            infoOnOps.setText("Following operations were not attempted: " + notAttemptedOps.toString());
        } else {
            infoOnOps.setText("");
        }
        BarData data = new BarData(DataHolder.operationsList, dataSet);
        chart.setData(data);
        chart.setDescription("Persistent Score");
        chart.animateXY(1000, 1000);
        chart.getLegend();
        chart.invalidate();
    }
}

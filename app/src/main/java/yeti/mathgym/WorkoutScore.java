package yeti.mathgym;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkoutScore extends AppCompatActivity {
    private Button clickMe = null;
    private ArrayList<String> notAttemptedOps = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workoutscore);

        setScore();

        clickMe = (Button) findViewById(R.id.goHome);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LaunchPage.class);
                startActivity(intent);
            }
        });

        clickMe = (Button) findViewById(R.id.goToWorkout);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.navigation, menu);
        MenuItem displaySettings = menu.findItem(R.id.displaySettings);
        displaySettings.setTitle(DataHolder.settings);
        MenuItem mItem = menu.findItem(R.id.actionRevise);
        mItem.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = null;
        switch(item.getItemId()){
            case R.id.actionSettings:
                Toast.makeText(this, "Your current practise Score will be reset on 'Apply' of settings", Toast.LENGTH_LONG).show();
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
        int index = DataHolder.operations.size() - 1;
        int pos = 0;
        String levelSelected = DataHolder.level;
        notAttemptedOps.clear();
        for(; index >= 0; index--){
            String op = DataHolder.operations.get(index);
            try{
                JSONObject obj = DataHolder.practiseScore.getJSONObject(levelSelected).getJSONObject(op);
                long total = obj.getLong("total");
                if( total < 1){
                    notAttemptedOps.add(op);
                }
                double score = obj.getLong("percCorrect");
                BarEntry v1e1 = new BarEntry((float)score, pos);
                valueSet1.add(v1e1);
                score = obj.getLong("percTimedCorrect");
                v1e1 = new BarEntry((float)score, pos);
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
            infoOnOps.setText("Following operations were not atempted (in current setting) : " + notAttemptedOps.toString());
        } else {
            infoOnOps.setText("");
        }

        chart.setVisibility(View.VISIBLE);
        ArrayList<String> reversedOperations = (ArrayList<String>) DataHolder.operations.clone();
        Collections.reverse(reversedOperations);
        BarData data = new BarData(reversedOperations, getDataSet());
        chart.setData(data);
        chart.setDescription("Persistent Score");
        chart.animateXY(1000, 1000);
        chart.getLegend();
        chart.invalidate();
    }
}

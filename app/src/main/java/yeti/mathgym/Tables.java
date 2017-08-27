package yeti.mathgym;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tables extends AppCompatActivity {
    private Button clickMe = null;
    private EditText multiplier = null;
    private TextView multiplierInfo = null;
    private int multiplierInt = 0;
    private List<TextView> tableEntries = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        multiplier = (EditText) findViewById(R.id.multiplier);

        multiplierInfo = (TextView) findViewById(R.id.multiplierInfo);

        clickMe = (Button) findViewById(R.id.getTables);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTables();
            }
        });

        clickMe = (Button) findViewById(R.id.back);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tableEntries = Arrays.asList(
                (TextView) findViewById(R.id.mul1),
                (TextView) findViewById(R.id.mul2),
                (TextView) findViewById(R.id.mul3),
                (TextView) findViewById(R.id.mul4),
                (TextView) findViewById(R.id.mul5),
                (TextView) findViewById(R.id.mul6),
                (TextView) findViewById(R.id.mul7),
                (TextView) findViewById(R.id.mul8),
                (TextView) findViewById(R.id.mul9),
                (TextView) findViewById(R.id.mul10),
                (TextView) findViewById(R.id.mul11),
                (TextView) findViewById(R.id.mul12),
                (TextView) findViewById(R.id.mul13),
                (TextView) findViewById(R.id.mul14),
                (TextView) findViewById(R.id.mul15),
                (TextView) findViewById(R.id.mul16),
                (TextView) findViewById(R.id.mul17),
                (TextView) findViewById(R.id.mul18),
                (TextView) findViewById(R.id.mul19),
                (TextView) findViewById(R.id.mul20)
        );
        clearTables();
    }
    @Override
    public void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    public boolean validateMultiplierData(){
        String multiplierData = multiplier.getText().toString();
        if( multiplierData.isEmpty()){
            multiplierInfo.setText("enter a multipler");
            return false;
        }
        try{
            multiplierInt = Integer.parseInt(multiplierData);
            if( (multiplierInt < 1) || (multiplierInt > 25)){
                multiplierInfo.setText("enter a whole number between 1 - 25");
                return false;
            }
        }
        catch (Exception e){
            multiplierInfo.setText("enter a valid whole number between 1 - 25");
            return false;
        }
        multiplierInfo.setText("");
        multiplierInfo.setVisibility(View.GONE);
        return true;
    }
    public void clearTables() {
        for(int i = 0; i < 20; i++){
            tableEntries.get(i).setText("");
        }
    }
    public void getTables(){
        clearTables();

        if( validateMultiplierData() == false ){
            multiplierInfo.setVisibility(View.VISIBLE);
            return;
        }
        List<Integer> tableValue = Arrays.asList(
                multiplierInt * 1,
                multiplierInt * 2,
                multiplierInt * 3,
                multiplierInt * 4,
                multiplierInt * 5,
                multiplierInt * 6,
                multiplierInt * 7,
                multiplierInt * 8,
                multiplierInt * 9,
                multiplierInt * 10,
                multiplierInt * 11,
                multiplierInt * 12,
                multiplierInt * 13,
                multiplierInt * 14,
                multiplierInt * 15,
                multiplierInt * 16,
                multiplierInt * 17,
                multiplierInt * 18,
                multiplierInt * 19,
                multiplierInt * 20
        );
        String dataToShow = "";
        int index = 0;
        for(int i = 0; i < 20; i++){
            index = i+1;
            dataToShow = Integer.toString(multiplierInt) + " X " + Integer.toString(index) + " = " + Integer.toString(multiplierInt * index);
            tableEntries.get(i).setText(dataToShow);
        }
    }
}

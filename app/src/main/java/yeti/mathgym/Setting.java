package yeti.mathgym;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class Setting extends AppCompatActivity {
    private boolean resetScores = false;
    private boolean allowApprox = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //set the level dropdown correctly
        Spinner spinner = (Spinner) findViewById(R.id.chooseLevel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gameLevels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(DataHolder.level);
        spinner.setSelection(spinnerPosition);

        //make sure the current operations are selected by default
        CheckBox checkBox = (CheckBox) findViewById(R.id.addSelected);
        checkBox.setChecked(DataHolder.hasAdd);
        checkBox = (CheckBox) findViewById(R.id.subSelected);
        checkBox.setChecked(DataHolder.hasSub);
        checkBox = (CheckBox) findViewById(R.id.mulSelected);
        checkBox.setChecked(DataHolder.hasMul);
        checkBox = (CheckBox) findViewById(R.id.divSelected);
        checkBox.setChecked(DataHolder.hasDiv);
        checkBox = (CheckBox) findViewById(R.id.percSelected);
        checkBox.setChecked(DataHolder.hasPerc);
        TextView levelInfo = (TextView) findViewById(R.id.levelInfo);
        levelInfo.setText(DataHolder.settings);

        //enable the operation on toggle switch for clear scores
        final ToggleButton resetScoreToggle = (ToggleButton) findViewById(R.id.resetScore);
        resetScoreToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    resetScores = true;
                } else {
                    resetScores = false;
                }
            }
        });

        ToggleButton approxScoreToggle = (ToggleButton) findViewById(R.id.allowApprox);
        allowApprox = DataHolder.canBeApproximate;
        approxScoreToggle.setChecked(allowApprox);
        approxScoreToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked != DataHolder.canBeApproximate){
                    if(isChecked){
                        allowApprox = true;
                    } else {
                        allowApprox = false;
                    }
                    resetScoreToggle.setChecked(true);
                    resetScoreToggle.setEnabled(false);
                } else {
                    resetScoreToggle.setChecked(false);
                    resetScoreToggle.setEnabled(true);
                }
            }
        });

        //enable the apply button
        Button clickMe = (Button) findViewById(R.id.applySetting);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndUpdateSetting();
            }
        });

        clickMe = (Button) findViewById(R.id.cancelSetting);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.navigation, menu);
        MenuItem mItem = menu.findItem(R.id.actionSettings);
        mItem.setVisible(false);
        mItem = menu.findItem(R.id.actionRevise);
        mItem.setVisible(false);
        mItem = menu.findItem(R.id.displaySettings);
        mItem.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = null;
        switch(item.getItemId()){
            case R.id.actionFeedback:
                intent = new Intent(this, Feedback.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private boolean validateSetting(){
        boolean atleastOneOpSelected = false;
        CheckBox checked = (CheckBox) findViewById(R.id.addSelected);
        if(checked.isChecked()){
            atleastOneOpSelected = true;
        }
        checked = (CheckBox) findViewById(R.id.subSelected);
        if(checked.isChecked()){
            atleastOneOpSelected = true;
        }
        checked = (CheckBox) findViewById(R.id.mulSelected);
        if(checked.isChecked()){
            atleastOneOpSelected = true;
        }
        checked = (CheckBox) findViewById(R.id.divSelected);
        if(checked.isChecked()){
            atleastOneOpSelected = true;
        }
        checked = (CheckBox) findViewById(R.id.percSelected);
        if(checked.isChecked()){
            atleastOneOpSelected = true;
        }
        return atleastOneOpSelected;
    }
    private void saveAndUpdateSetting(){
        boolean settingsValid = validateSetting();

        if(settingsValid == false){
            TextView settingsValidText = (TextView) findViewById(R.id.settingsValid);
            settingsValidText.setVisibility(View.VISIBLE);
            return;
        } else {
            TextView settingsValidText = (TextView) findViewById(R.id.settingsValid);
            settingsValidText.setVisibility(View.GONE);
        }

        Spinner spinnerLevel = (Spinner) findViewById(R.id.chooseLevel);
        String levelSelected = spinnerLevel.getSelectedItem().toString();
        DataHolder.level = levelSelected;


        CheckBox checked = (CheckBox) findViewById(R.id.addSelected);
        DataHolder.operations.clear();
        String displaySetting = DataHolder.level;
        if(checked.isChecked()){
            DataHolder.hasAdd = true;
            DataHolder.operations.add("add");
            displaySetting += " add";
        } else {
            DataHolder.hasAdd = false;
        }
        checked = (CheckBox) findViewById(R.id.subSelected);
        if(checked.isChecked()){
            DataHolder.hasSub = true;
            DataHolder.operations.add("sub");
            displaySetting += " sub";
        } else {
            DataHolder.hasSub = false;
        }
        checked = (CheckBox) findViewById(R.id.mulSelected);
        if(checked.isChecked()){
            DataHolder.hasMul = true;
            DataHolder.operations.add("mul");
            displaySetting += " mul";
        } else {
            DataHolder.hasMul = false;
        }
        checked = (CheckBox) findViewById(R.id.divSelected);
        if(checked.isChecked()){
            DataHolder.hasDiv = true;
            DataHolder.operations.add("div");
            displaySetting += " div";
        } else {
            DataHolder.hasDiv = false;
        }
        checked = (CheckBox) findViewById(R.id.percSelected);
        if(checked.isChecked()){
            DataHolder.hasPerc = true;
            DataHolder.operations.add("perc");
            displaySetting += " perc";
        } else {
            DataHolder.hasPerc = false;
        }

        if( allowApprox == true){
            displaySetting = "approx " + displaySetting;
        }
        DataHolder.canBeApproximate = allowApprox;

        DataHolder.settings = displaySetting;

        if( resetScores == true ){
            try{
                DataHolder.score = new JSONObject(DataHolder.defaultScore);
                DataHolder.practiseScore = new JSONObject(DataHolder.defaultScore);
                DataHolder.bestOperations = "";
                DataHolder.bestOperationLevel = "";
                DataHolder.bestOperationScore = 0.0;
            } catch(Exception e){
            }
        }
        updateInPreferences();
        TextView levelInfo = (TextView) findViewById(R.id.levelInfo);
        finish();
    }
    private void updateInPreferences(){
        SharedPreferences preferences = getSharedPreferences(DataHolder.prefFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("level", DataHolder.level);
        editor.putBoolean("hasAdd", DataHolder.hasAdd);
        editor.putBoolean("hasSub", DataHolder.hasSub);
        editor.putBoolean("hasMul", DataHolder.hasMul);
        editor.putBoolean("hasDiv", DataHolder.hasDiv);
        editor.putBoolean("hasPerc", DataHolder.hasPerc);
        editor.putBoolean("allowApprox", DataHolder.canBeApproximate);
        if( resetScores ){
            editor.putString("score", DataHolder.score.toString());
            editor.putString("bestOperations", "");
            editor.putString("bestOperationLevel", "");
        }
        editor.commit();
    }
}

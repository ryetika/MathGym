package yeti.mathgym;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

public class LaunchPage extends AppCompatActivity {
    private Button clickMe = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readStoredPreferences();
        setContentView(R.layout.activity_launch);
        clickMe = (Button) findViewById(R.id.startWorkout);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPractiseScore();
                Intent intent = new Intent(v.getContext(), Game.class);
                startActivity(intent);
            }
        });

        clickMe = (Button) findViewById(R.id.settings);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Setting.class);
                startActivity(intent);
            }
        });

        clickMe = (Button) findViewById(R.id.viewScore);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Score.class);
                startActivity(intent);
            }
        });

        clickMe = (Button) findViewById(R.id.tables);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Tables.class);
                startActivity(intent);
            }
        });

        clickMe = (Button) findViewById(R.id.aboutYeti);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), About.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        invalidateOptionsMenu();
        setBestOperations();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        MenuItem mItem = menu.findItem(R.id.actionSettings);
        mItem.setVisible(false);
        mItem = menu.findItem(R.id.actionRevise);
        mItem.setVisible(false);
        MenuItem displaySettings = menu.findItem(R.id.displaySettings);
        displaySettings.setTitle(DataHolder.settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.actionFeedback:
                intent = new Intent(this, Feedback.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String generateDefaultScore() {
        String defaultScore = "{";
        for (String level : DataHolder.levels) {
            defaultScore += "\"" + level + "\":{";
            for (String opName : DataHolder.operationsList) {
                defaultScore += "\"" + opName + "\":{";
                for (String scoreName : DataHolder.scoresList) {
                    defaultScore += "\"" + scoreName + "\":0,";
                }
                defaultScore = defaultScore.replaceFirst(",$", "");
                defaultScore += "},";
            }
            defaultScore = defaultScore.replaceFirst(",$", "");
            defaultScore += "},";
        }
        defaultScore = defaultScore.replaceFirst(",$", "");
        defaultScore += "}";
        return defaultScore;
    }

    private void readStoredPreferences() {
        if (DataHolder.initialized) {
            return;
        }
        DataHolder.initialized = true;
        DataHolder.defaultScore = generateDefaultScore();
        SharedPreferences preferences = getSharedPreferences(DataHolder.prefFile, Context.MODE_PRIVATE);
        DataHolder.level = preferences.getString("level", "easy");
        DataHolder.hasAdd = preferences.getBoolean("hasAdd", true);
        DataHolder.hasSub = preferences.getBoolean("hasSub", false);
        DataHolder.hasMul = preferences.getBoolean("hasMul", false);
        DataHolder.hasDiv = preferences.getBoolean("hasDiv", false);
        DataHolder.hasPerc = preferences.getBoolean("hasPerc", false);
        DataHolder.bestOperations = preferences.getString("bestOperations", "");
        DataHolder.bestOperationLevel = preferences.getString("bestOperationLevel", "");
        DataHolder.bestOperationScore = preferences.getFloat("bestOperationScore", (float)0.0);
        DataHolder.canBeApproximate = preferences.getBoolean("allowApprox", false);
        try {
            DataHolder.practiseScore = new JSONObject(DataHolder.defaultScore);
            String stringifiedScores = preferences.getString("score", DataHolder.defaultScore);
            DataHolder.score = new JSONObject(stringifiedScores);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataHolder.operations.clear();
        String displaySetting = DataHolder.level;
        if (DataHolder.hasAdd) {
            displaySetting += " add";
            DataHolder.operations.add("add");
        }
        if (DataHolder.hasSub) {
            displaySetting += " sub";
            DataHolder.operations.add("sub");
        }
        if (DataHolder.hasMul) {
            displaySetting += " mul";
            DataHolder.operations.add("mul");
        }
        if (DataHolder.hasDiv) {
            displaySetting += " div";
            DataHolder.operations.add("div");
        }
        if (DataHolder.hasPerc) {
            displaySetting += " perc";
            DataHolder.operations.add("perc");
        }
        if( DataHolder.canBeApproximate == true){
            displaySetting = "approx " + displaySetting;
        }
        DataHolder.settings = displaySetting;
    }

    private void resetPractiseScore() {
        try {
            DataHolder.practiseScore = new JSONObject(DataHolder.defaultScore);
        } catch (Exception e) {
        }
    }

    public void setBestOperations() {
        String bestOpsVal = DataHolder.bestOperations;
        LinearLayout bestOpsGroup = (LinearLayout) findViewById(R.id.bestOpGroup);
        if (bestOpsVal.isEmpty()) {
            bestOpsGroup.setVisibility(View.GONE);
        } else {
            bestOpsGroup.setVisibility(View.VISIBLE);
            TextView bestOps = (TextView) findViewById(R.id.bestOps);
            bestOps.setText(DataHolder.bestOperations);
        }
    }
}

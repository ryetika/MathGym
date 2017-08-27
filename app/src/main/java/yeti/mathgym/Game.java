package yeti.mathgym;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game extends AppCompatActivity {
    private Button clickMe = null;
    private TextView answerSym = null;
    private TextView answerVal = null;
    private TextView numVal = null;
    private TextView qPart1 = null;
    private TextView qSym = null;
    private TextView qPart2 = null;
    private TextView tickStatus = null;
    private TextView attempted = null;
    private TextView correctAnswer = null;
    private ImageView time = null;
    private ImageView tick = null;
    private ToggleButton ansSign = null;
    private Button submit = null;

    private JSONObject operandSetting = null;
    private JSONObject currentQuestion = new JSONObject();
    private boolean typeLtoR = true;
    private boolean answerIsNeg = false;
    private boolean canBeApproximate = false;
    private boolean enterAsNum = false;
    private boolean digitOne = true;
    private static long startTime = 0;
    private short bestOpAttemptThreshold = 10;
    private short bestOpWeight  = 80;
    private short bestOpScoreThreshold = (short) (bestOpWeight /2);//bestOpWeight * 2 is the max score

    private enum CorrectStatus {Correct, Wrong, Approx};
    Random randomNumGenerator = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setLevelAndOperations();

        clickMe = (Button) findViewById(R.id.submit);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        clickMe = (Button) findViewById(R.id.nextQuestion);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewQuestion();
            }
        });

        clickMe = (Button) findViewById(R.id.viewScore);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WorkoutScore.class);
                startActivity(intent);
            }
        });

        clickMe = (Button) findViewById(R.id.goHome);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Map<Integer, String> digitPanel = new HashMap<Integer, String>();
        digitPanel.put(R.id.zero, "0");
        digitPanel.put(R.id.one, "1");
        digitPanel.put(R.id.two, "2");
        digitPanel.put(R.id.three, "3");
        digitPanel.put(R.id.four, "4");
        digitPanel.put(R.id.five, "5");
        digitPanel.put(R.id.six, "6");
        digitPanel.put(R.id.seven, "7");
        digitPanel.put(R.id.eight, "8");
        digitPanel.put(R.id.nine, "9");
        digitPanel.put(R.id.dot, ".");
        Iterator it = digitPanel.entrySet().iterator();
        while(it.hasNext()){
            final Map.Entry entry = (Map.Entry)it.next();
            clickMe = (Button) findViewById((int)entry.getKey());
            clickMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputAnswer((String) entry.getValue());
                }
            });
        }

        clickMe = (Button) findViewById(R.id.clear);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAnswer();
            }
        });

        clickMe = (Button) findViewById(R.id.del);
        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOneCharInAnswer();
            }
        });

        final ToggleButton enterNum = (ToggleButton) findViewById(R.id.asNum);
        ToggleButton toggleDir = (ToggleButton) findViewById(R.id.toggleDir);
        toggleDir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    typeLtoR = false;
                    enterNum.setVisibility(View.VISIBLE);
                    enterNum.setChecked(false);
                } else {
                    typeLtoR = true;
                    enterNum.setVisibility(View.GONE);
                }
                enterAsNum = false;
            }
        });

        ansSign = (ToggleButton) findViewById(R.id.toggleNeg);
        ansSign.setChecked(false);
        ansSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    answerIsNeg = true;
                } else {
                    answerIsNeg = false;
                }
                setAnswerSign();
            }
        });

        enterNum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    enterAsNum = true;
                    digitOne = true;
                } else {
                    enterAsNum = false;
                 }
            }
        });

        attempted.setText("0");
    }
    @Override
    public void onResume() {
        invalidateOptionsMenu();
        TextView timeLimit = (TextView) findViewById(R.id.timeLimit);
        try{
            timeLimit.setText(Integer.toString(operandSetting.getJSONObject(DataHolder.level).getInt("timeout")) + " secs");
        }catch(Exception e){
            ;
        }
        getNewQuestion();
        super.onResume();
    }
    @Override
    protected void onStop(){
        updateScore();
        super.onStop();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.navigation, menu);
        MenuItem displaySettings = menu.findItem(R.id.displaySettings);
        displaySettings.setTitle(DataHolder.settings);
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
            case R.id.actionRevise:
                intent = new Intent(this, Tables.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void keypadEnable(boolean enable){
        ArrayList<Integer> digitPanel = new ArrayList<Integer>();
        digitPanel.add(R.id.zero);
        digitPanel.add(R.id.one);
        digitPanel.add(R.id.two);
        digitPanel.add(R.id.three);
        digitPanel.add(R.id.four);
        digitPanel.add(R.id.five);
        digitPanel.add(R.id.six);
        digitPanel.add(R.id.seven);
        digitPanel.add(R.id.eight);
        digitPanel.add(R.id.nine);
        digitPanel.add(R.id.del);
        digitPanel.add(R.id.clear);
        digitPanel.add(R.id.submit);
        digitPanel.add(R.id.dot);
        for(Integer keyEle : digitPanel){
            clickMe = (Button) findViewById((int)keyEle);
            clickMe.setEnabled(enable);
        }

        ansSign.setEnabled(enable);

        if(enable == true){
            submit.setEnabled(false);
        }
    }
    private void inputAnswer(String value){
        if( (value == null) || value.isEmpty() ){
            return;
        }
        boolean isDot = false;
        String answerStr = answerVal.getText().toString();
        if( value.indexOf('.') > -1 ){
            isDot = true;
            if( answerStr.indexOf('.') > -1 ){
                return;
            }
        }else{
            if(submit.isEnabled() == false){
                submit.setEnabled(true);
            }
        }
        boolean checkSign = false;
        if( (answerStr == null) || answerStr.isEmpty() ){
            checkSign = true;
        }
        if(typeLtoR){
            answerStr += value;
        } else {
            if( enterAsNum && !isDot){
                if( digitOne){
                    answerStr = value + " " + answerStr;
                    submit.setEnabled(false);
                    digitOne = false;
                } else {
                    int firstDigit = answerStr.charAt(0) - '0';
                    if( firstDigit == 0){
                        answerStr = value + answerStr.substring(2);
                    } else {
                        answerStr = answerStr.charAt(0) + value + answerStr.substring(2);
                    }
                    submit.setEnabled(true);
                    digitOne = true;
                }
            } else {
                answerStr = value + answerStr;
            }
        }
        answerVal.setText(answerStr);
        if(checkSign){
            setAnswerSign();
        }
    }
    private void clearAnswer(){
        answerVal.setText("");
        answerSym.setText("");
        digitOne = true;
        submit.setEnabled(false);
        ansSign.setChecked(false);
    }
    private void clearOneCharInAnswer(){
        String answerStr = answerVal.getText().toString();
        if( (answerStr == null) || answerStr.isEmpty()){
            return;
        }
        if(answerStr.length() < 2){
            clearAnswer();
        }
        if(typeLtoR){
            answerStr = answerStr.substring(0,answerStr.length()-1);
        }else{
            if( enterAsNum && (digitOne == false)){
                answerStr = answerStr.substring(2);
                digitOne = true;
                submit.setEnabled(true);
            } else {
                answerStr = answerStr.substring(1);
            }
        }
        answerVal.setText(answerStr);
    }
    private void setAnswerSign(){
        String answerStr = answerVal.getText().toString();
        if( (answerStr == null) || answerStr.isEmpty()){
            return;
        }
        if(answerIsNeg){
            answerSym.setText("-");
        } else {
            answerSym.setText("");
        }
    }
    private boolean updateBest(){
        if( DataHolder.bestOperationLevel.isEmpty()){
            return true;
        }
        if( DataHolder.bestOperationLevel.equals(DataHolder.level) ){
           return true;
        }
        if( DataHolder.bestOperationLevel.toLowerCase().equals("medium")){
            if( DataHolder.level.toLowerCase().equals("hard")){
                return true;
            }
        }
        if( DataHolder.bestOperationLevel.toLowerCase().equals("easy")){
            return true;
        }
        return false;
    }
    private void updateScore() {
        try{
            JSONObject obj_score = null;
            JSONObject obj_practiseScore = null;
            long correct = 0;
            long timedCorrect = 0;
            long total = 0;
            double correctScore = 0.0;
            double timedCorrectScore = 0.0;
            boolean updateBest = updateBest();
            double maxTotal = 0.0;
            HashMap<String, ArrayList<Double>> opScore = new HashMap<String, ArrayList<Double>>();
            for(String op : DataHolder.operationsList){
                obj_score = DataHolder.score.getJSONObject(DataHolder.level).getJSONObject(op);
                obj_practiseScore = DataHolder.practiseScore.getJSONObject(DataHolder.level).getJSONObject(op);
                total = obj_score.getLong("total") + obj_practiseScore.getLong("total");
                obj_score.put("total", total);
                if(obj_practiseScore.getLong("total") < 1){
                    continue;
                }
                correct = obj_score.getLong("correct") + obj_practiseScore.getLong("correct");
                obj_score.put("correct", correct);
                correctScore = ((double) correct/ (double) total) * 100.0;
                correctScore = Math.round( correctScore * 100.0) / 100.0;
                obj_score.put("percCorrect", correctScore);
                timedCorrect = obj_score.getLong("timedCorrect") + obj_practiseScore.getLong("timedCorrect");
                obj_score.put("timedCorrect", timedCorrect);
                timedCorrectScore = ((double) timedCorrect/ (double) total) * 100.0;
                timedCorrectScore = Math.round( timedCorrectScore * 100.0) / 100.0;
                obj_score.put("percTimedCorrect", timedCorrectScore);
                DataHolder.score.getJSONObject(DataHolder.level).put(op, obj_score);
                if( updateBest){
                    if( total > bestOpAttemptThreshold ){
                        correctScore = ((double) correctScore/ (double) 100) * (bestOpWeight/2);
                        correctScore = Math.round( correctScore * 100.0) / 100.0;
                        timedCorrectScore = ((double) timedCorrectScore/ (double) 100) * bestOpWeight;
                        timedCorrectScore = Math.round( timedCorrectScore * 100.0) / 100.0;
                        ArrayList<Double> bestOpScores = new ArrayList<Double>();
                        bestOpScores.add(correctScore + timedCorrectScore);
                        bestOpScores.add((double)total);
                        opScore.put(op, bestOpScores);
                        if( maxTotal < total){
                            maxTotal = total;
                        }
                    }
                }
            }

            if( updateBest ){
                double bestScore = 0.0;
                HashMap<String, Double> bestFinalScore = new HashMap<String, Double>();
                for( Map.Entry<String, ArrayList<Double>> entry : opScore.entrySet()){
                    double marks = entry.getValue().get(0);
                    double attendance = entry.getValue().get(1);
                    double finalScore = marks + ((attendance / maxTotal)* (bestOpWeight/2));
                    finalScore = Math.round( finalScore * 100.0) / 100.0;
                    if( bestScore < finalScore ){
                        bestScore = finalScore;
                    }
                    bestFinalScore.put(entry.getKey(), finalScore);
                }
                String bestOpStr = "";
                if( bestScore >= bestOpScoreThreshold ){
                    if( DataHolder.bestOperationLevel == DataHolder.level){
                        if( bestScore < DataHolder.bestOperationScore ){
                            updateBest = false;
                        }
                    }
                    if( updateBest ){
                        for( Map.Entry<String, Double> entry : bestFinalScore.entrySet()){
                            if( entry.getValue() >= bestScore){
                                bestOpStr = bestOpStr + "," + entry.getKey();
                            }
                        }
                        if( !bestOpStr.isEmpty()){
                            bestOpStr = bestOpStr.substring(1);
                        }
                        DataHolder.bestOperationLevel = DataHolder.level;
                        DataHolder.bestOperations = bestOpStr;
                        DataHolder.bestOperationScore = bestScore;
                    }
                } else {
                    updateBest = false;
                }
            }

            SharedPreferences preferences = getSharedPreferences(DataHolder.prefFile, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("score", DataHolder.score.toString());
            if( updateBest ){
                editor.putString("bestOperations", DataHolder.bestOperations);
                editor.putString("bestOperationLevel", DataHolder.bestOperationLevel);
                editor.putFloat("bestOperationScore", (float)DataHolder.bestOperationScore);
            }
            editor.commit();
        }catch( ArithmeticException ae){
            // need to reset scores
        }catch( Exception e){
        }
    }
    private void setLevelAndOperations() {
        answerVal = (TextView) findViewById(R.id.answerVal);
        answerSym = (TextView) findViewById(R.id.answerSym);
        qPart1= (TextView) findViewById(R.id.qPart1);
        qSym= (TextView) findViewById(R.id.qSym);
        qPart2= (TextView) findViewById(R.id.qPart2);
        correctAnswer = (TextView) findViewById(R.id.correctAnswer);
        time = (ImageView) findViewById(R.id.time);
        tick = (ImageView) findViewById(R.id.tick);
        submit = (Button) findViewById(R.id.submit);
        tickStatus = (TextView) findViewById(R.id.tickStatus);
        attempted = (TextView) findViewById(R.id.attempted);
        canBeApproximate = DataHolder.canBeApproximate;
        //update questionSetting object with the correct level and operation bags
        try {
            operandSetting = new JSONObject(
                    "{\"easy\":{\"timeout\":30, \"add\":{\"op1\":2,\"op2\":2},\"sub\":{\"op1\":2,\"op2\":2},\"mul\":{\"op1\":2,\"op2\":1},\"div\":{\"op1\":2,\"op2\":1},\"perc\":{\"op1\":2,\"op2\":3}},\"medium\":{\"timeout\":20, \"add\":{\"op1\":3,\"op2\":3},\"sub\":{\"op1\":3,\"op2\":3},\"mul\":{\"op1\":2,\"op2\":2},\"div\":{\"op1\":3,\"op2\":1},\"perc\":{\"op1\":2,\"op2\":3}},\"hard\":{\"timeout\":20, \"add\":{\"op1\":4,\"op2\":4},\"sub\":{\"op1\":4,\"op2\":4},\"mul\":{\"op1\":4,\"op2\":2},\"div\":{\"op1\":4,\"op2\":2},\"perc\":{\"op1\":2,\"op2\":4}}}"
            );
        } catch (Exception e) {
            ;
        }
    }
    private void getNewQuestion() {
        // hide next question button
        clickMe = (Button) findViewById(R.id.nextQuestion);
        clickMe.setEnabled(false);

        //set neutral images
        resetImages();

        //reset all variable text
        clearAnswer();
        qPart1.setText("");
        qSym.setText("");
        qPart2.setText("");
        correctAnswer.setText("");

        //get New question
        setQuestion();

        keypadEnable(true);

        //start timer
        startTime = System.currentTimeMillis();
    }
    private int getRandomNum(int digits){
        int limit = 1;
        for(int i = 0; i < digits; i++){
            limit *= 10;
        }
        int number = 0;
        while( 0 == number){
            number = randomNumGenerator.nextInt(limit);
        }
        return number;
    }
    private int getRandomNumWithLimit(int limit){
        int number = 0;
        while( 0 == number){
            number = randomNumGenerator.nextInt(limit);
        }
        return number;
    }
    private int getEasyPerc(){
        // for the easy format we want x% of something, such that, the x is from th foll list
        List<Integer> possibleOptions = Arrays.asList(5, 10, 20, 25, 50);
        int operandIndex = randomNumGenerator.nextInt(possibleOptions.size());
        return possibleOptions.get(operandIndex);
    }
    private int getMedHardPerc(){
        // for the easy format we want x% of something, such that, the x is from th foll list
        List<Integer> possibleOptions = Arrays.asList(5, 10, 20, 25, 30, 40, 50, 60, 70, 75, 80, 90);
        int operandIndex = randomNumGenerator.nextInt(possibleOptions.size());
        return possibleOptions.get(operandIndex);
    }
    private void setQuestion() {
        // based on the valid operations and level of the game, get a random question
        try{
            String level = DataHolder.level;
            int operationIndex = randomNumGenerator.nextInt(DataHolder.operations.size());
            String operation = DataHolder.operations.get(operationIndex);
            int operandSize1 = operandSetting.getJSONObject(level).getJSONObject(operation).getInt("op1");
            int operand1 = getRandomNum(operandSize1);
            int operandSize2 = operandSetting.getJSONObject(level).getJSONObject(operation).getInt("op2");
            int operand2 = getRandomNum(operandSize2);
            String questionStr = "";
            String answerVal = "";
            String answerStr = "The correct answer is";
            String symStr = "";
            switch( operation ){
                case "add":
                    symStr = "+";
                    answerVal = Integer.toString((operand1 + operand2));
                    break;
                case "sub":
                    int ansSub = operand1 - operand2;
                    switch( level ){
                        case "easy":
                            while( ansSub < 1 ){
                                operand1 = getRandomNum(operandSize1);
                                ansSub = operand1 - operand2;
                            }
                            break;
                        case "medium":
                        case "hard":
                            break;
                        default:
                            break;
                    }
                    symStr = "-";
                    answerVal = Integer.toString((operand1 - operand2));
                    break;
                case "mul":
                    symStr = "X";
                    answerVal = Integer.toString((operand1 * operand2));
                    break;
                case "div":
                    double ansDiv = (double)operand1 / (double)operand2;
                    switch( level ){
                        case "easy":
                        case "medium":
                            while( ansDiv < 1 ){
                                operand1 = getRandomNum(operandSize1);
                                ansDiv = (double) operand1 / (double) operand2;
                            }
                            if( (ansDiv  % 1.0) != 0 ){
                                operand1 = (int) Math.round(ansDiv) * operand2;
                            }
                            break;
                        case "hard":
                            break;
                        default:
                            break;
                    }
                    symStr = "/";
                    answerVal = Double.toString(Math.round(((double)operand1 / (double)operand2)* 100.0) / 100.0);
                    answerStr += " (rounded to 2 decimal places)";
                    break;
                case "perc":
                    operand2 = getRandomNum(operandSize2);
                    double ansPerc = 0;
                    switch( level ){
                        case "easy":
                            operand1 = getEasyPerc();
                            ansPerc = (((double)operand1 / 100.0) * (double)operand2);
                            while( ansPerc < 1){
                                operand2 = getRandomNum(operandSize2);
                                ansPerc = (((double)operand1 / 100.0) * (double)operand2);
                            }
                            if((ansPerc  % 1.0) != 0 ){
                                operand2 = (int) Math.round(ansPerc) * 100 / operand1;
                            }
                            break;
                        case "medium":
                            operand1 = getMedHardPerc();
                            ansPerc = (((double)operand1 / 100.0) * (double)operand2);
                            while( ansPerc < 1){
                                operand2 = getRandomNum(operandSize2);
                                ansPerc = (((double)operand1 / 100.0) * (double)operand2);
                            }
                            if((ansPerc  % 1.0) != 0 ){
                                operand2 = (int) Math.round(ansPerc) * 100 / operand1;
                            }
                            break;
                        case "hard":
                            operand1 = getMedHardPerc();
                            operand2 = getRandomNum(operandSize2);
                            break;
                        default:
                            break;
                    }
                    symStr = "% of";
                    answerVal = Double.toString(Math.round((((double)operand1 / 100.0) * (double)operand2)* 100.0) / 100.0);
                    answerStr += " (rounded to 2 decimal places)";
                    break;
                default :
                    return;
            }
            answerStr += " : " + answerVal;
            questionStr = operand1 + symStr + operand2;
            currentQuestion.put("operation", operation);
            currentQuestion.put("question", questionStr);
            currentQuestion.put("answer", answerVal);
            currentQuestion.put("answerStr", answerStr);

            //set the question
            String dummy = "";
            dummy += operand1;
            qPart1.setText(dummy);
            qSym.setText(symStr);
            dummy = "" + operand2;
            qPart2.setText(dummy);
        }catch(Exception e){
            return;
        }
    }
    private void resetImages(){
        time.setImageResource(R.drawable.safetime);
        tick.setImageResource(R.drawable.safecheck);
        tickStatus.setText("");
    }
    private void setImages(CorrectStatus correctStatus, boolean isOnTime){
        //based on correct flag set the tick mark and cross mark correctly
        if(correctStatus == CorrectStatus.Correct){
            tick.setImageResource(R.drawable.goodcheck);
        } else if(correctStatus == CorrectStatus.Approx){
            tick.setImageResource(R.drawable.approxcheck);
        } else {
            tick.setImageResource(R.drawable.dangerx);
        }
        tickStatus.setText(correctStatus.toString());

        if(isOnTime == true){
            time.setImageResource(R.drawable.goodtime);
        } else {
            time.setImageResource(R.drawable.dangertime);
        }
    }
    private short numberOfWholeDigits(double number){
        number = Math.abs(number);
        short numOfDigits = 0;
        double divisor = 10.0;
        int wholeNum = (int)(number - (number % 1));
        while(wholeNum > 0){
            wholeNum = (int)(wholeNum / divisor);
            numOfDigits++;
        }
        return numOfDigits;
    }
    private short numberOfFractDigits(double number){
        number = Math.abs(number);
        short numOfDigits = 0;
        double divisor = 10.0;
        double fractNum = (number % 1);
        while(fractNum != 0){
            fractNum = (fractNum * divisor) % 1;
            numOfDigits++;
        }
        return numOfDigits;
    }
    private short numberOfDigits(double number){
        return (short)(numberOfWholeDigits(number) + numberOfFractDigits(number));
    }
    private int getApproxVal(double number){
        number = Math.round(number);
        short numOfDigits = numberOfWholeDigits(number);
        int divisor = (int) Math.pow(10, (numOfDigits - 1));
        divisor = divisor / 2;
        int approxVal = (int) (number/ divisor);
        if( (int)(number % divisor) > 0){
            approxVal++;
        }
        approxVal = (approxVal * divisor);
        return approxVal;
    }
    private boolean approximatelySame(double number1, double number2){
        if( (numberOfWholeDigits(number1) < 2) || (numberOfWholeDigits(number2) < 2)){
            return (number1 == number2);
        }
        number1 = getApproxVal(number1);
        number2 = getApproxVal(number2);
        if( number1 == number2){
            return true;
        }
        return false;
    }
    private void checkAnswer() {
        // get time taken to answer
        long timeTaken_secs = (System.currentTimeMillis() - startTime) / 1000;
        startTime = 0;

        keypadEnable(false);
        Long attemptedQ = Long.parseLong(attempted.getText().toString());
        attemptedQ++;
        attempted.setText(attemptedQ.toString());

        // see if the answer is in time limit
        boolean isOnTime = true;
        String level = DataHolder.level;
        try{
            if( timeTaken_secs > operandSetting.getJSONObject(level).getInt("timeout")){
                isOnTime = false;
            }
        } catch( Exception e){
        }

        // see if answer is right
        CorrectStatus answerStatus = CorrectStatus.Wrong;
        double total = 0;
        double currScore = 0;
        double percScore = 0;
        try{
            String oper = currentQuestion.getString("operation");
            JSONObject operObject = DataHolder.practiseScore.getJSONObject(level).getJSONObject(oper);
            total = (double) (operObject.getLong("total") + 1);
            operObject.put("total", total);
            String userAnsStr = answerSym.getText().toString() + answerVal.getText().toString();
            switch( oper ){
                case "add":
                case "sub":
                case "mul":
                    int userAns = Integer.parseInt(userAnsStr);
                    int correctAns = Integer.parseInt(currentQuestion.getString("answer"));
                    if( userAns == correctAns ){
                        answerStatus = CorrectStatus.Correct;
                    } else if(canBeApproximate == true){
                        if( approximatelySame(correctAns, userAns)){
                            answerStatus = CorrectStatus.Approx;
                        }
                    }
                    break;
                case "div":
                case "perc":
                    double userAnsReal = Double.parseDouble(userAnsStr);
                    userAnsReal = Math.round(userAnsReal * 100.0) / 100.0;
                    String correctAnsStr = currentQuestion.getString("answer");
                    double correctAnsReal = Double.parseDouble(correctAnsStr);
                    if( userAnsReal == correctAnsReal ){
                        answerStatus = CorrectStatus.Correct;
                    } else if(canBeApproximate == true){
                        if( approximatelySame(correctAnsReal, userAnsReal)){
                            answerStatus = CorrectStatus.Approx;
                        }
                    }
                    break;
                default :
                    break;
            }
            if((answerStatus == CorrectStatus.Correct) || (answerStatus == CorrectStatus.Approx)){
                currScore = (double) (operObject.getLong("correct") + 1);
                operObject.put("correct", currScore);
                percScore = (currScore/ total) * 100.0;
                operObject.put("percCorrect", percScore);
                if(isOnTime == true){
                    currScore = (double) (operObject.getLong("timedCorrect") + 1);
                    operObject.put("timedCorrect", currScore);
                    percScore = (currScore/ total) * 100.0;
                    operObject.put("percTimedCorrect", percScore);
                }
            }
            DataHolder.practiseScore.getJSONObject(level).put(oper, operObject);
        } catch(Exception e){
        }

        // set the images accordingly
        setImages(answerStatus, isOnTime);

        // display correct answer if user answer is not exactly correct
        try{
            if(answerStatus != CorrectStatus.Correct){
                correctAnswer.setText(currentQuestion.getString("answerStr"));
            }
        }catch(Exception e){
        }

        // un-hide and enable next question button
        clickMe = (Button) findViewById(R.id.nextQuestion);
        clickMe.setEnabled(true);
    }
}

package yeti.mathgym;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by rychatur on 6/10/2017.
 */

public class DataHolder{
    public static List<String> operationsList = Arrays.asList("perc", "div", "mul", "sub", "add");
    public static List<String> levels = Arrays.asList("easy", "medium", "hard");
    public static List<String> scoresList = Arrays.asList("total", "correct", "timedCorrect", "percCorrect", "percTimedCorrect");
    public static String defaultScore = "";// "{\"easy\":{\"add\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"sub\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"mul\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"div\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"perc\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0}},\"medium\":{\"add\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"sub\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"mul\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"div\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"perc\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0}},\"hard\":{\"add\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"sub\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"mul\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"div\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0},\"perc\":{\"total\":0,\"correct\":0,\"timedCorrect\":0,\"perccorrect\":0,\"perctimedCorrect\":0}}}";
    public static boolean initialized = false;
    public static String prefFile = "mathGymPref";
    public static String settings = "";
    public static String level = "";
    public static boolean hasAdd = false;
    public static boolean hasSub = false;
    public static boolean hasMul = false;
    public static boolean hasDiv = false;
    public static boolean hasPerc = false;
    public static JSONObject score = null;
    public static JSONObject practiseScore = null;
    public static ArrayList<String> operations = new ArrayList<String>();
    public static String bestOperations = "";
    public static String bestOperationLevel = "";
    public static double bestOperationScore = 0.0;
    public static boolean canBeApproximate = false;
}

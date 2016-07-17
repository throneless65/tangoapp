package me.abidi.tangoapp.tango;

/**
 * Created by zozulya on 17/07/16.
 */

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zozulya on 17/07/16.
 */

public class EventProcessor {

    public final static String TurnLightOffWhenFridgeClosed = "{\n" +
            "\t\"conditionName\": \"When fridge closed then turn the lights off\",\n" +
            "\t\"when\": \"FridgeClosed\",\n" +
            "\t\"then\": {\n" +
            "\t\t\"command\": {\n" +
            "\t\t\t\"deviceName\": \"Light\",\n" +
            "\t\t\t\"activity\": \"Stop\"\n" +
            "\t\t},\n" +
            "\t\t\"time\": 0\n" +
            "\t}\n" +
            "}";

    public final static String TurnLightWhenOvenClosed = "{\n" +
            "\t\"conditionName\": \"When fridge oven closed then turn the lights on\",\n" +
            "\t\"when\": \"OvenClosed\",\n" +
            "\t\"then\": {\n" +
            "\t\t\"command\": {\n" +
            "\t\t\t\"deviceName\": \"Light\",\n" +
            "\t\t\t\"activity\": \"Start\"\n" +
            "\t\t},\n" +
            "\t\t\"time\": 0\n" +
            "\t}\n" +
            "}";

    public final static String StopLightWhenNoBodyHome = "{\n" +
            "\t\"conditionName\": \"When fridge closed then turn the lights on\",\n" +
            "\t\"when\": \"NobodyHome\",\n" +
            "\t\"then\": {\n" +
            "\t\t\"command\": {\n" +
            "\t\t\t\"deviceName\": \"Light\",\n" +
            "\t\t\t\"activity\": \"Stop\"\n" +
            "\t\t},\n" +
            "\t\t\"time\": 0\n" +
            "\t}\n" +
            "}";

    public final static String WharMeWhenNoMilk = "{\n" +
            "\t\"conditionName\": \"When I run out of milk then notify me\",\n" +
            "\t\"when\": \"NoMilk\",\n" +
            "\t\"then\": {\n" +
            "\t\t\"command\": {\n" +
            "\t\t\t\"deviceName\": \"UserNotification\",\n" +
            "\t\t\t\"activity\": \"Start\",\n" +
            "\t\t\t\"options\": {\n" +
            "\t\t\t\t\"message\": \"You are out of milk!\"\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t\"time\": 0\n" +
            "\t}\n" +
            "}";

    public EventProcessor() {
        try {
            registerCondition(TurnLightOffWhenFridgeClosed);
            registerCondition(StopLightWhenNoBodyHome);
            registerCondition(WharMeWhenNoMilk);
            registerCondition(TurnLightWhenOvenClosed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void processEvent(String eventName){
        ArrayList<Condition> conditions = event2Conditions.get(eventName);
        if (conditions == null) return;

        for (Condition condition : conditions) {
            condition.triggerAction();
        }
    };

    /**
     *
     * @param conditionJSON json string with condition
     */
    void registerCondition(String conditionJSON) throws JSONException {
        Condition condition = new Condition(conditionJSON);
        registerCondition(condition);
    };

    void registerCondition(Condition condition) {
        ArrayList<Condition> conditions = event2Conditions.get(condition.getEventName());
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        conditions.add(condition);
        event2Conditions.put(condition.getEventName(), conditions);
    }

    private HashMap<String, ArrayList<Condition>> event2Conditions
            = new HashMap<>();
}


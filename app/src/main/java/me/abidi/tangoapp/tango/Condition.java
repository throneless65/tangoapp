package me.abidi.tangoapp.tango;

/**
 * Created by zozulya on 17/07/16.
 */


import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by zozulya on 15/07/16.
 */

/**
 * Created by zozulya on 17/07/16.
 */



public class Condition {

    String eventName;
    String action;
    String name;

    /**
     * { “conditionName” : “name”,
     “when”: “eventName”,
     “then”: action
     }
     * @param conditionJSON
     */
    Condition(String conditionJSON) throws JSONException {
        // Get top-level JSON Object - a Map
        JSONObject responseObject = (JSONObject) new JSONTokener(
                conditionJSON).nextValue();
        name = responseObject.get("conditionName").toString();
        eventName = responseObject.get("when").toString();
        action = responseObject.get("then").toString();

    }
    public String getEventName() {
        return eventName;
    }

    /**
     *
     * @return json string for action
     */
    public String getAction() {
        return action;
    }

    public void triggerAction(){
        if (action != null) {
            Log.e("Condition " + name, action);
            try {
                Routine.createRoutineFromAction(action).Activate();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }

}
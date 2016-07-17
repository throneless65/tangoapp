package me.abidi.tangoapp.tango;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by zozulya on 15/07/16.
 */

public class CommandFactory {
    private String deviceName = "";
    private String activityName = "";
    private String options = "";
    final private static String DEVICE_TAG = "deviceName";
    final private static String ACTIVITY_TAG = "activity";
    final private static String OPTIONS_TAG = "options";


    final private static String testJson = "{\"deviceName\": \"Light\", \"activity\": \"Stop\"," +
            " \"options\": {\"temperature\": 175}}";

    CommandFactory(){};

    public static CommandFactory setOptions(String jsonString) throws JSONException {
        CommandFactory ret = new CommandFactory();
        JSONObject jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
        ret.deviceName = jsonObject.get(DEVICE_TAG).toString();
        ret.activityName = jsonObject.get(ACTIVITY_TAG).toString();
        try {
            ret.options = jsonObject.get(OPTIONS_TAG).toString();
        } catch (JSONException e) {
            // options are optional
        }
        return ret;
    }

    @Nullable Command create() {
        Device device = null;
        if (deviceName.equals("Oven")) {
            device = new Oven();
        } else if (deviceName.equals("CoffeeMachine")) {
            device = new CoffeMachine();
        } else if (deviceName.equals("Light")) {
            device = new LightHue();
        } else if (deviceName.equals("Fridge")) {
            device = new Fridge();
        } else if (deviceName.equals("UserNotification")) {
            String message = "";
            if (options != null) {
                try {
                    JSONObject jsonObject = (JSONObject) new JSONTokener(options).nextValue();
                    message =  jsonObject.get("message").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            device = new UserNotification(message);
        }

        if (device != null) {
            final Device theDevice = device;
            if (activityName.equals("Start")) {
                return new Command() {
                    @Override
                    public void execute() {
                        theDevice.Start();
                    }
                };
            } else if (activityName.equals("Stop")){
                return new Command() {
                    @Override
                    public void execute() {
                        theDevice.Stop();
                    }
                };
            } else if (activityName.equals("Status")){
                return new Command() {
                    @Override
                    public void execute() {
                        theDevice.Status();
                    }
                };
            }
        }

        return null;
    };
}

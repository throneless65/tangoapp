package me.abidi.tangoapp.tango;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zozulya on 15/07/16.
 */

public class Routine {
    private static final String TAG = Routine.class.getSimpleName();
    private static final String testRoutine = "{\n" +
            "\t\"name\": \"Light Music\",\n" +
            "\t\"actions\": [{\n" +
            "\t\t\"command\": {\n" +
            "\t\t\t\"deviceName\": \"Light\",\n" +
            "\t\t\t\"activity\": \"Start\",\n" +
            "\t\t\t\"options\": {\n" +
            "\t\t\t\t\"temp\": 499\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t\"time\": 5000\n" +
            "\t}, {\n" +
            "\t\t\"command\": {\n" +
            "\t\t\t\"deviceName\": \"Light\",\n" +
            "\t\t\t\"activity\": \"Stop\",\n" +
            "\t\t\t\"options\": {\n" +
            "\t\t\t\t\"temp\": 499\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t\"time\": 5000\n" +
            "\t}, {\n" +
            "\t\t\"command\": {\n" +
            "\t\t\t\"deviceName\": \"Oven\",\n" +
            "\t\t\t\"activity\": \"Start\",\n" +
            "\t\t\t\"options\": {\n" +
            "\t\t\t\t\"temp\": 499\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t\"time\": 5000\n" +
            "\t}, {\n" +
            "\t\t\"command\": {\n" +
            "\t\t\t\"deviceName\": \"Light\",\n" +
            "\t\t\t\"activity\": \"Stop\",\n" +
            "\t\t\t\"options\": {\n" +
            "\t\t\t\t\"temp\": 499\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t\"time\": 5000\n" +
            "\t}]\n" +
            "}";


    private List<Pair<Command, Integer>> commandList = new ArrayList<>();
    private String Name;



    public void Activate(){

        // check for an error
        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).first == null) return; // TODO: or throw
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < commandList.size(); i++) {
                    Log.e("TAG", "Execute command");
                    try {
                        Thread.sleep(commandList.get(i).second);
                    } catch (InterruptedException e) {
                        // ignore: TODO or not?
                    }
                    commandList.get(i).first.execute();
                }
            }
        }).start();

    };

    public void addCommand(Command command) {
        commandList.add(new Pair(command, 10000));
    }

    public void addCommand(Command command, Integer interval) {
        commandList.add(new Pair(command, interval));
    }

    public String getName() {
        return Name;
    }
    public void setname(final String name) {this.Name = name; }

    public static Routine createRoutine(String json) throws JSONException {
//        json = testRoutine; // FIXME
        final String NAME_TAG = "name";
        final String ACTIONS_TAG = "actions";
        final String COMMAND_TAG = "command";
        final String TIME_TAG = "time";

        JSONObject jsonObject = (JSONObject) new JSONTokener(json).nextValue();
        String name = jsonObject.get(NAME_TAG).toString();
        Routine routine = new Routine();
        routine.setname(name);

        JSONArray actions =  jsonObject.getJSONArray(ACTIONS_TAG);
        for (int i = 0; i < actions.length(); i++) {
            JSONObject action = (JSONObject) actions.get(i);
            JSONObject command = (JSONObject) action.get(COMMAND_TAG);
            Integer time = Integer.parseInt( action.get(TIME_TAG).toString());
            routine.addCommand(CommandFactory.setOptions(command.toString()).create(),
                    time);

        }

//        routine.addCommand(CommandFactory.setOptions("{\"deviceName\": \"Light\", \"activity\": \"Start\"," +
//                " \"options\": {\"temperature\": 175}}").create());
//        routine.addCommand(CommandFactory.setOptions("{\"deviceName\": \"Light\", \"activity\": \"Stop\"," +
//                " \"options\": {\"temperature\": 175}}").create());
//        routine.addCommand(CommandFactory.setOptions("{\"deviceName\": \"Light\", \"activity\": \"Start\"," +
//                " \"options\": {\"temperature\": 175}}").create());
        return  routine;
    }
}

package me.abidi.tangoapp.tango;

import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Then then then pattern
 * Created by zozulya on 16/07/16.
 */

public class TTTProcessor {

    public static boolean isTTTText(String text) {
        return text.contains("then");
    }

    public void processText(String text) {
        try {
            String[] phrases = text.split("then");
            ArrayList<String> actions = new ArrayList<>();
            TextProcessor textProcessor = new TextProcessor();
            for (int i = 0; i < phrases.length; i++) {
                String phrase = phrases[i];
                String luisJSON = textProcessor.getResponseFromLuis(phrase);
                actions.addAll(textProcessor.extractActions(luisJSON));
            }
            String routine = createRoutine(actions);
            Log.e("TTTProcessor", routine);
            Routine.createRoutine(routine).Activate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String createRoutine(List<String> actions){
        StringBuilder builder = new StringBuilder();
        builder.append("{\"name\": \"tempName\"");
        builder.append(", \"actions\" : [ ");
        for (int i = 0; i < actions.size(); i++) {
            if (i != 0) builder.append(",");
            builder.append(actions.get(i));
        }
        builder.append("]}");
        return builder.toString();
    }
}
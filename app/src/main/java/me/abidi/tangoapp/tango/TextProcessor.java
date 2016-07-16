package me.abidi.tangoapp.tango;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.ResponseHandler;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.BasicResponseHandler;

/**
 * Created by zozulya on 16/07/16.
 */

public class TextProcessor {

    private static final String INTENTS_TAG = "intents";
    private static final String ENTITIES_TAG = "entities";
    private static final String INTENT_TAG = "intent";
    private static final String ENTITY_TAG = "type";
    private static final String SCORE_TAG = "score";

    private String lastConstructedRoutine = "";


    private final static String exampleResponse = "{\n" +
            "  \"query\": \"turn the lights on in 5 seconds\",\n" +
            "  \"intents\": [\n" +
            "    {\n" +
            "      \"intent\": \"StartDevice\",\n" +
            "      \"score\": 0.809820831\n" +
            "    },\n" +
            "    {\n" +
            "      \"intent\": \"None\",\n" +
            "      \"score\": 0.0261161663\n" +
            "    },\n" +
            "    {\n" +
            "      \"intent\": \"StatusDevice\",\n" +
            "      \"score\": 0.0206643473\n" +
            "    },\n" +
            "    {\n" +
            "      \"intent\": \"StopDevice\",\n" +
            "      \"score\": 3.35621948E-08\n" +
            "    }\n" +
            "  ],\n" +
            "  \"entities\": [\n" +
            "    {\n" +
            "      \"entity\": \"lights\",\n" +
            "      \"type\": \"light\",\n" +
            "      \"startIndex\": 9,\n" +
            "      \"endIndex\": 14,\n" +
            "      \"score\": 0.944613636\n" +
            "    },\n" +
            "    {\n" +
            "      \"entity\": \"in 5 seconds\",\n" +
            "      \"type\": \"builtin.datetime.time\",\n" +
            "      \"startIndex\": 19,\n" +
            "      \"endIndex\": 30,\n" +
            "      \"resolution\": {\n" +
            "        \"time\": \"7/16/2016 6:34:15 AM\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    // process one
    public void processText(final String text) {
        // if then AAAA and BBBB then CCCC
        // simple command
        if (TTTProcessor.isTTTText(text)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TTTProcessor tttProcessor = new TTTProcessor();
                    tttProcessor.processText(text);
                }
            }).start();

        } else {
            new HttpGetTask().execute(text);
        }
    }

    /**
     *
     * @param text extract actions with one intent, such as "turn on the lgihts and musics"
     * @return
     */
    public static List<String> extractActions(String text) throws JSONException{
        ArrayList<String> actions = new ArrayList<>();
        String routine  = parseJSON(text);
        if (routine == null || routine.isEmpty()) return actions;
        JSONObject responseObject = (JSONObject) new JSONTokener(
                routine).nextValue();

        // Extract value of "intents" key -- a List
        JSONArray actionsArray = responseObject.getJSONArray("actions");

        for (int idx = 0; idx < actionsArray.length(); idx++) {
            // Get single intents data - a Map
            JSONObject action = (JSONObject) actionsArray.get(idx);
            actions.add(action.toString());
        }
        return actions;
    }


    private class HttpGetTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            return getResponseFromLuis(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            lastConstructedRoutine = parseJSON(result);
            try {
                if (lastConstructedRoutine != null) {
                    Log.e("TextProcessor", lastConstructedRoutine);
                    Routine.createRoutine(lastConstructedRoutine).Activate();
                } else {
                    Log.e("TextProcessor", "didn't recognize a routine");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    public static String getResponseFromLuis(String text) {
        String ret = null;
        try {
            Log.e("LUIS", text);
            String url = "https://api.projectoxford.ai/luis/v1/application?id=3f8b1ed6-98fc-4675-abde-74c85225dc63&subscription-key=276ec68466dc4e829bb7c999d24bcf5a&q="
                    + java.net.URLEncoder.encode(text, "UTF-8");
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            ret = response.body().string();
            Log.e("LUIS", ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     *
     * @param text json as returned from luis
     * @return the json in routine
     */
    @Nullable public static String parseJSON(String text){
        String action = null;
        List<String> entities = new ArrayList<>();
        Integer interval = null;
        try {
//
            // Get top-level JSON Object - a Map
            JSONObject responseObject = (JSONObject) new JSONTokener(
                    text).nextValue();

            // Extract value of "intents" key -- a List
            JSONArray intentsArray = responseObject.getJSONArray(INTENTS_TAG);

            // Iterate over earthquakes list
            for (int idx = 0; idx < intentsArray.length(); idx++) {

                // Get single intents data - a Map
                JSONObject intent = (JSONObject) intentsArray.get(idx);

                if (Double.parseDouble(intent.get(SCORE_TAG).toString()) > 0.5) {
                    action = intent.get(INTENT_TAG).toString();
                }
//                    intents.add( INTENT_TAG + ":"
//                            + intent.get(INTENT_TAG) + ","
//                            + SCORE_TAG +":"
//                            + intent.get(SCORE_TAG));
            }

            // Extract value of "entities" key -- a List
            JSONArray entitiesArray = responseObject.getJSONArray(ENTITIES_TAG);

            // Iterate over earthquakes list
            for (int idx = 0; idx < entitiesArray.length(); idx++) {

                // Get single intents data - a Map
                JSONObject entity = (JSONObject) entitiesArray.get(idx);

                if (entity.get(ENTITY_TAG).toString().equals("builtin.datetime.time")) {
                    interval = extractInterval(
                            ((JSONObject)entity.get("resolution")).get("time").toString()
                    );
                    continue;
                }

                if (Double.parseDouble(entity.get(SCORE_TAG).toString()) > 0.5) {
                    String type = entity.get(ENTITY_TAG).toString();
                    if (type.equals("builtin.datetime.time")) {
                        interval = extractInterval(
                                ((JSONObject)entity.get("resolution")).get("time").toString()
                        );
                    } else {
                        entities.add(entity.get(ENTITY_TAG).toString());
                    }
                }

//                    entities.add(ENTITY_TAG + ":"
//                            + entity.get(ENTITY_TAG));
            }
        } catch (JSONException e) {
        }
        if (action != null && !entities.isEmpty()) {
            return createRoutine(action, entities, interval);
        } else {
            return null;
        }
    }

    /**
     *
     * @param action
     * @param entities
     * @return JSON for routine with one action
     */
    public static String createRoutine(String action, List<String> entities, Integer interval) {
        String action_tag = action.replace("Device", ""); // turn StartDevice into Start
        if (interval == null) interval = 0;
        StringBuilder builder = new StringBuilder();
        builder.append("{\"name\": \"tempName\"");
        builder.append(", \"actions\" : [ ");
        for (int i = 0; i < entities.size(); i++) {
            String device = mapToDevice(entities.get(i));
            if (i != 0) builder.append(",");
            builder.append(String.format("{ \"command\" : {\"deviceName\": \"%s\", \"activity\": \"%s\"}, \"time\": %d }",
                    device, action_tag, interval));
        }
        builder.append("]}");
        return builder.toString();
    }

    private final static HashMap<String, String> deviceToEntity;
    static {
        deviceToEntity = new HashMap<>();
        deviceToEntity.put("light", "Light");
        deviceToEntity.put("oven", "Oven");
        deviceToEntity.put("coffeemachine", "CoffeeMachine");
    }

    public static String mapToDevice(String entity_type) {
        return deviceToEntity.get(entity_type);
    }

    /**
     *  Should turn "time": "7/16/2016 10:05:37 AM" in milliseconds from now.
     * @param dataTime
     * @return
     */
    public static Integer extractInterval(String dataTime) {
        return 5000;
    }




//    private class JSONResponseHandler implements ResponseHandler<List<String>> {
//
//        private static final String INTENTS_TAG = "intents";
//        private static final String ENTITIES_TAG = "entities";
//        private static final String INTENT_TAG = "intent";
//        private static final String ENTITY_TAG = "entity";
//        private static final String SCORE_TAG = "score";
//
//        @Override
//        public List<String> handleResponse(HttpResponse response)
//                throws ClientProtocolException, IOException {
//            List<String> intents = new ArrayList<String>();
//            List<String> entities = new ArrayList<String>();
//            String JSONResponse = new BasicResponseHandler().handleResponse(response);
//            try {
//
//                // Get top-level JSON Object - a Map
//                JSONObject responseObject = (JSONObject) new JSONTokener(
//                        JSONResponse).nextValue();
//
//                // Extract value of "intents" key -- a List
//                JSONArray intentsArray = responseObject.getJSONArray(INTENTS_TAG);
//
//                // Iterate over earthquakes list
//                for (int idx = 0; idx < intentsArray.length(); idx++) {
//
//                    // Get single intents data - a Map
//                    JSONObject intent = (JSONObject) intentsArray.get(idx);
//
//                    // Summarize earthquake data as a string and add it to
//                    // result
//                    intents.add( INTENT_TAG + ":"
//                            + intent.get(INTENT_TAG) + ","
//                            + SCORE_TAG +":"
//                            + intent.get(SCORE_TAG));
//                }
//
//                // Extract value of "entities" key -- a List
//                JSONArray entitiesArray = responseObject.getJSONArray(ENTITIES_TAG);
//
//                // Iterate over earthquakes list
//                for (int idx = 0; idx < entitiesArray.length(); idx++) {
//
//                    // Get single intents data - a Map
//                    JSONObject entity = (JSONObject) entitiesArray.get(idx);
//
//                    // Summarize earthquake data as a string and add it to
//                    // result
//                    entities.add(ENTITY_TAG + ":"
//                            + entity.get(ENTITY_TAG));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            intents.addAll(entities);
//            return intents;
//        }
//    }
}

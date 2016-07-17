package me.abidi.tangoapp.tango;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zozulya on 16/07/16.
 */

public class LightHue implements Device {
    private static final String TAG = LightHue.class.getSimpleName();
    private static final String STOP_URL = "http://192.168.2.196/api/WLnHAj80rCH2vMjp8IRWth2euGqMWZxwJ3zw83RT/lights/1/state";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String NAME = "Hue";
    private String color = "";

    public LightHue(String color) {
        this.color = color;
    }
    @Override
    public void Stop() {
        Log.e(TAG, "Stop light");

        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody body;
            if (color.equalsIgnoreCase("red")) {
                body  = RequestBody.create(JSON, "{\"on\":true, \"sat\":254, \"bri\":254,\"hue\":10000}");
            } else {
                body  = RequestBody.create(JSON, "{\"on\":false}");
            }
            Request request = new Request.Builder()
                    .url(STOP_URL)
                    .put(body)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return response.body().string();
    }

    @Override
    public void Start() {
        Log.e(TAG, "Start Light");
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, "{\"on\":true}");
            Request request = new Request.Builder()
                    .url(STOP_URL)
                    .put(body)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String Status() {
        Log.e(TAG, "Status Light");
        return null;
    }
}

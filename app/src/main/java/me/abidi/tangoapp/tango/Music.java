package me.abidi.tangoapp.tango;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zozulya on 17/07/16.
 */

public class Music implements Device {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String API_BASE_URL = "http://192.168.2.187:4567/run_music";
    //private static Retrofit retrofit;
    @Override
    public void Start() {

        Log.e("Music", "Start Music");
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, "{\"on\":true}");
            Request request = new Request.Builder()
                    .url("http://192.168.2.187:9494/run_music")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Stop() {

    }

    @Override
    public String Status() {
        return null;
    }
}

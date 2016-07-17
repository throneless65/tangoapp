package me.abidi.tangoapp.tango;


import android.util.Log;

/**
 * Created by zozulya on 17/07/16.
 */

public class UserNotification implements Device {
    private String message = "";

    public UserNotification(String message) {
        this.message = message;
    }
    @Override
    public void Start() {
        Log.e("UserNotification", message);
    }

    @Override
    public void Stop() {

    }

    @Override
    public String Status() {
        return null;
    }
}

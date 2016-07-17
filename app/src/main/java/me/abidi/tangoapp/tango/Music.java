package me.abidi.tangoapp.tango;

import android.util.Log;


/**
 * Created by zozulya on 17/07/16.
 */

public class Music implements Device {
    @Override
    public void Start() {
        Log.e("Music", "Start Music");
    }

    @Override
    public void Stop() {

    }

    @Override
    public String Status() {
        return null;
    }
}

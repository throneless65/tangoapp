package me.abidi.tangoapp.tango;


import android.util.Log;

/**
 * Created by zozulya on 15/07/16.
 */

public class CoffeMachine implements Device {
    private static final String TAG = CoffeMachine.class.getSimpleName();

    @Override
    public void Start() {
        Log.e(TAG, "Start CoffeMachine");
    }

    @Override
    public void Stop() {
        Log.e(TAG, "Stop CoffeMachine");
    }

    @Override
    public String Status() {
        Log.e(TAG, "Status CoffeMachine");
        return null;
    }
}

package me.abidi.tangoapp;

import android.app.Application;

import com.home_connect.sdk.Configuration;
import com.home_connect.sdk.Environment;
import com.home_connect.sdk.ServerType;
import com.home_connect.sdk.services.HomeConnect;

/**
 * Configures the HomeConnectInstance
 */
public class TangoAppApplication extends Application {
    /**
     * Configures the {@link HomeConnect} instance at the App start.
     * It is mandatory to use the SDK.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        HomeConnect
                .getInstance()
                .setConfiguration(Configuration.create()
                        .setApplicationContext(this)
                        .setApiKey("2F075EAB752DCC92D1EBD7E3BE1397CA17BF8CFFB97D69432690C063613D00B2")
                        .setEnvironment(Environment.SIMULATOR)
                        .build());
        /*TESTINGSTART*/
        HomeConnect.getInstance().getModel().setUseStagingAPISimulator(true);
        HomeConnect.getInstance().setServerType(ServerType.SIMULATOR_PRODUCTION);
        /*TESTINGEND*/
    }
}

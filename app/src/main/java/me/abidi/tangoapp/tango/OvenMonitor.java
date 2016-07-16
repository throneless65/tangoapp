package me.abidi.tangoapp.tango;

import android.util.Log;

import com.home_connect.sdk.model.HomeApplianceModel;
import com.home_connect.sdk.model.events.MonitorEvent;
import com.home_connect.sdk.services.ApplianceService;
import com.home_connect.sdk.services.ProgramService;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by zozulya on 16/07/16.
 */

public class OvenMonitor {
    private static final String TAG = Oven.class.getSimpleName();
    private HomeApplianceModel applianceModel;
    private final ProgramService programService;
    private Subscription subscription;

    public OvenMonitor(){
        ApplianceService applianceService = ApplianceService.create();
        applianceService.updateAppliances();
        List<HomeApplianceModel> applianceModelList = applianceService.getAppliances();
        for (HomeApplianceModel homeApplianceModel : applianceModelList) {
            if (homeApplianceModel.getName().contains("Oven")) {
                applianceModel = homeApplianceModel;
                break;
            }
        }
        if (applianceModel == null) {
            Log.e(TAG, "Coulnd't find an oven");
        }
        programService = ProgramService.create();

    }

    public void startMonitoring() {
        /*
         * The events are provided in a stream until unsubscribed
         *//*
          * Handle failure
          */
        if (applianceModel == null) {
            Log.e("OvenMonitor", "appliance in snull");
            return;
        }
        subscription = ApplianceService.create()
                .monitor(applianceModel)
                .subscribe(
                        new Action1<MonitorEvent>() {
                            @Override
                            public void call(MonitorEvent monitorEvent) {
                /*
                 * The events are provided in a stream until unsubscribed
                 *
                 */
                                Log.e("OvenMonitor", monitorEvent.type.toString());
                                for (int i = 0; i < monitorEvent.data.size(); i++) {
                                    Log.e("OvenMonitor", monitorEvent.data.get(i).value.toString());
                                    Log.e("OvenMonitor", monitorEvent.data.get(i).description);
                                    Log.e("OvenMonitor", monitorEvent.data.get(i).key);

                                }
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                /*
                 * Handle failure
                 */
                            }
                        }
                );
        //BSH.Common.Status.DoorState
    }

    public void stopMonitor() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}


package me.abidi.tangoapp.tango;

import android.util.Log;

import com.home_connect.sdk.model.HomeApplianceModel;
import com.home_connect.sdk.model.events.MonitorEvent;
import com.home_connect.sdk.model.events.MonitorEventType;
import com.home_connect.sdk.services.ApplianceService;
import com.home_connect.sdk.services.ProgramService;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by zozulya on 16/07/16.
 */

public class OvenMonitor {  private static final String TAG = Oven.class.getSimpleName();
    private HomeApplianceModel applianceModel;
    private final ProgramService programService;
    private Subscription subscription;
    private EventProcessor eventProcessor;

    public OvenMonitor(EventProcessor eventProcessor) {
        this();
        this.eventProcessor = eventProcessor;
    }

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

    private boolean doorShut(MonitorEvent event) {
        //E/FridgeMonitor: STATUS
        //E/FridgeMonitor: BSH.Common.EnumType.DoorState.Open
        //E/FridgeMonitor: NULL
        //E/FridgeMonitor: BSH.Common.Status.DoorState
        if (event.type == MonitorEventType.STATUS
                && event.data != null
                && event.data.size() > 0
                && event.data.get(0).value != null
                && event.data.get(0).value.toString().equals("BSH.Common.Status.DoorState.Closed")) {
            return true;
        }
        return false;
    }

    public static String NullorString(Object o) {
        if (o == null) return "NULL";
        return  o.toString();
    }

    public void startMonitoring() {
        /*
         * The events are provided in a stream until unsubscribed
         *//*
          * Handle failure
          */
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
                                if (monitorEvent.data == null) return;
                                for (int i = 0; i < monitorEvent.data.size(); i++) {
                                    Log.e("OvenMonitor", NullorString(monitorEvent.data.get(i).value));
                                    Log.e("OvenMonitor", NullorString(monitorEvent.data.get(i).description));
                                    Log.e("OvenMonitor", NullorString(monitorEvent.data.get(i).key));

                                }
                                if (doorShut(monitorEvent)) {
                                    Log.e("OvenMonitor","DOOR IS SHUT");
                                    if (eventProcessor != null) {
                                        eventProcessor.processEvent("OvenClosed");
                                    }

                                    //takeAndProcessImage();
                                }
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                /*
                 * Handle failure
                 *
                 */
                                Log.e("OvenMonitorError", throwable.getMessage());

                            }
                        }
                );
        Log.e("OvenMonitorError", "Started Monitoring");

        //BSH.Common.Status.DoorState
    }

    public void stopMonitor() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}


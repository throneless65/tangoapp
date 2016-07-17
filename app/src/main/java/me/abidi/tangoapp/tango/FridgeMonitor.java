package me.abidi.tangoapp.tango;

/**
 * Created by zozulya on 17/07/16.
 */

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.home_connect.sdk.exceptions.HomeConnectException;
import com.home_connect.sdk.internal.log.HCLog;
import com.home_connect.sdk.model.HomeApplianceModel;
import com.home_connect.sdk.model.Image;
import com.home_connect.sdk.model.events.MonitorEvent;
import com.home_connect.sdk.model.events.MonitorEventType;
import com.home_connect.sdk.property.RxBinder;
import com.home_connect.sdk.services.ApplianceService;
import com.home_connect.sdk.services.ProgramService;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by zozulya on 16/07/16.
 */

public class FridgeMonitor {
    private static final String TAG = FridgeMonitor.class.getSimpleName();
    private HomeApplianceModel applianceModel;
    private final ProgramService programService;
    private Subscription subscription;
    private final ApplianceService applianceService;
    private ArrayList<Image> imageList = new ArrayList<>();
    private String imageKey = "";
    private String fridgeDescription = "";
    private EventProcessor eventProcessor = null;

    synchronized public String getFridgeDescription() {
        return fridgeDescription;
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
                && event.data.get(0).value.toString().equals("BSH.Common.EnumType.DoorState.Closed")) {
            return true;
        }
        return false;
    }

    public FridgeMonitor(){
        applianceService = ApplianceService.create();
        applianceService.updateAppliances();
        List<HomeApplianceModel> applianceModelList = applianceService.getAppliances();
        for (HomeApplianceModel homeApplianceModel : applianceModelList) {
            if (homeApplianceModel.getName().contains("Freezer")) {
                applianceModel = homeApplianceModel;
                break;
            }
        }
        if (applianceModel == null) {
            Log.e(TAG, "Coulnd't find an fridge");
        }
        programService = ProgramService.create();

    }

    public FridgeMonitor(EventProcessor eventProcessor) {
        this();
        this.eventProcessor = eventProcessor;
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
        if (applianceModel == null) {
            Log.e(TAG, "fridge is null");
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
                                Log.e("FridgeMonitor", monitorEvent.type.toString());
                                if (monitorEvent.data == null) return;
                                for (int i = 0; i < monitorEvent.data.size(); i++) {
                                    Log.e("FridgeMonitor", NullorString(monitorEvent.data.get(i).value));
                                    Log.e("FridgeMonitor", NullorString(monitorEvent.data.get(i).description));
                                    Log.e("FridgeMonitor", NullorString(monitorEvent.data.get(i).key));

                                }
                                if (doorShut(monitorEvent)) {
                                    Log.e("FridgeMponitor","DOOR IS SHUT");
                                    if (eventProcessor != null) {
                                        eventProcessor.processEvent("FridgeClosed");
                                        eventProcessor.processEvent("NoMilk");
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
                                Log.e("FridgeMonitorError", throwable.getMessage());

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

    private void takeAndProcessImage() {

        RxBinder.bind(this,
                applianceService.requestImages(applianceModel),
                new Action1<List<Image>>() {
                    @Override
                    public void call(List<Image> images) {
                        imageList.addAll(images);
                        int lastImage = 0;
                        imageKey = imageList.get(lastImage).getImageKey();
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HCLog.d(throwable);
                        if (throwable instanceof HomeConnectException) {
                            HomeConnectException exception = (HomeConnectException) throwable;
//                            showErrorDialog(exception);
                        } else {
//                            showErrorDialog(null);
                        }
                    }
                });
    }

    private void loadImage() {
        RxBinder.bind(this,
                applianceService.requestImageBitmap(applianceModel, imageKey),
                new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        analyzeImage(bitmap);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HCLog.d(throwable);
                        if (throwable instanceof HomeConnectException) {
                            HomeConnectException exception = (HomeConnectException) throwable;
//                            showErrorDialog(exception);
                        } else {
//                            showErrorDialog(null);
                        }
                    }
                });
    }

    private void analyzeImage(Bitmap bitmap){
        // TODO get string from msft api
        // send it to main activity.
    }
}


package me.abidi.tangoapp.tango;

import android.util.Log;

import com.home_connect.sdk.constants.ProgramConstants;
import com.home_connect.sdk.exceptions.HomeConnectException;
import com.home_connect.sdk.internal.log.HCLog;
import com.home_connect.sdk.model.HomeApplianceModel;
import com.home_connect.sdk.model.ProgramModel;
import com.home_connect.sdk.model.features.Feature;
import com.home_connect.sdk.model.features.Unit;
import com.home_connect.sdk.property.RxBinder;
import com.home_connect.sdk.services.ApplianceService;
import com.home_connect.sdk.services.ProgramService;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by zozulya on 15/07/16.
 */

public class Oven implements Device {
    private static final String TAG = Oven.class.getSimpleName();
    private  HomeApplianceModel applianceModel =  null;
    private final ProgramModel selectedProgram;
    private final ProgramService programService;

    Oven(){
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
//        applianceModel = applianceService.findAppliance("BOSCH-HCS01OVN1-8A66AADD197275");
        // TODO
        selectedProgram = ProgramModel.create().key(ProgramConstants.Oven.PIZZA_SETTING.getProgramKey())
                .option(
                        Feature.create()
                                .key(ProgramConstants.CommonOptions.DURATION.getOptionKey())
                                .unit(Unit.SECONDS)
                                .value(100) // TODO
                )
                .option(
                        Feature.create()
                                .key(ProgramConstants.Oven.Options.SetPointTemperature.getOptionKey())
                                .unit(Unit.TEMPERATURE_CELSIUS)
                                .value(150) // TODO
                )
                .build();
        programService = ProgramService.create();

    }

    @Override
    public void Start() {
        Log.e(TAG, "Start Oven");
        if (applianceModel == null) {
            Log.e(TAG, "cannot start, null applicance");
            return;
        }
        RxBinder.bind(this,
                programService.startProgram(applianceModel, selectedProgram).single(),
                new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                        presenter.showProgramStartState();
//                        isProgramStarted = true;
                        Log.e(TAG, "Oven Started");

                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HCLog.d(throwable);
                        if (throwable instanceof HomeConnectException) {
                            HomeConnectException exception = (HomeConnectException) throwable;
//                            presenter.showError(exception);
                        } else {
//                            presenter.showError(null);
                        }
                    }
                });
    }

    @Override
    public void Stop() {
        Log.e(TAG, "Stop Oven");
        if (applianceModel == null) {
            Log.e(TAG, "cannot stop, null applicance");
            return;
        }
        RxBinder.bind(this,
                programService.stopProgram(applianceModel).single(),
                new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // Handle program stop success if necessary
                        Log.e(TAG, "Oven is stopped");
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof HomeConnectException) {
                            HomeConnectException exception = (HomeConnectException) throwable;
//                            presenter.showError(exception);
                        } else {
//                            presenter.showError(null);
                        }
                    }
                });
    }

    @Override
    public String Status() {
        Log.e(TAG, "Status Oven");
        return null;
    }
}

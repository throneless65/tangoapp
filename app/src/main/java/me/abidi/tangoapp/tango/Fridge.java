package me.abidi.tangoapp.tango;

import android.util.Log;

import com.home_connect.sdk.constants.ProgramConstants;
import com.home_connect.sdk.model.HomeApplianceModel;
import com.home_connect.sdk.model.ProgramModel;
import com.home_connect.sdk.model.features.Feature;
import com.home_connect.sdk.model.features.Unit;
import com.home_connect.sdk.services.ApplianceService;
import com.home_connect.sdk.services.ProgramService;

import java.util.List;

/**
 * Created by zozulya on 17/07/16.
 */

public class Fridge implements Device {
    private static final String TAG = Oven.class.getSimpleName();
    private HomeApplianceModel applianceModel =  null;
    private final ProgramModel selectedProgram;
    private final ProgramService programService;
    private FridgeMonitor fridgeMonitor = null;

    Fridge(){
        ApplianceService applianceService = ApplianceService.create();
        applianceService.updateAppliances();
        List<HomeApplianceModel> applianceModelList = applianceService.getAppliances();
        for (HomeApplianceModel homeApplianceModel : applianceModelList) {
            if (homeApplianceModel.getName().contains("Freezer")) {
                applianceModel = homeApplianceModel;
                break;
            }
        }
        if (applianceModel == null) {
            Log.e(TAG, "Coulnd't find a fridge");
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
        Log.e(TAG, "Start Fridge");
    }

    @Override
    public void Stop() {
        Log.e(TAG, "Stop Fridge");
    }

    @Override
    public String Status() {
        Log.e(TAG, "Fridge Status");
        String status = "";
        if (FridgeMonitor.words.toLowerCase().contains("milch")) {
            status = "Got milk!";
        } else {
            status = FridgeMonitor.fridgeDescription;
        }
        Log.e(TAG, status);
        return status;
    }
}

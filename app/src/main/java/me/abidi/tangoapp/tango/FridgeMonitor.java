package me.abidi.tangoapp.tango;

/**
 * Created by zozulya on 17/07/16.
 */

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.home_connect.sdk.exceptions.HomeConnectException;
import com.home_connect.sdk.internal.log.HCLog;
import com.home_connect.sdk.model.HomeApplianceModel;
import com.home_connect.sdk.model.Image;
import com.home_connect.sdk.model.events.MonitorEvent;
import com.home_connect.sdk.model.events.MonitorEventType;
import com.home_connect.sdk.property.RxBinder;
import com.home_connect.sdk.services.ApplianceService;
import com.home_connect.sdk.services.ProgramService;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.abidi.tangoapp.R;
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
    private EventProcessor eventProcessor = null;
    private ContentResolver contentResolver;

    // Visual API
    private VisionServiceClient client;
    private Bitmap mBitmapInner;
    private Bitmap mBitmapDoor;
    private Uri mImageUri;

    public static String fridgeDescription = "Don't know";
    public static String words = "";


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

    public FridgeMonitor(EventProcessor eventProcessor, ContentResolver contentResolver) {
        this();
        this.eventProcessor = eventProcessor;
        this.contentResolver = contentResolver;

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
        if (client==null){
            client = new VisionServiceRestClient("01f948e3ae20494b8a95b94c0f0b6b6b");//HACK
        }
        doAnalyze();
        doRecognize();
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
                                        doAnalyze();
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
        Log.e("FridgeMonitorError", "Started Monitoring");
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
//                        analyzeImage(bitmap);
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


    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            if (e != null) {
                Log.e("FridgeMonitor", "Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

//                String omageFromat  = result.metadata.format;
//                mEditText.append("Image width: " + result.metadata.width + ", height:" + result.metadata.height + "\n");
//                mEditText.append("\n");
//
//                for (Caption caption: result.description.captions) {
//                    mEditText.append("Caption: " + caption.text + ", confidence: " + caption.confidence + "\n");
//                }
//                mEditText.append("\n");

                fridgeDescription = result.description.captions.get(0).text;
                ArrayList<String> tags = new ArrayList<>();
                for (String tag : result.description.tags) {
                    tags.add(tag);
                }
            }
        }
    }

        private String process() throws VisionServiceException, IOException {
            Gson gson = new Gson();

            // Put the image into an input stream for detection.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            mBitmapInner.compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

            AnalysisResult v = client.describe(inputStream, 1);

            String result = gson.toJson(v);
            Log.d("result", result);

            return result;
        }

    private void prepareImage(){
        mImageUri = Uri.parse("android.resource://me.abidi.tangoapp/" + R.drawable.fridge_inner);
        Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                mImageUri, contentResolver);
        mBitmapInner = ImageHelper.rotateBitmap(bitmap, 90);
        if (mBitmapInner != null) {
                // Show the image on screen.
//                ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
//                imageView.setImageBitmap(mBitmapInner);

                // Add detection log.
                Log.d("DescribeActivity", "Image: " + mImageUri + " resized to " + mBitmapInner.getWidth()
                        + "x" + mBitmapInner.getHeight());
        }
    }

        public void doAnalyze() {
            prepareImage();
            try {
                new doRequest().execute();
            } catch (Exception e)
            {
                Log.e("FridgeMonitor","Error encountered. Exception is: " + e.toString());
            }
        }

    /********* Text Rcognition **************/

    public void doRecognize() {
        Uri uri = Uri.parse("android.resource://me.abidi.tangoapp/" + R.drawable.fridge_door);
        Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                uri, contentResolver);
        mBitmapDoor = ImageHelper.rotateBitmap(bitmap, 90);
        if (mBitmapDoor != null) {
            // Show the image on screen.
//                ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
//                imageView.setImageBitmap(mBitmapInner);

            // Add detection log.
            Log.d("DescribeActivity", "Image: " + uri + " resized to " + mBitmapDoor.getWidth()
                    + "x" + mBitmapDoor.getHeight());
        }
        try {
            new doRequestRecognize().execute();
        } catch (Exception e)
        {
        }
    }

    private String processRecognize() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmapDoor.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr;
        ocr = this.client.recognizeText(inputStream, LanguageCodes.AutoDetect, true);

        String result = gson.toJson(ocr);
        Log.d("result", result);

        return result;
    }

    private class doRequestRecognize extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequestRecognize() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return processRecognize();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            if (e != null) {
                Log.e("Recognier", "Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                String result = "";
                for (Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text + " ";
                        }
                        result += "\n";
                    }
                    result += "\n\n";
                }
                words = result;

            }
        }
    }
}


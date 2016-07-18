package me.abidi.tangoapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import me.abidi.tangoapp.tango.FridgeMonitor;
import me.abidi.tangoapp.tango.TextProcessor;

public class SelectionActivity extends AppCompatActivity {

    private TextView txtSpeechInput;
    private TextView txtViewFridge;
    private ImageButton btnSpeak;
    private Button btnActivate;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        //txtViewFridge = (TextView) findViewById(R.id.txtViewFridge);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnActivate = (Button) findViewById(R.id.btnActivate);
        btnActivate.setVisibility(View.INVISIBLE);

        // hide the action bar
        //getSupportActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    final ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));

                    btnActivate.setVisibility(View.VISIBLE);

                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            // call the method that will process the text and pass result.get(0)
                            // processText(result.get(0))
                            // then show toast
                            //
                            Log.e("Processing speech", result.get(0));
                            TextProcessor textProcessor = new TextProcessor();
//                            textProcessor.processText(result.get(0));
                            textProcessor.processText(result.get(0));
                            Snackbar activatedSnackBar = Snackbar.make(view, "Command activated.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            activatedSnackBar.show();

                            if (activatedSnackBar.isShown()){
                                btnActivate.setVisibility(View.INVISIBLE);
                            }

                            //txtViewFridge.setText(FridgeMonitor.fridgeDescription);
                            //txtSpeechInput.setText("turning off the lights...");
                            //startActivity(intent);
                        }
                    };
                    btnActivate.setOnClickListener(onClickListener);
                }
                break;
            }

        }

    }
}
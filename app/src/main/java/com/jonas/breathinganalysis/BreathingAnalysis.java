package com.jonas.breathinganalysis;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.pitch.PitchProcessor;

import static android.content.ContentValues.TAG;

public class BreathingAnalysis extends Activity implements OnMetronomeDoneListener, OnVolumeDetectedListener, AdapterView.OnItemSelectedListener, OnSavingDoneListener {

    private static final int SENSOR_IDS[] = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE};
    private static final String SENSOR_NAMES[] = {"Accelerometer", "Gyroscope"};
    private static final String SENSOR_ENTRIES[][] =
            {{"accelerometer-x-axis", "accelerometer-y-axis", "accelerometer-z-axis"},
             {"gyroscope-x-axis", "gyroscope-y-axis", "gyroscope-z-axis"}};
    static final String[] EXERCISE_IDS = {"long-tone-piano", "long-tone-forte",
            "scale-slurred", "scale-tongued", "octave-jump-piano", "octave-jump-forte"};

    private Button measurementController;
    private Spinner spinner;
    private EditText nameInput, instrumentInput;


    private String exerciseId;


    private static final int SAMPLERATE = 22050;
    private static final int BUFFER = 1024;
    private static final int OVERLAP = 0;
    private static final double SENSITIVITY = 65;
    private static final double THRESHOLD = PercussionOnsetDetector.DEFAULT_THRESHOLD * 1.5;

    private static final int DEFAULT_TUNING = 442;
    private int tuning;

    private SilenceDetector silenceDetector;

    private ArrayList<Recorder> recorders;

    private Metronome metronome;

    private ArrayList<FeatureVector> featureVectors;

    private Thread dispatcher;

    private PitchRecorder pitchRecorder;
    private VolumeRecorder volumeRecorder;
    private PercussionRecorder percussionRecorder;

    private ArrayList<AudioProcessor> audioProcessors;

    AudioDispatcher audioDispatcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestPermissions();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tuning = DEFAULT_TUNING;

        recorders = new ArrayList<>();

        featureVectors = new ArrayList<>();

        initializeSensorRecorders();

        metronome = Metronome.newInstance();

        initializeFragments();

        installButton();
        installSpinner();
        nameInput = findViewById(R.id.nameInput);
        instrumentInput = findViewById(R.id.instrumentInput);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 42);
    }

    private void installButton() {
        measurementController = findViewById(R.id.button_send);
        measurementController.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(measurementController.getText().equals("Start")) {



                    spinner.setEnabled(false);
                    nameInput.setEnabled(false);
                    instrumentInput.setEnabled(false);

                    Recorder.startRecording();

                    metronome.begin();
                    measurementController.setText(R.string.stop);
                }
                else {
                    spinner.setEnabled(true);
                    nameInput.setEnabled(true);
                    instrumentInput.setEnabled(true);

                    Recorder.stopRecording();
                    metronome.reset();
                    metronome.interrupt();
                    measurementController.setText(R.string.start);
                }

            }
        });
    }

    private void installSpinner() {
        spinner = findViewById(R.id.exercise_spinner);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.exercise_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }


    private void initializeSensorRecorders() {
        SensorRecorder[] sensorRecorders = new SensorRecorder[SENSOR_IDS.length];

        for(int i = 0; i < sensorRecorders.length; i++) {
            sensorRecorders[i] = SensorRecorder.newInstance(SENSOR_NAMES[i], SENSOR_ENTRIES[i]);
        }

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor[] sensors = new Sensor[sensorRecorders.length];

        for(int i = 0; i < sensorRecorders.length; i++) {
            if (sensorManager.getDefaultSensor(SENSOR_IDS[i]) != null) {
                sensors[i] = sensorManager.getDefaultSensor(SENSOR_IDS[i]);
                sensorManager.registerListener(sensorRecorders[i], sensors[i], SensorManager.SENSOR_DELAY_FASTEST);
            }
            else {
                Log.d(TAG, "No " + SENSOR_NAMES[i] + " available!");
            }
        }

        pitchRecorder = new PitchRecorder();
        silenceDetector = new SilenceDetector(THRESHOLD,false);
        volumeRecorder = new VolumeRecorder();
        percussionRecorder = new PercussionRecorder();



        Collections.addAll(recorders, sensorRecorders);
        recorders.add(pitchRecorder);
        recorders.add(volumeRecorder);
        recorders.add(percussionRecorder);
    }

    private void startAudioRecording() {
        audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLERATE, BUFFER, OVERLAP);

        this.audioProcessors = new ArrayList<>();

        audioProcessors.add(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLERATE, BUFFER, pitchRecorder));
        audioProcessors.add(silenceDetector);
        audioProcessors.add(volumeRecorder);
        audioProcessors.add(new PercussionOnsetDetector(SAMPLERATE, BUFFER, percussionRecorder, SENSITIVITY,THRESHOLD));

        for (AudioProcessor audioProcessor : audioProcessors) {
            audioDispatcher.addAudioProcessor(audioProcessor);
        }

        this.dispatcher = new Thread(audioDispatcher);
        this.dispatcher.start();
    }

    private void initializeFragments() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        for (Recorder recorder : recorders) {
            fragmentTransaction.add(R.id.generalized_sensor_fragment, recorder, recorder.getSensorName());
        }
        fragmentTransaction.add(metronome, "Metronome");

        fragmentTransaction.commit();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(String permission: permissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Log.e("denied", permission);
                requestPermissions();
            }
            else {
                if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    Log.e("allowed", permission);
                    if(permission.equals("android.permission.RECORD_AUDIO")) {
                        startAudioRecording();
                    }
                }
                else {
                    Log.e("set to never ask again", permission);
                    showAlertBox2();
                }
            }
        }
    }



    @Override
    public void onMetronomeDone(long bestFittingStartTimestamp, long overallDuration) {
        Recorder.stopRecording();

        ArrayList<MeasurementSeries> allRecordedSensorData = new ArrayList<>();

        for (Recorder recorder : recorders) {
            allRecordedSensorData.add(new MeasurementSeries(recorder.getSensorData(), recorder.getEntryNames()));
        }
        allRecordedSensorData.add(new MeasurementSeries(metronome.getSensorData(), new String[]{"beat-played"}));

        featureVectors.clear();

        featureVectors.add(new FeatureVector("instrument", instrumentInput.getText().toString()));
        featureVectors.add(new FeatureVector("player-name", nameInput.getText().toString()));
        featureVectors.add(new FeatureVector("exercise-name", exerciseId));
        featureVectors.add(new FeatureVector("overall-duration", Long.toString(overallDuration)));
        featureVectors.add(new FeatureVector("duration-of-one-beat", Long.toString(metronome.getDurationOfOneBeat())));
        featureVectors.add(new FeatureVector("beats-per-bar", Integer.toString(metronome.getBeatsPerBar())));
        featureVectors.add(new FeatureVector("bpm", Integer.toString(metronome.getBpm())));
        featureVectors.add(new FeatureVector("tuning", Integer.toString(this.tuning)));

        File csvFile = DataLogger.getFilePath(nameInput.getText().toString(), instrumentInput.getText().toString(), exerciseId, ".csv");
        File arffFile = DataLogger.getFilePath(nameInput.getText().toString(), instrumentInput.getText().toString(), exerciseId, ".arff");

        DataHandler dataHandler = new DataHandler(new MeasuredData(allRecordedSensorData, bestFittingStartTimestamp, overallDuration), featureVectors, csvFile, arffFile);
        dataHandler.setOnSavingDoneListener(this);
        dataHandler.start();


        measurementController.performClick();
        measurementController.setEnabled(false);
    }

    @Override
    public double getSPL() {
        return silenceDetector.currentSPL();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        this.exerciseId = EXERCISE_IDS[pos];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void savingDone(final boolean savingFailed) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                measurementController.setEnabled(true);
                spinner.setEnabled(true);
                nameInput.setEnabled(true);
                instrumentInput.setEnabled(true);
                if(savingFailed) {
                    showAlertBox();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Saving successful!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        for (Recorder recorder : recorders) {
            recorder.clearSensorData();
        }
    }

    private void showAlertBox2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permission(s) missing");

        builder.setMessage("All permissions have to be granted to use this app!\nGrant all permissions via:\n'Settings -> Apps -> BreathingAnalysis -> Permissions'")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showAlertBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Saving error");

        builder.setMessage("The recorded data did not get saved!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("Clicked ok");
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onPause() {
        if(dispatcher != null) {
            audioDispatcher.stop();
            for (AudioProcessor audioProcessor : audioProcessors) {
                audioDispatcher.removeAudioProcessor(audioProcessor);
            }
            audioProcessors.clear();
        }
        super.onPause();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        startAudioRecording();
    }
}
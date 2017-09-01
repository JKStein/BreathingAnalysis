package com.jonas.breathinganalysis;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.pitch.PitchProcessor;

import static android.content.ContentValues.TAG;

public class BreathingAnalysis extends Activity implements OnMetronomeDoneListener, OnVolumeDetectedListener, AdapterView.OnItemSelectedListener, OnSavingDoneListener {

    private static final int SENSOR_IDS[] = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD};
    private static final String SENSOR_NAMES[] = {"Accelerometer", "Gyroscope", "Magnetometer"};
    private static final String SENSOR_ENTRIES[][] =
            {{"Accelerometer x-Axis", "Accelerometer y-Axis", "Accelerometer z-Axis"},
             {"Gyroscope x-Axis", "Gyroscope y-Axis", "Gyroscope z-Axis"},
             {"Magnetometer x-Axis", "Magnetometer y-Axis", "Magnetometer z-Axis"}};
    static final String[] EXERCISE_IDS = {"long-tone-piano", "long-tone-forte",
            "scale-slurred", "scale-tongued", "octave-jump-piano", "octave-jump-forte"};

    private Button measurementController;
    private Spinner spinner;
    private EditText nameInput;


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    }

    private void installButton() {
        measurementController = findViewById(R.id.button_send);
        measurementController.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(measurementController.getText().equals("Start")) {
                    spinner.setEnabled(false);
                    nameInput.setEnabled(false);

                    Recorder.startRecording();

                    metronome.begin();
                    measurementController.setText(R.string.stop);
                }
                else {
                    spinner.setEnabled(true);
                    nameInput.setEnabled(true);

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

        PitchRecorder pitchRecorder = PitchRecorder.newInstance(this.tuning);
        silenceDetector = new SilenceDetector(THRESHOLD,false);
        VolumeRecorder volumeRecorder = new VolumeRecorder();
        PercussionRecorder percussionRecorder = new PercussionRecorder();



        Collections.addAll(recorders, sensorRecorders);
        recorders.add(pitchRecorder);
        recorders.add(volumeRecorder);
        recorders.add(percussionRecorder);

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLERATE, BUFFER, OVERLAP);

        //Pitch and its probability
        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLERATE, BUFFER, pitchRecorder));
        //SPL
        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(volumeRecorder);
        //Percussion event
        dispatcher.addAudioProcessor(new PercussionOnsetDetector(SAMPLERATE, BUFFER, percussionRecorder, SENSITIVITY,THRESHOLD));

        new Thread(dispatcher, "Audio Dispatcher").start();
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onMetronomeDone(long bestFittingStartTimestamp, long overallDuration) {
        Recorder.stopRecording();

        ArrayList<MeasurementSeries> allRecordedSensorData = new ArrayList<>();

        for (Recorder recorder : recorders) {
            allRecordedSensorData.add(new MeasurementSeries(recorder.getSensorData(), recorder.getEntryNames()));
        }
        allRecordedSensorData.add(new MeasurementSeries(metronome.getSensorData(), new String[]{"Beat played"}));

        featureVectors.add(new FeatureVector("player-name", nameInput.getText().toString()));
        featureVectors.add(new FeatureVector("exercise-name", exerciseId));
        featureVectors.add(new FeatureVector("overallDuration", Long.toString(overallDuration)));
        featureVectors.add(new FeatureVector("durationOfOneBeat", Long.toString(metronome.getDurationOfOneBeat())));
        featureVectors.add(new FeatureVector("beatsPerBar", Integer.toString(metronome.getBeatsPerBar())));
        featureVectors.add(new FeatureVector("bpm", Integer.toString(metronome.getBpm())));
        featureVectors.add(new FeatureVector("tuning", Integer.toString(this.tuning)));

        DataHandler dataHandler = new DataHandler(new MeasuredData(allRecordedSensorData, bestFittingStartTimestamp, overallDuration), featureVectors);
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
    public void savingDone() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                measurementController.setEnabled(true);
                spinner.setEnabled(true);
                nameInput.setEnabled(true);
            }
        });
        for (Recorder recorder : recorders) {
            recorder.clearSensorData();
        }
    }
}
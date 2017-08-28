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
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.pitch.PitchProcessor;

import static android.content.ContentValues.TAG;

public class BreathingAnalysis extends Activity implements OnMetronomeDoneListener, OnVolumeDetectedListener{

    private static final int SENSOR_IDS[] = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD};
    private static final String SENSOR_NAMES[] = {"Accelerometer", "Gyroscope", "Magnetometer"};
    private static final String SENSOR_ENTRIES[][] =
            {{"Accelerometer x-Axis", "Accelerometer y-Axis", "Accelerometer z-Axis"},
             {"Gyroscope x-Axis", "Gyroscope y-Axis", "Gyroscope z-Axis"},
             {"Magnetometer x-Axis", "Magnetometer y-Axis", "Magnetometer z-Axis"}};
    private SensorRecorder[] sensorRecorders;
    private Sensor[] sensors;
    private SensorManager sensorManager;

    private Button measurementController;


    private static final int SAMPLERATE = 22050;
    private static final int BUFFER = 1024;
    private static final int OVERLAP = 0;
    private static final double SENSITIVITY = 65;
    private static final double THRESHOLD = PercussionOnsetDetector.DEFAULT_THRESHOLD * 1.5;

    SilenceDetector silenceDetector;
    PitchRecorder pitchRecorder;
    VolumeRecorder volumeRecorder;
    PercussionRecorder percussionRecorder;

    ArrayList<Recorder> recorders;

    Metronome metronome;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorders = new ArrayList<>();

        initializeSensorRecorders();
        initializeFragments();
        startAudioDispatcher();

        installButton();

    }

    private void installButton() {
        measurementController = findViewById(R.id.button_send);
        measurementController.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(measurementController.getText().equals("Start")) {

                    for (Recorder recorder : recorders) {
                        recorder.clearSensorData();
                    }
                    Recorder.startRecording();

                    metronome.begin();
                    measurementController.setText(R.string.stop);
                }
                else {
                    Recorder.stopRecording();
                    metronome.reset();
                    metronome.interrupt();
                    measurementController.setText(R.string.start);
                }

            }
        });
    }


    private void initializeSensorRecorders() {
        sensorRecorders = new SensorRecorder[SENSOR_IDS.length];

        for(int i = 0; i < sensorRecorders.length; i++) {
            sensorRecorders[i] = SensorRecorder.newInstance(SENSOR_NAMES[i], SENSOR_ENTRIES[i]);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensors = new Sensor[sensorRecorders.length];

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
        metronome = Metronome.newInstance();


        Collections.addAll(recorders, this.sensorRecorders);
        recorders.add(pitchRecorder);
        recorders.add(volumeRecorder);
        recorders.add(percussionRecorder);
    }

    private void initializeFragments() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();


        for(int i = 0; i < sensorRecorders.length; i++) {
            fragmentTransaction.add(R.id.generalized_sensor_fragment, sensorRecorders[i], SENSOR_NAMES[i]);
        }
        fragmentTransaction.add(R.id.generalized_sensor_fragment, pitchRecorder, "Pitch-Recorder");
        fragmentTransaction.add(R.id.generalized_sensor_fragment, volumeRecorder, "Volume-Recorder");
        fragmentTransaction.add(R.id.generalized_sensor_fragment, percussionRecorder, "Percussion-Recorder");
        fragmentTransaction.add(metronome, "Metronome");

        fragmentTransaction.commit();
    }

    private void startAudioDispatcher() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLERATE,BUFFER,OVERLAP);
        //Pitch and its probability
        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLERATE, BUFFER, pitchRecorder));

        //SPL
        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(volumeRecorder);

        //Percussion event
        dispatcher.addAudioProcessor(new PercussionOnsetDetector(SAMPLERATE, BUFFER, percussionRecorder, SENSITIVITY,THRESHOLD));

        new Thread(dispatcher,"Audio Dispatcher").start();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(int i = 0; i < SENSOR_IDS.length; i++) {
            sensorManager.registerListener(sensorRecorders[i], sensors[i], SensorManager.SENSOR_DELAY_FASTEST);
        }
        //TODO care for audio recorders and metronome
    }

    @Override
    protected void onPause() {
        super.onPause();
        for(int i = 0; i < SENSOR_IDS.length; i++) {
            sensorManager.unregisterListener(sensorRecorders[i]);
        }
        //TODO care for audio recorders and metronome
    }

    @Override
    public void onMetronomeDone(long bestFittingStartTimestamp, long overallDuration) {
        //Stop recording measured data.
        Recorder.stopRecording();

        ArrayList<MeasurementSeries> allRecordedSensorData = new ArrayList<>();
        for(int i = 0; i < SENSOR_IDS.length; i++) {
            allRecordedSensorData.add(new MeasurementSeries(sensorRecorders[i].getSensorData(), SENSOR_ENTRIES[i]));
        }

        allRecordedSensorData.add(new MeasurementSeries(pitchRecorder.getSensorData(), PitchRecorder.ENTRY_NAMES));
        allRecordedSensorData.add(new MeasurementSeries(volumeRecorder.getSensorData(), VolumeRecorder.ENTRY_NAMES));
        allRecordedSensorData.add(new MeasurementSeries(percussionRecorder.getSensorData(), PercussionRecorder.ENTRY_NAMES));

        (new DataHandler(new MeasuredData(allRecordedSensorData, bestFittingStartTimestamp, overallDuration))).start();


        measurementController.performClick();
    }

    @Override
    public double getSPL() {
        return silenceDetector.currentSPL();
    }
}
package com.jonas.breathinganalysis;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

import static android.os.SystemClock.uptimeMillis;

public class VolumeRecorder extends Fragment implements AudioProcessor {

    /**
     * The names of the collected data.
     */
    static final String[] ENTRY_NAMES = {"Sound Pressure Length"};
    /**
     * The values collected by the sensor.
     */
    private ArrayList<SensorDate> sensorData;
    /**
     * Only if this attribute is true, the measured values will be stored.
     */
    private boolean recording;

    private Activity activity;
    private View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sensorData = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.volume_fragment, container, false);
        return this.view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = getActivity();
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        final float[] sensorValues = {(float) ((OnVolumeDetectedListener) activity).getSPL()};

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) view.findViewById(R.id.volume)).setText(String.format(Locale.US, "%f", sensorValues[0]));
            }
        });

        if(recording) {
            sensorData.add(new SensorDate(uptimeMillis(), sensorValues));
        }

        return true;
    }

    @Override
    public void processingFinished() {

    }

    /**
     * Getter for the {@link java.util.ArrayList} containing the captured sensor data.
     * @return An {@link java.util.ArrayList} containing the captured sensor data.
     */
    ArrayList<SensorDate> getSensorData() {
        return this.sensorData;
    }
    /**
     * Starts the scoring of all measured sensor data.
     */
    void startRecording() {
        this.recording = true;
    }

    /**
     * Stop the scoring of all measured sensor data.
     */
    void stopRecording() {
        this.recording = false;
    }

    /**
     * Enables storing a new series of measurement by deleting all old data.
     */
    void clearSensorData() {
        this.sensorData.clear();
    }
}
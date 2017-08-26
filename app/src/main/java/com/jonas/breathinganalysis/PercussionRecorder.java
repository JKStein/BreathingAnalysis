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

import be.tarsos.dsp.onsets.OnsetHandler;

import static android.os.SystemClock.uptimeMillis;

/**
 * @author Jonas Stein
 */

public class PercussionRecorder extends Fragment implements OnsetHandler {

    /**
     * The names of the collected data.
     */
    static final String[] ENTRY_NAMES = {"Time", "Salience"};
    /**
     * The values collected by the sensor.
     */
    private ArrayList<SensorDate> sensorData;
    /**
     * Only if this attribute is true, the measured values will be stored.
     */
    private boolean recording;

    private int percussionEventCounter;

    private Activity activity;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       this.view = inflater.inflate(R.layout.percussion_fragment, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.percussionEventCounter = 0;
        this.sensorData = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = getActivity();
    }

    @Override
    public void handleOnset(double time, double salience) {
        //TODO: check out time and salience

        float[] sensorValues = {(float) time, (float) salience};

        percussionEventCounter++;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) view.findViewById(R.id.percussion)).setText(String.format(Locale.US, "%d", percussionEventCounter));
            }
        });


        if(recording) {
            this.sensorData.add(new SensorDate(uptimeMillis(), sensorValues));
        }
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

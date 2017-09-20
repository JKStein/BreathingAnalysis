package com.jonas.breathinganalysis;

import android.os.Bundle;
import android.os.SystemClock;

import be.tarsos.dsp.onsets.OnsetHandler;

/**
 * @author Jonas Stein
 */

public class PercussionRecorder extends Recorder implements OnsetHandler {

    /**
     * The name of this sensor.
     */
    static final String SENSOR_NAME = "Percussion";
    /**
     * The names of the collected data.
     */
    static final String[] ENTRY_NAMES = {"Time"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSensorName(SENSOR_NAME);
        setEntryNames(ENTRY_NAMES);
    }

    /**
     * Gets called when a percussion event occurs.
     * Updates the textViews with passed time argument and stores its value if recording.
     * @param time The amount of seconds passed since the start of the recording.
     * @param salience This value seems to be a constant (-1).
     */
    @Override
    public void handleOnset(double time, double salience) {
        final float[] sensorValues = {(float) time};
        update(SystemClock.elapsedRealtime(), sensorValues, true);
    }
}

package com.jonas.breathinganalysis;

import android.os.Bundle;

import be.tarsos.dsp.onsets.OnsetHandler;

import static android.os.SystemClock.uptimeMillis;

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
    static final String[] ENTRY_NAMES = {"Time", "Salience"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSensorName(SENSOR_NAME);
        setEntryNames(ENTRY_NAMES);
    }

    @Override
    public void handleOnset(double time, double salience) {
        final float[] sensorValues = {(float) time, (float) salience};
        update(uptimeMillis(), sensorValues, true);
    }
}

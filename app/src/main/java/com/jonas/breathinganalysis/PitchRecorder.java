package com.jonas.breathinganalysis;

import android.os.Bundle;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

import static android.os.SystemClock.uptimeMillis;

/**
 * @author Jonas Stein
 */

public class PitchRecorder extends Recorder implements PitchDetectionHandler{

    private static final int DEFAULT_TUNING = 442;
    /**
     * The names of the collected data.
     */
    static final String[] ENTRY_NAMES = {"Pitch", "Probability"};
    /**
     * The name of this Sensor.
     */
    static final String SENSOR_NAME = "Pitch";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSensorName(SENSOR_NAME);
        setEntryNames(ENTRY_NAMES);
    }

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {

        final float pitch = pitchDetectionResult.getPitch();
        final float probability = pitchDetectionResult.getProbability();

        final float[] sensorValues = {pitch, probability};

        //audioEvent.getTimeStamp() would return the amount of seconds passed
        //since the start of the recording.
        update(uptimeMillis(), sensorValues, true);
    }
}

package com.jonas.breathinganalysis;

import android.os.Bundle;
import android.os.SystemClock;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

/**
 * An Audio Processor recording the sound pressure length detected.
 * @author Jonas Stein
 */
public class VolumeRecorder extends Recorder implements AudioProcessor {

    /**
     * The name of this sensor.
     */
    static final String SENSOR_NAME = "Volume";
    /**
     * The names of the collected data.
     */
    static final String[] ENTRY_NAMES = {"sound-pressure-length"};

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setSensorName(SENSOR_NAME);
        setEntryNames(ENTRY_NAMES);
    }

    /**
     * Retrieves the spl from the SilenceDetector, displays and (if recording) stores it.
     * @param audioEvent A new AudioEvent pushed to the AudioProcessor.
     * @return True.
     */
    @Override
    public boolean process(AudioEvent audioEvent) {
        final float[] sensorValues = {(float) ((OnVolumeDetectedListener) super.getActivity()).getSPL()};

        //audioEvent.getTimeStamp() would return the amount of seconds passed
        //since the start of the recording.
        update(SystemClock.elapsedRealtime(), sensorValues, true);
        return true;
    }

    /**
     * Gets called when the processing of the AudioEvent finished.
     * Nothing needs to be done.
     */
    @Override
    public void processingFinished() {

    }
}
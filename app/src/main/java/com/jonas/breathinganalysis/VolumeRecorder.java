package com.jonas.breathinganalysis;

import android.os.Bundle;
import android.os.SystemClock;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class VolumeRecorder extends Recorder implements AudioProcessor {

    /**
     * The name of this sensor.
     */
    static final String SENSOR_NAME = "Volume";
    /**
     * The names of the collected data.
     */
    static final String[] ENTRY_NAMES = {"Sound Pressure Length"};

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setSensorName(SENSOR_NAME);
        setEntryNames(ENTRY_NAMES);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        final float[] sensorValues = {(float) ((OnVolumeDetectedListener) super.getActivity()).getSPL()};

        //audioEvent.getTimeStamp() would return the amount of seconds passed
        //since the start of the recording.
        update(SystemClock.elapsedRealtime(), sensorValues, true);

        return true;
    }

    @Override
    public void processingFinished() {

    }
}
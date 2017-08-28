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
    static final String[] ENTRY_NAMES = {"Pitch", "Probability", "MIDI Note", "Pitch Deviation"};
    /**
     * The name of this Sensor.
     */
    static final String SENSOR_NAME = "Pitch";

    private float[] midiTable;

    @SuppressWarnings("unused")
    public PitchRecorder newInstance(int tuning) {
        PitchRecorder pitchRecorder = new PitchRecorder();
        Bundle bundle = new Bundle();
        bundle.putInt("tuning", tuning);
        pitchRecorder.setArguments(bundle);
        return pitchRecorder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.midiTable = Normalizer.midiTable(getArguments().getInt("tuning", DEFAULT_TUNING));
        }
        else {
            this.midiTable = Normalizer.midiTable(DEFAULT_TUNING);
        }
        setSensorName(SENSOR_NAME);
        setEntryNames(ENTRY_NAMES);
    }

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        //TODO: check out audioEvent.getSampleRate() and audioEvent.getTimestamp()

        final float pitch = pitchDetectionResult.getPitch();
        final float probability = pitchDetectionResult.getProbability();
        final int midiNote = Normalizer.getMidiNote(pitch, this.midiTable);
        final float deviation = Normalizer.getPitchDeviation(pitch, this.midiTable);

        final float[] sensorValues = {pitch, probability, midiNote, deviation};

        update(uptimeMillis(), sensorValues, true);
    }
}

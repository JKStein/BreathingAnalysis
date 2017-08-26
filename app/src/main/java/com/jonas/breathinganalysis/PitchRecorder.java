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
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

import static android.os.SystemClock.uptimeMillis;

/**
 * @author Jonas Stein
 */

public class PitchRecorder extends Fragment implements PitchDetectionHandler{

    private static final int DEFAULT_TUNING = 442;
    /**
     * The names of the collected data.
     */
    static final String[] ENTRY_NAMES = {"Pitch", "Probability", "MIDI Note", "Pitch Deviation"};
    /**
     * The values collected by the sensor.
     */
    private ArrayList<SensorDate> sensorData;
    /**
     * Only if this attribute is true, the measured values will be stored.
     */
    private boolean recording;

    private float[] midiTable;

    private Activity activity;
    private View view;

    @SuppressWarnings("unused")
    public PitchRecorder newInstance(int tuning) {
        PitchRecorder pitchRecorder = new PitchRecorder();
        Bundle bundle = new Bundle();
        bundle.putInt("tuning", tuning);
        pitchRecorder.setArguments(bundle);
        return pitchRecorder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.pitch_fragment, container, false);
        return this.view;
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
        this.sensorData = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = getActivity();
    }

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        //TODO: check out audioEvent.getSampleRate() and audioEvent.getTimestamp()

        final float pitch = pitchDetectionResult.getPitch();
        final float probability = pitchDetectionResult.getProbability();
        final int midiNote = Normalizer.getMidiNote(pitch, this.midiTable);
        final float deviation = Normalizer.getPitchDeviation(pitch, this.midiTable);

        float[] sensorValues = {pitch, probability, midiNote, deviation};

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) view.findViewById(R.id.currentPitch)).setText(String.format(Locale.US, "%f", pitch));
                ((TextView) view.findViewById(R.id.currentProbability)).setText(String.format(Locale.US, "%f", probability));
                ((TextView) view.findViewById(R.id.midiNote)).setText(Normalizer.midiNoteToString(midiNote));
                ((TextView) view.findViewById(R.id.deviation)).setText(String.format(Locale.US, "%f", deviation));
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

package com.jonas.breathinganalysis;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

import static android.os.SystemClock.uptimeMillis;

public class VolumeRecorder extends Recorder implements AudioProcessor {

    /**
     * The names of the collected data.
     */
    static final String[] ENTRY_NAMES = {"Sound Pressure Length"};

    private Activity activity;
    private View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        if(isRecording()) {
            getSensorData().add(new SensorDate(uptimeMillis(), sensorValues));
        }

        return true;
    }

    @Override
    public void processingFinished() {

    }
}
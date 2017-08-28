package com.jonas.breathinganalysis;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import be.tarsos.dsp.onsets.OnsetHandler;

import static android.os.SystemClock.uptimeMillis;

/**
 * @author Jonas Stein
 */

public class PercussionRecorder extends Recorder implements OnsetHandler {

    /**
     * The names of the collected data.
     */
    static final String[] ENTRY_NAMES = {"Time", "Salience"};

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


        if(isRecording()) {
            getSensorData().add(new SensorDate(uptimeMillis(), sensorValues));
        }
    }
}

package com.jonas.breathinganalysis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Measures, stores and displays all available sensor data of a particular sensor.
 * @author Jonas Stein
 */
public class SensorRecorder extends Recorder implements SensorEventListener{
    /**
     * The amount of nanoseconds in one millisecond.
     */
    private static final long NANOSECONDS_PER_MILLISECOND = 1000000L;


    public static SensorRecorder newInstance(String sensorName, String[] entryNames) {
        SensorRecorder sensorRecorder = new SensorRecorder();
        Bundle bundle = new Bundle();
        bundle.putString("sensorName", sensorName);
        bundle.putStringArray("entryNames", entryNames);
        sensorRecorder.setArguments(bundle);
        return sensorRecorder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            setSensorName(getArguments().getString("sensorName", DEFAULT_SENSOR_NAME));
            setEntryNames(getArguments().getStringArray("entryNames"));
        }
        else {
            setSensorName(DEFAULT_SENSOR_NAME);
            setEntryNames(new String[]{DEFAULT_ENTRY_NAME});
        }
    }

    /**
     * Updates the {@link android.widget.TextView TextViews} illustrating the current sensor
     * values and adds the new data to the sensorData {@link java.util.ArrayList}.
     * Gets called when there is a new {@link android.hardware.SensorEvent}.
     * @param event The {@link android.hardware.SensorEvent} that has been sent,
     * containing the newly captured sensor values.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        final float[] values = event.values.clone();
        //event.timestamp yields the timestamp of the SensorEvent in nanoseconds,
        // but overall measurement is based on milliseconds.
        final long timestamp = event.timestamp / NANOSECONDS_PER_MILLISECOND;

        //Display the new sensor values - no runOnUi() needed, the SensorManager (running
        //on the UI thread) is the thread calling this method!
        update(timestamp, values, false);

    }

    /**
     * Logs the change of accuracy.
     * Called when the accuracy of a {@link android.hardware.Sensor} changed.
     * @param sensor The ID of the sensor being monitored.
     * @param accuracy The new accuracy of this sensor.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (accuracy) {
            case 0:
                Log.d(TAG, "The accuracy of the " + getSensorName() + " has changed to: unreliable");
            case 1:
                Log.d(TAG, "The accuracy of the " + getSensorName() + " has changed to: low");
            case 2:
                Log.d(TAG, "The accuracy of the " + getSensorName() + " has changed to: medium");
            case 3:
                Log.d(TAG, "The accuracy of the " + getSensorName() + " has changed to: high");
        }
    }
}

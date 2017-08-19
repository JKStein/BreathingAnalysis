package com.jonas.breathinganalysis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Measures, stores and displays all available sensor data of a particular sensor.
 * @author Jonas Stein
 */
class SensorRecorder implements SensorEventListener{

    /**
     * The values collected by the sensor.
     */
    private ArrayList<SensorDate> sensorData;
    /**
     * The amount of nanoseconds in one millisecond.
     */
    private static final long NANOSECONDS_PER_MILLISECOND = 1000000L;
    /**
     * The {@link android.widget.TextView TextViews} illustrating the sensor values.
     */
    private TextView xAxis, yAxis, zAxis;
    /**
     * Only if this attribute is true, the measured values will be stored.
     */
    private boolean recording;

    /**
     * Initializes the {@link android.widget.TextView TextViews} illustrating the sensor values,
     * as well as the {@link java.util.ArrayList} containing the values collected by the sensor.
     */
    SensorRecorder(TextView xAxis, TextView yAxis, TextView zAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zAxis = zAxis;
        this.recording = false;
        this.sensorData = new ArrayList<>();
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
        //Display the new sensor values.

        //Float.toString() could not work properly, because the separator (dot or comma) is unknown.
        xAxis.setText(String.format(Locale.US, "%f", event.values[0]));
        yAxis.setText(String.format(Locale.US, "%f", event.values[1]));
        zAxis.setText(String.format(Locale.US, "%f", event.values[2]));

        //Add new values to the series of measurement if recording.
        if(recording) {
            //event.timestamp yields the timestamp of the SensorEvent in nanoseconds,
            // but overall measurement is based on milliseconds.
            sensorData.add(new SensorDate(event.timestamp / NANOSECONDS_PER_MILLISECOND,
                    event.values[0], event.values[1], event.values[2]));
        }
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
                Log.d(TAG, "The accuracy of the " + sensor.getName() + " has changed to: unreliable");
            case 1:
                Log.d(TAG, "The accuracy of the " + sensor.getName() + " has changed to: low");
            case 2:
                Log.d(TAG, "The accuracy of the " + sensor.getName() + " has changed to: medium");
            case 3:
                Log.d(TAG, "The accuracy of the " + sensor.getName() + " has changed to: high");
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
        sensorData.clear();
    }
}

package com.jonas.breathinganalysis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Measures, stores and displays all available sensor data of the magnetometer.
 * @author Jonas Stein
 */
class MagnetRecorder implements SensorEventListener  {

    /**
     * The main object running the UI and administrating the app.
     */
    private BreathingAnalysis breathingAnalysis;
    /**
     * The {@link android.widget.TextView TextViews} illustrating the sensor values.
     */
    private TextView currentX, currentY, currentZ;

    /**
     * Initializes the {@link android.widget.TextView TextViews} illustrating the sensor values,
     * as well as the attribute breathingAnalysis with the object passed to the constructor.
     * @param breathingAnalysis The main object running the UI and administrating the app.
     */
    MagnetRecorder(BreathingAnalysis breathingAnalysis) {
        this.breathingAnalysis = breathingAnalysis;
        currentX = (TextView) breathingAnalysis.findViewById(R.id.currentXM);
        currentY = (TextView) breathingAnalysis.findViewById(R.id.currentYM);
        currentZ = (TextView) breathingAnalysis.findViewById(R.id.currentZM);
    }

    /**
     * Updates the {@link android.widget.TextView TextViews} illustrating the current
     * magnetometer sensor values and adds the new data to the corresponding
     * {@link java.util.ArrayList ArrayList} storing the sensor data in breathingAnalysis.
     * Gets called when there is a new {@link android.hardware.SensorEvent}.
     * @param event The {@link android.hardware.SensorEvent} that has been sent,
     * containing the newly captured sensor values.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Display changed values.

        //Float.toString would give warning, because the separator (dot or comma) is unknown.
        currentX.setText(String.format(Locale.US, "%f", event.values[0]));
        currentY.setText(String.format(Locale.US, "%f", event.values[1]));
        currentZ.setText(String.format(Locale.US, "%f", event.values[2]));

        //Add new values to series of measurement.
        breathingAnalysis.magnetSensorValues.add(new SensorDate(event.timestamp / 1000000L, event.values[0], event.values[1], event.values[2]));
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
                Log.d(TAG, "The accuracy of the accelerometer has changed to: unreliable");
            case 1:
                Log.d(TAG, "The accuracy of the accelerometer has changed to: low");
            case 2:
                Log.d(TAG, "The accuracy of the accelerometer has changed to: medium");
            case 3:
                Log.d(TAG, "The accuracy of the accelerometer has changed to: high");
        }
    }
}


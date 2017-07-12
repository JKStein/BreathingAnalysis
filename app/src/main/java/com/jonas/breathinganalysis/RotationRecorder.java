package com.jonas.breathinganalysis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Measures, stores and displays all available sensor data of the gyroscope.
 * @author Jonas Stein
 */
class RotationRecorder implements SensorEventListener{

    private BreathingAnalysis breathingAnalysis;
    private TextView currentX, currentY, currentZ;

    /**
     * Initializes the {@link android.widget.TextView TextViews} illustrating the sensor values.
     * @param breathingAnalysis The main object running the UI and administrates the app.
     */
    RotationRecorder(BreathingAnalysis breathingAnalysis) {
        this.breathingAnalysis = breathingAnalysis;
        currentX = (TextView) breathingAnalysis.findViewById(R.id.currentXG);
        currentY = (TextView) breathingAnalysis.findViewById(R.id.currentYG);
        currentZ = (TextView) breathingAnalysis.findViewById(R.id.currentZG);
    }

    /**
     * Updates the TextViews illustrating the current gyroscope sensor values and
     * adds the new data to the respective ArrayList in breathingAnalysis.
     * Gets called when there is a new SensorEvent.
     * @param event The SensorEvent that has been sent, containing the newly captured sensor values.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Display the new sensor values.

        //Float.toString could not work properly, because the separator (dot or comma) is unknown.
        currentX.setText(String.format(Locale.US, "%f", event.values[0]));
        currentY.setText(String.format(Locale.US, "%f", event.values[1]));
        currentZ.setText(String.format(Locale.US, "%f", event.values[2]));

        //Add new values to the series of measurement.
        breathingAnalysis.rotationSensorValues.add(new SensorDate(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
    }

    /**
     * Called when the accuracy of a sensor changed.
     * @param sensor The ID of the sensor being monitored.
     * @param accuracy The new accuracy of this sensor.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (accuracy) {
            case 0:
                Log.d(TAG, "The accuracy of the gyroscope has changed to: unreliable");
            case 1:
                Log.d(TAG, "The accuracy of the gyroscope has changed to: low");
            case 2:
                Log.d(TAG, "The accuracy of the gyroscope has changed to: medium");
            case 3:
                Log.d(TAG, "The accuracy of the gyroscope has changed to: high");
        }
    }
}
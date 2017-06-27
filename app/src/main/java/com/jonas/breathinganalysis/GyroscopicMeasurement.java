package com.jonas.breathinganalysis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * The class implements a SensorEventListener for measure- and storing gyroscopic activities.
 * @author Jonas Stein
 */
class GyroscopicMeasurement implements SensorEventListener{

    private BreathingAnalysis breathingAnalysis;
    private TextView currentX, currentY, currentZ;

    /**
     * Initializes the class variables.
     * @param breathingAnalysis The main class object coordinating everything.
     */
    GyroscopicMeasurement(BreathingAnalysis breathingAnalysis) {
        this.breathingAnalysis = breathingAnalysis;
        initializeViews();
    }

    /**
     * Gets called when sensor values have changed.
     * Updates the TextViews illustrating the current gyroscopic sensor data values and
     * adds the new data to the respective ArrayList of breathingAnalysis.
     * @param event The SensorEvent that has occurred containing the newly captured sensor values.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //display new values

        //Float.toString would give warning, because the separator (dot or comma) is unknown
        currentX.setText(String.format(Locale.US, "%f", event.values[0]));
        currentY.setText(String.format(Locale.US, "%f", event.values[1]));
        currentZ.setText(String.format(Locale.US, "%f", event.values[2]));

        //Add new values to series of measurement
        breathingAnalysis.rotationList.add(new SensorDate(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
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

    /**
     * Initializes the TextViews illustrating the sensor data.
     */
    private void initializeViews() {
        currentX = (TextView) breathingAnalysis.findViewById(R.id.currentXG);
        currentY = (TextView) breathingAnalysis.findViewById(R.id.currentYG);
        currentZ = (TextView) breathingAnalysis.findViewById(R.id.currentZG);
    }
}
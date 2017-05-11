package com.jonas.breathinganalysis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import java.util.Locale;


class AccelerationMeasurement implements SensorEventListener {
    private BreathingAnalysis breathingAnalysis;

    private float currentXF = 0;
    private float currentYF = 0;
    private float currentZF = 0;

    private boolean loggerActivated;

    private TextView currentX, currentY, currentZ;

    AccelerationMeasurement(BreathingAnalysis breathingAnalysis) {
        this.breathingAnalysis = breathingAnalysis;
        initializeViews();
        loggerActivated = false;
        displayCleanValues();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!loggerActivated) {
            activateLogger();
        }

        // get the change of the x,y,z values of the accelerometer
        currentXF = event.values[0];
        currentYF = event.values[1];
        currentZF = event.values[2];

        displayCurrentValues();
        breathingAnalysis.accelerationChartManager.addAccelerationEntry();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    private void displayCurrentValues() {
        //Float.toString would give warning, because the separator (dot or comma) is unknown
        currentX.setText(String.format(Locale.US, "%f", currentXF));
        currentY.setText(String.format(Locale.US, "%f", currentYF));
        currentZ.setText(String.format(Locale.US, "%f", currentZF));
    }

    private void initializeViews() {
        currentX = (TextView) breathingAnalysis.findViewById(R.id.currentX);
        currentY = (TextView) breathingAnalysis.findViewById(R.id.currentY);
        currentZ = (TextView) breathingAnalysis.findViewById(R.id.currentZ);
    }

    float getCurrentXValue() {
        return currentXF;
    }

    float getCurrentYValue() {
        return currentYF;
    }

    float getCurrentZValue() {
        return currentZF;
    }

    private void activateLogger() {
        loggerActivated = true;
        AccelerationLogger accelerationLogger = new AccelerationLogger(this);
        accelerationLogger.start();
    }
}

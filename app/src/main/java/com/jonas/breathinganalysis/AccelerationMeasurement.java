package com.jonas.breathinganalysis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;


class AccelerationMeasurement implements SensorEventListener {

    private BreathingAnalysis breathingAnalysis;

    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private float currentXF = 0;
    private float currentYF = 0;
    private float currentZF = 0;

    boolean loggerActivated;


    private TextView currentX, currentY, currentZ;

    AccelerationMeasurement(BreathingAnalysis breathingAnalysis) {
        this.breathingAnalysis = breathingAnalysis;
        initializeViews();
        loggerActivated = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(loggerActivated == false) {
            activateLogger();
        }
        // clean current values
        displayCleanValues();

        // get the change of the x,y,z values of the accelerometer
        currentXF = event.values[0];
        currentYF = event.values[1];
        currentZF = event.values[2];

        // if the change is below 2, it is just plain noise
        if (Math.abs(lastX - currentXF) < 0.01)
            currentXF = lastX;
        if (Math.abs(lastY - currentYF) < 0.01) {
            currentYF = lastY;
            //y.add(currentYF);
        }
        if (Math.abs(lastZ - currentZF) < 0.01)
            currentZF = lastZ;

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

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

    // display the current x,y,z accelerometer values
    private void displayCurrentValues() {
        currentX.setText(Float.toString(currentXF));
        currentY.setText(Float.toString(currentYF));
        currentZ.setText(Float.toString(currentZF));
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

    void activateLogger() {
        loggerActivated = true;
        AccelerationLogger accelerationLogger = new AccelerationLogger(this);
        accelerationLogger.start();
    }
}

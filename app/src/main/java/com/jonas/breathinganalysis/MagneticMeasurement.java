package com.jonas.breathinganalysis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import java.util.Locale;

class MagneticMeasurement implements SensorEventListener  {

    private BreathingAnalysis breathingAnalysis;
    private TextView currentX, currentY, currentZ;

    MagneticMeasurement(BreathingAnalysis breathingAnalysis) {
        this.breathingAnalysis = breathingAnalysis;
        initializeViews();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //display changed values

        //Float.toString would give warning, because the separator (dot or comma) is unknown
        currentX.setText(String.format(Locale.US, "%f", event.values[0]));
        currentY.setText(String.format(Locale.US, "%f", event.values[1]));
        currentZ.setText(String.format(Locale.US, "%f", event.values[2]));

        //Add new values to series of measurement
        breathingAnalysis.magneticList.add(new SensorDate(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void initializeViews() {
        currentX = (TextView) breathingAnalysis.findViewById(R.id.currentXM);
        currentY = (TextView) breathingAnalysis.findViewById(R.id.currentYM);
        currentZ = (TextView) breathingAnalysis.findViewById(R.id.currentZM);
    }
}


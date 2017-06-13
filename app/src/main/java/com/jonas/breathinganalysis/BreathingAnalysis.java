package com.jonas.breathinganalysis;

import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

public class BreathingAnalysis extends Activity{
    AccelerationMeasurement accelerationMeasurement;
    AccelerationChartManager accelerationChartManager;

    DBMeasurement dbMeasurement;
    DBChartManager dbChartManager;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Testing of the interpolation tool:
        //DataPreprocessor dataPreprocessor = new DataPreprocessor();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accelerationMeasurement = new AccelerationMeasurement(this);
        accelerationChartManager = new AccelerationChartManager(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        installAccelerometer();

        //dbMeasurement = new DBMeasurement(this);
        //dbChartManager = new DBChartManager(this);
        //dbMeasurement.initializeDBMeasurement();
        //dbMeasurement.startRecorder();

        //audio processing
        new AudioProcessor(this);
    }

    private void installAccelerometer() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(accelerationMeasurement, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            System.out.println("No Accelerometer available!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //onResume() register the accelerometer for listening the events
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationMeasurement, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerationMeasurement);
    }
}
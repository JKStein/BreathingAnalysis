package com.jonas.breathinganalysis;

import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

public class BreathingAnalysis extends Activity{
    AccelerationChartManager accelerationChartManager;
    AccelerationMeasurement accelerationMeasurement;

    DBChartManager dbChartManager;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    DBMeasurement dbMeasurement;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbMeasurement = new DBMeasurement(this);
        accelerationMeasurement = new AccelerationMeasurement(this);
        accelerationChartManager = new AccelerationChartManager(this);
        dbChartManager = new DBChartManager(this);
        dbMeasurement.initializeDBMeasurement();
        dbChartManager.startRecorder();
        dbChartManager.initializeDBChart();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(accelerationMeasurement, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationMeasurement, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerationMeasurement);
    }

    public float getCurrentYValue() {
        return accelerationMeasurement.getCurrentYValue();
    }

    public float getCurrentDB() {
        return dbMeasurement.getCurrentDB();
    }

    public void setCurrentDB(float newDB) {
        dbMeasurement.setCurrentDB(newDB);
    }
}
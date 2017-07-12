package com.jonas.breathinganalysis;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;

//import com.jonas.breathinganalysis.Deprecated.DBChartManager;
//import com.jonas.breathinganalysis.Deprecated.DBMeasurement;

public class BreathingAnalysis extends Activity{

    ArrayList<SensorDate> accelerationSensorValues, rotationSensorValues, magnetSensorValues;
    ArrayList<Sound> soundEventValues;
    ArrayList<Long> percussionEventValues;

    AccelerationRecorder accelerationRecorder;
    //AccelerationChartManager accelerationChartManager;

    RotationRecorder rotationRecorder;

    MagnetRecorder magnetRecorder;

    //DBMeasurement dbMeasurement;
    //DBChartManager dbChartManager;

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer;

    private Button measurementController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        accelerationSensorValues = new ArrayList<>();
        rotationSensorValues = new ArrayList<>();
        magnetSensorValues = new ArrayList<>();
        soundEventValues = new ArrayList<>();
        percussionEventValues = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerationRecorder = new AccelerationRecorder(this);
        rotationRecorder = new RotationRecorder(this);
        magnetRecorder = new MagnetRecorder(this);
        //accelerationChartManager = new AccelerationChartManager(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        new AudioHandler(this);

        installAccelerometer();
        installGyroscope();
        installMagnetometer();

        //dbMeasurement = new DBMeasurement(this);
        //dbChartManager = new DBChartManager(this);
        //dbMeasurement.initializeDBMeasurement();
        //dbMeasurement.startRecorder();

        //audio processing
        installButton();
    }

    private void installButton() {
        measurementController = (Button) findViewById(R.id.button_send);
        measurementController.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(measurementController.getText().equals("Start")) {
                    accelerationSensorValues.clear();
                    rotationSensorValues.clear();
                    magnetSensorValues.clear();
                    soundEventValues.clear();
                    measurementController.setText(R.string.stop);
                }
                else {
                    (new DataHandler(new MeasuredData(accelerationSensorValues, rotationSensorValues,  magnetSensorValues, soundEventValues, percussionEventValues))).start();
                    measurementController.setText(R.string.start);
                }

            }
        });
    }

    /**
     * Initializes
     */
    private void installAccelerometer() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(accelerationRecorder, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            Log.d(TAG, "No Accelerometer available!");
        }
    }

    private void installGyroscope() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(rotationRecorder, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            Log.d(TAG, "No Gyroscope available!");
        }
    }

    private void installMagnetometer() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(magnetRecorder, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            Log.d(TAG, "No MagnetRecorder available!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationRecorder, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(rotationRecorder, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(magnetRecorder, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerationRecorder);
        sensorManager.unregisterListener(rotationRecorder);
        sensorManager.unregisterListener(magnetRecorder);
    }
}
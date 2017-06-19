package com.jonas.breathinganalysis;

import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

//import com.jonas.breathinganalysis.Deprecated.DBChartManager;
//import com.jonas.breathinganalysis.Deprecated.DBMeasurement;

import java.util.ArrayList;

import static java.lang.System.*;

public class BreathingAnalysis extends Activity{

    ArrayList<Acceleration> accelerationList;
    ArrayList<Rotation> rotationList;
    ArrayList<Magnet> magneticList;
    ArrayList<Sound> soundList;

    AccelerationMeasurement accelerationMeasurement;
    //AccelerationChartManager accelerationChartManager;

    GyroscopicMeasurement gyroscopicMeasurement;

    MagneticMeasurement magneticMeasurement;

    //DBMeasurement dbMeasurement;
    //DBChartManager dbChartManager;

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer;

    Button button;

    private long startTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        startTime = -1;

        //Testing of the interpolation tool:
        //DataPreprocessor dataPreprocessor = new DataPreprocessor();

        accelerationList = new ArrayList<>();
        rotationList = new ArrayList<>();
        magneticList = new ArrayList<>();
        soundList = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accelerationMeasurement = new AccelerationMeasurement(this);
        gyroscopicMeasurement = new GyroscopicMeasurement(this);
        magneticMeasurement = new MagneticMeasurement(this);
        //accelerationChartManager = new AccelerationChartManager(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        installAccelerometer();
        installGyroscope();
        installMagnetometer();

        //dbMeasurement = new DBMeasurement(this);
        //dbChartManager = new DBChartManager(this);
        //dbMeasurement.initializeDBMeasurement();
        //dbMeasurement.startRecorder();

        //audio processing
        new AudioProcessor(this);

        installButton();
    }



    private void installButton() {
        button = (Button) findViewById(R.id.button_send);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                out.println("Hallo ich bin ein Button");
                if(button.getText().equals("Start")) {
                    startTime = currentTimeMillis();
                    accelerationList.clear();
                    rotationList.clear();
                    magneticList.clear();
                    soundList.clear();
                    button.setText("Stop");
                }
                else {
                    //new Normalizer(startTime, accelerationList, rotationList, magneticList, soundList);
                    startTime = -1;
                    button.setText("Start");
                }

            }
        });
    }


    private void installAccelerometer() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(accelerationMeasurement, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            out.println("No Accelerometer available!");
        }
    }

    private void installGyroscope() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            // success! we have a gyroscope
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(gyroscopicMeasurement, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            out.println("No Gyroscope available!");
        }
    }

    private void installMagnetometer() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // success! we have a magnetometer
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(magneticMeasurement, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            out.println("No Magnetometer available!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationMeasurement, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(gyroscopicMeasurement, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(magneticMeasurement, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerationMeasurement);
        sensorManager.unregisterListener(gyroscopicMeasurement);
        sensorManager.unregisterListener(magneticMeasurement);
    }
}
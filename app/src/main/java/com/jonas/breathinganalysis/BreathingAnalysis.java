package com.jonas.breathinganalysis;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class BreathingAnalysis extends Activity{

    ArrayList<SensorDate> accelerationSensorValues, rotationSensorValues, magnetSensorValues;
    ArrayList<Sound> soundEventValues;
    ArrayList<Long> percussionEventValues;

    AccelerationRecorder accelerationRecorder;
    RotationRecorder rotationRecorder;
    MagnetRecorder magnetRecorder;

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer;

    private Button measurementController;

    SoundPool soundPool;
    int tick, tock;
    Handler handler;
    Metronome metronome;
    long bestFittingStartTimestamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerationSensorValues = new ArrayList<>();
        rotationSensorValues = new ArrayList<>();
        magnetSensorValues = new ArrayList<>();
        soundEventValues = new ArrayList<>();
        percussionEventValues = new ArrayList<>();

        accelerationRecorder = new AccelerationRecorder(this);
        rotationRecorder = new RotationRecorder(this);
        magnetRecorder = new MagnetRecorder(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        initializeAndRegisterAccelerometer();
        initializeAndRegisterGyroscope();
        initializeAndRegisterMagnetometer();

        new AudioHandler(this);

        soundPool = new SoundPool.Builder().build();
        tick = soundPool.load(this, R.raw.tick,1);
        tock = soundPool.load(this, R.raw.tock,1);
        handler = new Handler();
        metronome = new Metronome(soundPool, tick, tock, handler, this, 80, 3, 5);
        bestFittingStartTimestamp = 0;

        installButton();
    }

    private void installButton() {
        measurementController = (Button) findViewById(R.id.button_send);
        measurementController.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(measurementController.getText().equals("Start")) {
                    handler.post(metronome);

                    accelerationSensorValues.clear();
                    rotationSensorValues.clear();
                    magnetSensorValues.clear();
                    soundEventValues.clear();
                    measurementController.setText(R.string.stop);
                }
                else {
                    metronome.reset();
                    handler.removeCallbacks(metronome);
                    measurementController.setText(R.string.start);
                }

            }
        });
    }


    void setBestFittingStartTimestamp(long bestFittingStartTimestamp) {
        (new DataHandler(new MeasuredData(accelerationSensorValues, rotationSensorValues,  magnetSensorValues, soundEventValues, percussionEventValues, bestFittingStartTimestamp))).start();
        measurementController.performClick();
    }


    private void initializeAndRegisterAccelerometer() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(accelerationRecorder, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            Log.d(TAG, "No Accelerometer available!");
        }
    }

    private void initializeAndRegisterGyroscope() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(rotationRecorder, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            Log.d(TAG, "No Gyroscope available!");
        }
    }

    private void initializeAndRegisterMagnetometer() {
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
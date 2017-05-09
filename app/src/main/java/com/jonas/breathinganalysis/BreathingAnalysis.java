package com.jonas.breathinganalysis;

import android.os.Build;
import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.widget.TextView;
import android.util.Log;

import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;



public class BreathingAnalysis extends Activity implements SensorEventListener, OnChartValueSelectedListener {
    AccelerationChartManager accelerationChartManager;
    DBChartManager dbChartManager;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private float currentXF = 0;
    private float currentYF = 0;
    private float currentZF = 0;

    private float currentDB = 0;

    private TextView currentX, currentY, currentZ;

    Thread runner;
    final Runnable updater = new Runnable(){

        public void run(){
            dbChartManager.updateTv();
        }
    };
    final Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        accelerationChartManager = new AccelerationChartManager(this, (LineChart) findViewById(R.id.accelerationChartDisplay));
        accelerationChartManager.initializeAccelerationChart();
        dbChartManager = new DBChartManager(this, (LineChart) findViewById(R.id.dBChartDisplay));
        initializeDBMeasurement();
        dbChartManager.startRecorder();
        dbChartManager.initializeDBChart();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            // fai! we don't have an accelerometer!
        }
    }

    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void initializeDBMeasurement() {
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(100);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }
    }



    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }



    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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
        accelerationChartManager.addAccelerationEntry();
    }

    public float getCurrentYValue() {
        return currentYF;
    }

    public float getCurrentDB() {
        return currentDB;
    }

    public void setCurrentDB(float newDB) {
        currentDB = newDB;
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(currentXF));
        currentY.setText(Float.toString(currentYF));
        currentZ.setText(Float.toString(currentZF));
    }
}
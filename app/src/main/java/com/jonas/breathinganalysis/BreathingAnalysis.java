package com.jonas.breathinganalysis;

import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.github.mikephil.charting.highlight.Highlight;

import android.widget.TextView;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.utils.ColorTemplate;


import android.media.MediaRecorder;
import android.os.Handler;

import android.os.Build;

import java.util.List;
import java.util.ArrayList;

public class BreathingAnalysis extends Activity implements SensorEventListener, OnChartValueSelectedListener {

    MediaRecorder mRecorder;
    Thread runner;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;

    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        };
    };
    final Handler mHandler = new Handler();

    LineChart lineChart1, lineChart2;


    private List<Float> y;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        y = new ArrayList<Float>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeAccelerationChart();
        initializeDBMeasurement();
        startRecorder();
        initializeDBChart();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
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

    public void startRecorder(){


        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }

            //mEMA = 0.0;
        }

    }

    public void updateTv(){
        //System.out.println(Double.toString((getAmplitudeEMA())));
        currentDB = Float.parseFloat(Double.toString((getAmplitudeEMA())));
        addDBEntry();
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
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

    public void initializeAccelerationChart() {
        lineChart1 = (LineChart) findViewById(R.id.lineChartDisplay1);

        ArrayList<Entry> yAxesYAcceleration = new ArrayList<>();
        float xEntry = Float.parseFloat("0");
        yAxesYAcceleration.add(new Entry(xEntry,0f));
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(yAxesYAcceleration,"y-Axes-Acceleration");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSets.add(lineDataSet1);
        lineChart1.setData(new LineData(lineDataSets));
        lineChart1.setVisibleXRangeMaximum(65f);
        lineChart1.invalidate();
    }

    private void addAccelerationEntry() {
        LineData data = lineChart1.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), currentYF), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            lineChart1.notifyDataSetChanged();

            // limit the number of visible entries
            lineChart1.setVisibleXRangeMaximum(120);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            lineChart1.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private void addDBEntry() {
        LineData data = lineChart2.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), currentDB), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            lineChart2.notifyDataSetChanged();

            // limit the number of visible entries
            lineChart2.setVisibleXRangeMaximum(120);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            lineChart2.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    public void initializeDBChart() {
        float value = Float.parseFloat(Double.toString((getAmplitudeEMA())));

        lineChart2 = (LineChart) findViewById(R.id.lineChartDisplay2);
        ArrayList<Entry> yAxesDB = new ArrayList<>();
        float xEntry = Float.parseFloat("0");
        yAxesDB.add(new Entry(xEntry,0f));

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(yAxesDB,"y-Axes-dB");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSets.add(lineDataSet1);
        lineChart2.setData(new LineData(lineDataSets));
        lineChart2.setVisibleXRangeMaximum(65f);
        lineChart2.invalidate();
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
            y.add(currentYF);
        }
        if (Math.abs(lastZ - currentZF) < 0.01)
            currentZF = lastZ;

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        displayCurrentValues();
        addAccelerationEntry();
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
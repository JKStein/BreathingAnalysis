package com.jonas.breathinganalysis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Measures, stores and displays all available sensor data of a particular sensor.
 * @author Jonas Stein
 */
public class SensorRecorder extends Recorder implements SensorEventListener{

    /**
     * The name of the Sensor.
     */
    private String sensorName;
    /**
     * The amount of nanoseconds in one millisecond.
     */
    private static final long NANOSECONDS_PER_MILLISECOND = 1000000L;
    /**
     * The name a sensor will be called if none is supplied.
     */
    private static final String DEFAULT_SENSOR_NAME = "Unnamed Sensor";
    /**
     * The {@link android.widget.TextView TextViews} illustrating the sensor values.
     */
    private TextView xAxis, yAxis, zAxis;


    public static SensorRecorder newInstance(String sensorName) {
        SensorRecorder sensorRecorder = new SensorRecorder();
        Bundle bundle = new Bundle();
        bundle.putString("sensorName", sensorName);
        sensorRecorder.setArguments(bundle);
        return sensorRecorder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this sensor_fragment
        final View view = inflater.inflate(R.layout.sensor_fragment, container, false);
        xAxis = (TextView) view.findViewById(R.id.xAxisSensor);
        yAxis = (TextView) view.findViewById(R.id.yAxisSensor);
        zAxis = (TextView) view.findViewById(R.id.zAxisSensor);
        ((TextView) view.findViewById(R.id.sensorName)).setText(this.sensorName);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.sensorName = getArguments().getString("sensorName", DEFAULT_SENSOR_NAME);
        }
        else {
            this.sensorName = DEFAULT_SENSOR_NAME;
        }
    }

    /**
     * Updates the {@link android.widget.TextView TextViews} illustrating the current sensor
     * values and adds the new data to the sensorData {@link java.util.ArrayList}.
     * Gets called when there is a new {@link android.hardware.SensorEvent}.
     * @param event The {@link android.hardware.SensorEvent} that has been sent,
     * containing the newly captured sensor values.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Display the new sensor values.

        //Float.toString() could not work properly, because the separator (dot or comma) is unknown.
        xAxis.setText(String.format(Locale.US, "%f", event.values[0]));
        yAxis.setText(String.format(Locale.US, "%f", event.values[1]));
        zAxis.setText(String.format(Locale.US, "%f", event.values[2]));

        final float[] values = event.values.clone();
        final long timestamp = event.timestamp / NANOSECONDS_PER_MILLISECOND;

        //Add new values to the series of measurement if recording.
        if(isRecording()) {
            //event.timestamp yields the timestamp of the SensorEvent in nanoseconds,
            // but overall measurement is based on milliseconds.
            getSensorData().add(new SensorDate(timestamp, values));
        }
    }

    /**
     * Logs the change of accuracy.
     * Called when the accuracy of a {@link android.hardware.Sensor} changed.
     * @param sensor The ID of the sensor being monitored.
     * @param accuracy The new accuracy of this sensor.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (accuracy) {
            case 0:
                Log.d(TAG, "The accuracy of the " + this.sensorName + " has changed to: unreliable");
            case 1:
                Log.d(TAG, "The accuracy of the " + this.sensorName + " has changed to: low");
            case 2:
                Log.d(TAG, "The accuracy of the " + this.sensorName + " has changed to: medium");
            case 3:
                Log.d(TAG, "The accuracy of the " + this.sensorName + " has changed to: high");
        }
    }
}

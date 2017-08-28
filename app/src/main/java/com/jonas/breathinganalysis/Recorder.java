package com.jonas.breathinganalysis;

import android.app.Fragment;

import java.util.ArrayList;

/**
 * @author Jonas Stein
 */

abstract class Recorder extends Fragment {

    /**
     * The values collected by the sensor.
     */
    private ArrayList<SensorDate> sensorData;
    /**
     * Only if this attribute is true, the measured values will be stored.
     */
    private boolean recording;


    /**
     * Instantiates all needed attributes.
     */
    Recorder() {
        this.sensorData = new ArrayList<>();
        this.recording = false;
    }


    /**
     * Getter for the {@link java.util.ArrayList} containing the captured sensor data.
     * @return An {@link java.util.ArrayList} containing the captured sensor data.
     */
    ArrayList<SensorDate> getSensorData() {
        return this.sensorData;
    }


    /**
     * Starts the scoring of all measured sensor data.
     */
    void startRecording() {
        this.recording = true;
    }

    /**
     * Stop the scoring of all measured sensor data.
     */
    void stopRecording() {
        this.recording = false;
    }

    /**
     * Enables storing a new series of measurement by deleting all old data.
     */
    void clearSensorData() {
        sensorData.clear();
    }

    /**
     * Getter for recording.
     * @return recording The boolean value representing weather the Sensor should record its values or not.
     */
    boolean isRecording() {
        return recording;
    }
}

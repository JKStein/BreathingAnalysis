package com.jonas.breathinganalysis;

/**
 * @author Jonas Stein
 */

class ListEntry {

    private static final String DEFAULT_SENSOR_VALUE = "0.0";
    private String sensorEntryName;
    private String sensorValue;

    ListEntry(String sensorEntryName) {
        this.sensorEntryName = sensorEntryName;
        this.sensorValue = DEFAULT_SENSOR_VALUE;
    }

    String getSensorValue() {
        return sensorValue;
    }

    void setSensorValue(String sensorValue) {
        this.sensorValue = sensorValue;
    }

    String getSensorEntryName() {
        return sensorEntryName;
    }

    @Override
    public String toString() {
        return ("sensor entry name = " + sensorEntryName + " | sensor value = " + sensorValue);
    }
}
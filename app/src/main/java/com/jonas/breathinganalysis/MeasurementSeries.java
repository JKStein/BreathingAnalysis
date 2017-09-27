package com.jonas.breathinganalysis;

import java.util.ArrayList;

/**
 * A series of measurements of a specific sensor.
 * @author Jonas Stein
 */
class MeasurementSeries {
    private ArrayList<SensorDate> sensorData;
    private String[] sensorEntryNames;

    /**
     * Instantiates this object's attributes.
     * @param sensorData An ArrayList containing all recorded SensorDates of a specific sensor.
     * @param sensorEntryNames The names of the entries of a specific sensor.
     * @throws IllegalArgumentException If the amount of values in the SensorDate ArrayList is not
     *                                  equal to the length of the sensorEntryNames String array.
     *                                  This might indicate, that 'sensorData' and 'sensorEntryNames'
     *                                  are from different sensors.
     */
    MeasurementSeries(ArrayList<SensorDate> sensorData, String[] sensorEntryNames) throws IllegalArgumentException{
        this.sensorData = sensorData;

        if(sensorData.size() == 0 || sensorData.get(0) != null &&
                sensorData.get(0).getValues().length == sensorEntryNames.length) {
            this.sensorEntryNames = sensorEntryNames;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    ArrayList<SensorDate> getSensorData() {
        return sensorData;
    }

    String[] getSensorEntryNames() {
        return sensorEntryNames;
    }

    /**
     * Checks if this object's sensor data contains an entry with the passed timestamp,
     * if it does, the values get returned as a String array. If it does not an empty
     * String array of the correct length is returned.
     * @param timestamp The timestamp to be checked.
     * @return A string array containing the values recorded by the sensor at the passed timestamp.
     */
    String[] contains(long timestamp) {
        for(int i = 0; i < this.sensorData.size(); i++) {
            SensorDate sensorDate = this.sensorData.get(i);
            long foundTimestamp = sensorDate.getTimestamp();
            if(foundTimestamp == timestamp) {
                String[] result = sensorDate.valuesToString().clone();
                this.sensorData.remove(sensorDate);
                return result;
            }
            else if(foundTimestamp > timestamp) {
                return new String[sensorEntryNames.length];
            }
        }
        return new String[sensorEntryNames.length];
    }
}